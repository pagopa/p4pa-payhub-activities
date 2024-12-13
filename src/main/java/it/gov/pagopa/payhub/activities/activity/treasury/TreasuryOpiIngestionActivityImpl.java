package it.gov.pagopa.payhub.activities.activity.treasury;

import it.gov.pagopa.payhub.activities.dao.FlussoTesoreriaPIIDao;
import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import it.gov.pagopa.payhub.activities.dao.TreasuryDao;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.*;
import it.gov.pagopa.payhub.activities.enums.IngestionFlowFileType;
import it.gov.pagopa.payhub.activities.exception.ActivitiesException;
import it.gov.pagopa.payhub.activities.exception.IngestionFlowFileNotFoundException;

import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;

import it.gov.pagopa.payhub.activities.service.treasury.TreasuryOpi14MapperService;
import it.gov.pagopa.payhub.activities.service.treasury.TreasuryOpi161MapperService;
import it.gov.pagopa.payhub.activities.service.treasury.TreasuryUnmarshallerService;
import it.gov.pagopa.payhub.activities.service.treasury.TreasuryValidatorService;
import it.gov.pagopa.payhub.activities.util.CsvUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


@Slf4j
@Lazy
@Component
public class TreasuryOpiIngestionActivityImpl implements TreasuryOpiIngestionActivity {

    private final IngestionFlowFileType ingestionflowFileType;
    private final IngestionFlowFileDao ingestionFlowFileDao;
    private final String archiveDirectory;
    private final TreasuryDao treasuryDao;
    private final FlussoTesoreriaPIIDao flussoTesoreriaPIIDao;
    private final IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService;
    private final TreasuryUnmarshallerService treasuryUnmarshallerService;
    private final TreasuryOpi14MapperService treasuryOpi14MapperService;
    private final TreasuryOpi161MapperService treasuryOpi161MapperService;
    private final TreasuryValidatorService treasuryValidatorService;
    private final IngestionFlowFileArchiverService ingestionFlowFileArchiverService;

    public TreasuryOpiIngestionActivityImpl(
            IngestionFlowFileDao ingestionFlowFileDao, TreasuryDao treasuryDao, FlussoTesoreriaPIIDao flussoTesoreriaPIIDao,
            IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService,
            TreasuryUnmarshallerService treasuryUnmarshallerService,
            TreasuryOpi14MapperService treasuryOpi14MapperService, TreasuryOpi161MapperService treasuryOpi161MapperService,
            TreasuryValidatorService treasuryValidatorService, IngestionFlowFileArchiverService ingestionFlowFileArchiverService,
            String archiveDirectory) {
        this.archiveDirectory = archiveDirectory;
        this.ingestionFlowFileArchiverService = ingestionFlowFileArchiverService;
        this.ingestionflowFileType = IngestionFlowFileType.OPI;
        this.ingestionFlowFileDao = ingestionFlowFileDao;
        this.treasuryDao = treasuryDao;
        this.flussoTesoreriaPIIDao = flussoTesoreriaPIIDao;
        this.ingestionFlowFileRetrieverService = ingestionFlowFileRetrieverService;
        this.treasuryUnmarshallerService = treasuryUnmarshallerService;
        this.treasuryOpi14MapperService = treasuryOpi14MapperService;
        this.treasuryOpi161MapperService = treasuryOpi161MapperService;
        this.treasuryValidatorService = treasuryValidatorService;
    }


