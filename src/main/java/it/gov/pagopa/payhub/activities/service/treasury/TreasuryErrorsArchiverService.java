package it.gov.pagopa.payhub.activities.service.treasury;

import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryErrorDTO;
import it.gov.pagopa.payhub.activities.exception.ActivitiesException;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileArchiverService;
import it.gov.pagopa.payhub.activities.util.CsvUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TreasuryErrorsArchiverService {

    private final IngestionFlowFileArchiverService ingestionFlowFileArchiverService;

    private final String tempDirectory;
    private final String errorDirectory;

    public TreasuryErrorsArchiverService(IngestionFlowFileArchiverService ingestionFlowFileArchiverService,
                                         @Value("${tmp-dir:/tmp/}") String tempDirectory,
                                         @Value("${archive-relative-error-path:error/}") String archiveRelativeErrorPathDirectory) {
        this.ingestionFlowFileArchiverService = ingestionFlowFileArchiverService;
        this.tempDirectory = tempDirectory;
        this.errorDirectory = archiveRelativeErrorPathDirectory;
    }


    void writeErrors(List<TreasuryErrorDTO> errorDTOList, String fileName) {

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

            String errorPathName = tempDirectory +"ERROR-" + fileName;
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
        Path originalFilePath = Paths.get(errorFile.getParent() != null ? errorFile.getParent() : "",errorFile.getName());
        Path targetDirectory = Paths.get(errorFile.getParent() != null ? errorFile.getParent() : "", targetDir);
        ingestionFlowFileArchiverService.archive(List.of(originalFilePath), targetDirectory);
    }

}
