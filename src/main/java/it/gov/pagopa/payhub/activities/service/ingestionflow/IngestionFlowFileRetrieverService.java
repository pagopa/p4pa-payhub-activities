package it.gov.pagopa.payhub.activities.service.ingestionflow;

import it.gov.pagopa.payhub.activities.service.FileValidatorService;
import it.gov.pagopa.payhub.activities.service.ZipFileService;
import it.gov.pagopa.payhub.activities.util.AESUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Service class responsible for handling encrypted ingestion files,
 * including decryption, validation, and extraction of ZIP files.
 */
@Lazy
@Slf4j
@Service
public class IngestionFlowFileRetrieverService {

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

    public IngestionFlowFileRetrieverService(
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
            throw new IllegalStateException("Shared folder doesn't exist!");
        }
        if (!Files.exists(tempDirectoryPath)) {
            throw new IllegalStateException("Temp folder doesn't exist!");
        }
    }

    /**
     * Handles the setup process for an ingestion file by performing the following steps:
     * <ul>
     *     <li>Decrypts the file using AES encryption.</li>
     *     <li>Validates if the file is a valid ZIP archive.</li>
     *     <li>Extracts the contents of the ZIP file to a temporary directory.</li>
     * </ul>
     *
     * @param sourcePath the relative path to the directory containing the file.
     * @param filename   the name of the file to process.
     * @return the path to the extracted file.
     * @throws IOException if any file operation fails during the setup process.
     */
    public List<Path> retrieveAndUnzipFile(Long organizationId, Path sourcePath, String filename) throws IOException {
        String organizationFolder = String.valueOf(organizationId);
        Path encryptedFilePath = sharedDirectoryPath
                .resolve(organizationFolder)
                .resolve(sourcePath)
                .resolve(filename);

        fileValidatorService.validateFile(encryptedFilePath);

        Path workingPath = tempDirectoryPath
                .resolve(organizationFolder)
                .resolve(sourcePath.subpath(0, sourcePath.getNameCount()));
        Files.createDirectories(workingPath);

        String filenameNoCipher = filename.replace(AESUtils.CIPHER_EXTENSION, "");
        Path zipFilePath = workingPath.resolve(filenameNoCipher);

        log.debug("Decrypting file: {}", encryptedFilePath);
        AESUtils.decrypt(dataCipherPsw,
                encryptedFilePath.toFile(),
                zipFilePath.toFile());

        log.debug("Validating ZIP file: {}", zipFilePath);
        fileValidatorService.isArchive(zipFilePath);

        log.debug("Unzipping files in : {}", zipFilePath);
        List<Path> unzippedPaths = zipFileService.unzip(zipFilePath);

        log.debug("File process completed successfully for: {}", filenameNoCipher);
        return unzippedPaths;
    }
}