    @Override
    public TreasuryIufResult processFile(Long ingestionFlowFileId) {
        List<String> iufIuvList = new ArrayList<>();
        List<Path> ingestionFlowFiles = new ArrayList<>();
        IngestionFlowFileDTO ingestionFlowFileDTO = null;
        AtomicReference<TreasuryIufResult> treasuryIufResult = new AtomicReference<>();

        try {
            ingestionFlowFileDTO = findIngestionFlowFileRecord(ingestionFlowFileId);

            ingestionFlowFiles = retrieveFiles(ingestionFlowFileDTO);


            if (ingestionFlowFiles != null && !ingestionFlowFiles.isEmpty()) {

                IngestionFlowFileDTO finalIngestionFlowFileDTO = ingestionFlowFileDTO;
                List<Path> finalIngestionFlowFiles = ingestionFlowFiles;
                ingestionFlowFiles.forEach(path -> {
                    File ingestionFlowFile = path.toFile();
                    log.debug("file from zip archive with name {} loaded successfully ", ingestionFlowFile.getName());

                    treasuryIufResult.set(parseData(ingestionFlowFile, finalIngestionFlowFileDTO, finalIngestionFlowFiles.size()));
                    try {
                        archive(finalIngestionFlowFileDTO);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }


                });
            }
        } catch (Exception e) {
            log.error("Error during TreasuryOpiIngestionActivity ingestionFlowFileId {}", ingestionFlowFileId, e);
            return new TreasuryIufResult(Collections.emptyList(), false, e.getMessage());
        }
        return treasuryIufResult.get();
    }

    private IngestionFlowFileDTO findIngestionFlowFileRecord(Long ingestionFlowFileId) {
        IngestionFlowFileDTO ingestionFlowFileDTO = ingestionFlowFileDao.findById(ingestionFlowFileId)
                .orElseThrow(() -> new IngestionFlowFileNotFoundException("Cannot found ingestionFlow having id: " + ingestionFlowFileId));
        if (!ingestionFlowFileDTO.getFlowFileType().equals(ingestionflowFileType)) {
            throw new IllegalArgumentException("invalid ingestionFlow file type " + ingestionFlowFileDTO.getFlowFileType());
        }
        return ingestionFlowFileDTO;
    }

    private List<Path> retrieveFiles(IngestionFlowFileDTO ingestionFlowFileDTO) throws IOException {

        return ingestionFlowFileRetrieverService
                .retrieveAndUnzipFile(Path.of(ingestionFlowFileDTO.getFilePath()), ingestionFlowFileDTO.getFileName());
    }

