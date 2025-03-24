package it.gov.pagopa.payhub.activities.service.exportflow;

import it.gov.pagopa.payhub.activities.exception.exportflow.ExportFileNotFoundException;
import it.gov.pagopa.payhub.activities.service.FileValidatorService;
import it.gov.pagopa.payhub.activities.service.ZipFileService;
import it.gov.pagopa.payhub.activities.util.FileShareUtils;
import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFile;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for handling encrypted ingestion files,
 * including decryption, validation, and extraction of ZIP files.
 */
@Lazy
@Slf4j
@Service
public class ExportFileRetrieverService {

    /**
     * the shared folder
     */
    private final Path sharedDirectoryPath;

    /**
     * The temporary directory used for working process.
     */
    private final Path tempDirectoryPath;

    /**
     * The password used for decrypting encrypted files.
     */
    private final String dataCipherPsw;

    private final FileValidatorService fileValidatorService;
    private final ZipFileService zipFileService;

    public ExportFileRetrieverService(
            @Value("${folders.shared}") String sharedFolder,
            @Value("${folders.tmp}") String tempFolder,
            @Value("${cipher.file-encrypt-psw}") String dataCipherPsw,
            FileValidatorService fileValidatorService, ZipFileService zipFileService) {
        this.sharedDirectoryPath = Path.of(sharedFolder);
        this.tempDirectoryPath = Path.of(tempFolder);
        this.dataCipherPsw = dataCipherPsw;
        this.fileValidatorService = fileValidatorService;
        this.zipFileService = zipFileService;

        if (!Files.exists(sharedDirectoryPath)) {
            throw new IllegalStateException("Shared folder doesn't exist: " + sharedDirectoryPath);
        }
        if (!Files.exists(tempDirectoryPath)) {
            throw new IllegalStateException("Temp folder doesn't exist: " + tempDirectoryPath);
        }
    }

    public Path getFilePath(ExportFile exportFile) {
        if(StringUtils.isEmpty(exportFile.getFilePathName())){
            throw new ExportFileNotFoundException("ExportFile not ready");
        }
        Path organizationBasePath = FileShareUtils.buildOrganizationBasePath(sharedDirectoryPath,
            exportFile.getOrganizationId());

        return organizationBasePath
            .resolve(exportFile.getFilePathName());
    }
}
