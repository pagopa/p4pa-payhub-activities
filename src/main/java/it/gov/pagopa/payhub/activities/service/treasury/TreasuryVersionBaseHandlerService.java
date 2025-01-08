package it.gov.pagopa.payhub.activities.service.treasury;

import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryErrorDTO;
import it.gov.pagopa.payhub.activities.enums.TreasuryOperationEnum;
import it.gov.pagopa.payhub.activities.exception.ActivitiesException;
import it.gov.pagopa.payhub.activities.exception.TreasuryOpiInvalidFileException;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileArchiverService;
import it.gov.pagopa.payhub.activities.util.CsvUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
@Slf4j
public abstract class TreasuryVersionBaseHandlerService <T> implements TreasuryVersionHandlerService{
    
    private final TreasuryMapperService<T> mapperService;
    private final TreasuryValidatorService<T> validatorService;
    private final IngestionFlowFileArchiverService ingestionFlowFileArchiverService;


    protected TreasuryVersionBaseHandlerService(TreasuryMapperService<T> mapperService, TreasuryValidatorService<T> validatorService, IngestionFlowFileArchiverService ingestionFlowFileArchiverService) {
        this.mapperService = mapperService;
        this.validatorService = validatorService;
        this.ingestionFlowFileArchiverService = ingestionFlowFileArchiverService;
    }

    abstract T unmarshall(File file);

    @Override
    public Map<TreasuryOperationEnum, List<TreasuryDTO>> handle(File input, IngestionFlowFileDTO ingestionFlowFileDTO, int size, String errorDirectory) {
        try {
            T unmarshalled = unmarshall(input);
            List<TreasuryErrorDTO> errorDTOList = validate(ingestionFlowFileDTO, size, unmarshalled);
            Map<TreasuryOperationEnum, List<TreasuryDTO>> result = mapperService.apply(unmarshalled, ingestionFlowFileDTO);
            log.debug("file flussoGiornaleDiCassa with name {} parsed successfully using mapper {} ", ingestionFlowFileDTO.getFileName(), getClass().getSimpleName());
            writeErrors(errorDTOList, input.getName(), errorDirectory);
            return result;
        } catch (Exception e) {
            log.info("file flussoGiornaleDiCassa with name {} parsing error using mapper{} ", ingestionFlowFileDTO.getFileName(), getClass().getSimpleName());
            return Collections.emptyMap();
        }
    }

    List<TreasuryErrorDTO> validate(IngestionFlowFileDTO ingestionFlowFileDTO, int size, T fGCUnmarshalled) {
        if (validatorService.validatePageSize(fGCUnmarshalled, size)) {
            throw new TreasuryOpiInvalidFileException("invalid total page number for ingestionFlowFile with name " + ingestionFlowFileDTO.getFileName());
        }
        return validatorService.validateData(fGCUnmarshalled, ingestionFlowFileDTO.getFileName());
    }

   void writeErrors(List<TreasuryErrorDTO> errorDTOList, String fileName, String errorDirectory) {

       List<String[]> data = errorDTOList.stream()
               .map(errorDTO -> new String[]{
                       errorDTO.getNomeFile(),
                       errorDTO.getDeAnnoBolletta(),
                       errorDTO.getCodBolletta(),
                       errorDTO.getErrorCode(),
                       errorDTO.getErrorMessage()
               })
               .toList();


       try {
           String[] headerArray = new String[]{"FileName", "Anno Bolletta", "Codice Bolletta", "Error Code", "Error Message"};
           List<String[]> header = new ArrayList<>(List.of());
           header.add(headerArray);

           String errorPathName = "ERROR-" + fileName;
           CsvUtils.createCsv(errorPathName, header, data);

           archiveErrorFile(new File(errorPathName), errorDirectory);

       } catch (IOException e) {
           throw new ActivitiesException(e.getMessage());
       }

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
       void archiveErrorFile(File errorFile, String targetDir) throws IOException {
           Path originalFilePath = Paths.get(errorFile.getParent(), errorFile.getName());
           Path targetDirectory = Paths.get(errorFile.getParent(), targetDir);
           ingestionFlowFileArchiverService.archive(List.of(originalFilePath), targetDirectory);
       }

}