    private TreasuryIufResult parseData(File ingestionFlowFile, IngestionFlowFileDTO finalIngestionFlowFileDTO, int zipFileSize) {
        Map<String, List<Pair<TreasuryDTO, FlussoTesoreriaPIIDTO>>> treasuryDtoMap = null;
        String versione = null;
        Set<String> iufList = new HashSet<>();
        boolean success = true;

        it.gov.pagopa.payhub.activities.xsd.treasury.opi14.FlussoGiornaleDiCassa flussoGiornaleDiCassa14 = null;
        it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa flussoGiornaleDiCassa161 = null;

        try {
            flussoGiornaleDiCassa161 = treasuryUnmarshallerService.unmarshalOpi161(ingestionFlowFile);
            log.debug("file flussoGiornaleDiCassa with Id {} parsed successfully ", flussoGiornaleDiCassa161.getId());
        } catch (Exception e) {
            log.error("file flussoGiornaleDiCassa parsing error with opi 1.6.1 format {} ", e.getMessage());
        }
        if (flussoGiornaleDiCassa161 == null) {
            try {
                flussoGiornaleDiCassa14 = treasuryUnmarshallerService.unmarshalOpi14(ingestionFlowFile);
                log.debug("file flussoGiornaleDiCassa with Id {} parsed successfully ", flussoGiornaleDiCassa14.getId());
                versione = TreasuryValidatorService.v14;
            } catch (Exception e) {
                log.error("file flussoGiornaleDiCassa parsing error with opi 1.4 format {} ", e.getMessage());
                success = false;
            }
        } else
            versione = TreasuryValidatorService.v161;

        assert versione != null;
//        if (!treasuryValidatorService.validatePageSize(flussoGiornaleDiCassa14, flussoGiornaleDiCassa161, zipFileSize, versione)) {
//          log.error("invalid total page number for ingestionFlowFile with name {}", ingestionFlowFile.getName());
//          throw new RuntimeException("invalid total page number for ingestionFlowFile with name " + ingestionFlowFile.getName() + " versione " + versione);
//        }


        List<TreasuryErrorDTO> errorDTOList = treasuryValidatorService.validateData(flussoGiornaleDiCassa14, flussoGiornaleDiCassa161, ingestionFlowFile, versione);

        String[] headerArray= new String[]{"FileName","Anno Bolletta", "Codice Bolletta", "Error Code", "Error Message"};
        List<String[]> header = new ArrayList<>(List.of());
        header.add(headerArray);

        List<String[]> data = errorDTOList.stream()
                .map(errorDTO -> new String[] {
                        errorDTO.getNomeFile(),
                        errorDTO.getDeAnnoBolletta(),
                        errorDTO.getCodBolletta(),
                        errorDTO.getErrorCode(),
                        errorDTO.getErrorMessage()
                })
                .collect(Collectors.toList());


        try {
            CsvUtils.createCsv("ciao", header,data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        treasuryDtoMap = switch (versione) {
            case TreasuryValidatorService.v14 ->
                    treasuryOpi14MapperService.apply(flussoGiornaleDiCassa14, finalIngestionFlowFileDTO);
            case TreasuryValidatorService.v161 ->
                    treasuryOpi161MapperService.apply(flussoGiornaleDiCassa161, finalIngestionFlowFileDTO);
            default -> treasuryDtoMap;
        };

        List<Pair<TreasuryDTO, FlussoTesoreriaPIIDTO>> pairs = treasuryDtoMap.get(StringUtils.firstNonBlank(TreasuryOpi161MapperService.insert, TreasuryOpi14MapperService.insert));
        pairs.forEach(pair -> {
            long idFlussoTesoreriaPiiId = flussoTesoreriaPIIDao.insert(pair.getRight());
            TreasuryDTO treasuryDTO = pair.getLeft();
            treasuryDTO.setPersonalDataId(idFlussoTesoreriaPiiId);
            treasuryDao.insert(treasuryDTO);
            iufList.add(treasuryDTO.getCodIdUnivocoFlusso());
        });

        return new TreasuryIufResult(iufList.stream().toList(), success, null);
    }

    /**
     * Archives the file specified in the given {@link IngestionFlowFileDTO}. The file is moved to
     * the archive directory located within the same file path.
     *
     * @param ingestionFlowFileDTO the DTO containing details of the file to be archived.
     * @throws IOException if an error occurs during file movement or directory creation.
     */
    private void archive(IngestionFlowFileDTO ingestionFlowFileDTO) throws IOException {
        Path originalFilePath = Paths.get(ingestionFlowFileDTO.getFilePath(), ingestionFlowFileDTO.getFileName());
        Path targetDirectory = Paths.get(ingestionFlowFileDTO.getFilePath(), archiveDirectory);
        ingestionFlowFileArchiverService.archive(List.of(originalFilePath), targetDirectory);
    }

    /**
     * Archives an error file to a specified target directory.
     * This method takes an error file and moves it to a target directory for archiving. It constructs
     * the original file path and the target directory path, then invokes the {@link IngestionFlowFileArchiverService}
     * to perform the archiving operation.
     *
     * @param errorFile the error file to be archived. This file is moved from its original location to the target directory.
     * @param targetDir the directory where the error file should be archived. The target directory path is constructed relative
     *                  to the parent directory of the error file.
     * @throws IOException if an I/O error occurs while archiving the file, such as issues with reading, writing, or accessing file paths.
     */
    private void archiveErrorFile(File errorFile, String targetDir) throws IOException {
        Path originalFilePath = Paths.get(errorFile.getParent(), errorFile.getName());
        Path targetDirectory = Paths.get(errorFile.getParent(), targetDir);
        ingestionFlowFileArchiverService.archive(List.of(originalFilePath), targetDirectory);
    }


    /**
     * Delete the specified file if not null.
     *
     * @param file2Delete the file to delete.
     * @throws IOException if an error occurs during deletion.
     */
    private void deletion(File file2Delete) {
        if (file2Delete != null) {
            try {
                Files.delete(file2Delete.toPath());
            } catch (IOException e) {
                throw new ActivitiesException("Error occured while delete file: " + file2Delete);
            }
        }
    }


}
