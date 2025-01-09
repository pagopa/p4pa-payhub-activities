package it.gov.pagopa.payhub.activities.service.treasury;

import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryErrorDTO;
import it.gov.pagopa.payhub.activities.exception.ActivitiesException;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileArchiverService;
import it.gov.pagopa.payhub.activities.service.CsvService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TreasuryErrorsArchiverService {

    private final Path sharedDirectoryPath;
    private final String errorFolder;

    private final IngestionFlowFileArchiverService ingestionFlowFileArchiverService;
    private final CsvService csvService;


    public TreasuryErrorsArchiverService(
            @Value("${folders.shared}") String sharedFolder,
            @Value("${folders.process-target-sub-folders.errors}") String errorFolder,

            IngestionFlowFileArchiverService ingestionFlowFileArchiverService, CsvService csvService
    ) {
        this.sharedDirectoryPath = Path.of(sharedFolder);
        this.errorFolder = errorFolder;
        this.ingestionFlowFileArchiverService = ingestionFlowFileArchiverService;
        this.csvService = csvService;
    }


    void writeErrors(Path workingDirectory, IngestionFlowFileDTO ingestionFlowFileDTO, List<TreasuryErrorDTO> errorDTOList) {

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

            String errorFileName = "ERROR-" + ingestionFlowFileDTO.getFileName().substring(0, ingestionFlowFileDTO.getFileName().lastIndexOf(".")) + ".csv";
            Path errorCsvFilePath = workingDirectory
                    .resolve(errorFileName);

            csvService.createCsv(errorCsvFilePath, header, data);

            archiveErrorFile(errorCsvFilePath, ingestionFlowFileDTO);

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
     * @param errorFilePath        the error file to be archived. This file is moved from its original location to the target directory.
     * @param ingestionFlowFileDTO the ingestion flow file
     * @throws IOException if an I/O error occurs while archiving the file, such as issues with reading, writing, or accessing file paths.
     */
    void archiveErrorFile(Path errorFilePath, IngestionFlowFileDTO ingestionFlowFileDTO) throws IOException {
        Path targetDirectory = sharedDirectoryPath
                .resolve(ingestionFlowFileDTO.getFilePathName())
                .resolve(errorFolder);
        ingestionFlowFileArchiverService.compressAndArchive(errorFilePath, targetDirectory);
    }

}
