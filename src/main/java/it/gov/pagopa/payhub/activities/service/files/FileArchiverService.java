package it.gov.pagopa.payhub.activities.service.files;

import it.gov.pagopa.payhub.activities.util.AESUtils;
import it.gov.pagopa.payhub.activities.util.FileShareUtils;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Service class responsible for handling archiving ingestion files,
 * including zipping and ecryption files.
 */
@Lazy
@Service
public class FileArchiverService {

    private final Path sharedDirectoryPath;
    private final String archiveFolder;
    /**
     * The password used for encrypting files.
     */
    private final String dataCipherPsw;

    private final ZipFileService zipFileService;

    public FileArchiverService(
            @Value("${folders.shared}") String sharedFolder,
            @Value("${folders.process-target-sub-folders.archive}") String archiveFolder,
            @Value("${cipher.file-encrypt-psw}") String dataCipherPsw,

            ZipFileService zipFileService
    ) {
        this.sharedDirectoryPath = Path.of(sharedFolder);
        this.archiveFolder = archiveFolder;
        this.dataCipherPsw = dataCipherPsw;
        this.zipFileService = zipFileService;

        if (!Files.exists(sharedDirectoryPath)) {
            throw new IllegalStateException("Shared folder doesn't exist: " + sharedDirectoryPath);
        }
    }

    public void compressAndArchive(Path errorFilePath, Path targetDirectory) throws IOException {
        Path tmpZipFilePath = errorFilePath
                .getParent()
                .resolve(errorFilePath.getFileName()+".zip");
        compressAndArchive(List.of(errorFilePath), tmpZipFilePath, targetDirectory);
    }

    /**
     * Compresses the specified list of files into a single archive, encrypts the resulting file, and
     * moves it to the target directory. Original and intermediate files created during the process are cleaned up.
     *
     * @param files2Archive the list of files to be compressed and encrypted.
     * @param file2Zip      the path of the temporary output file used for compression.
     * @param targetPath    the destination path where the encrypted archive will be saved.
     * @throws IOException if an error occurs during compression, encryption, file copying, or cleanup.
     */
    public Long compressAndArchive(List<Path> files2Archive, Path file2Zip, Path targetPath) throws IOException {
        File zipped = zipFileService.zipper(file2Zip, files2Archive);
        Long zippedFileSize = zipped.length();
        File encrypted = AESUtils.encrypt(dataCipherPsw, zipped);
        Files.delete(zipped.toPath());
        for (Path path : files2Archive) {
            Files.deleteIfExists(path);
        }
        archive(List.of(encrypted.toPath()), targetPath);
        return zippedFileSize;
    }

    /**
     * Archives the file specified in the given {@link IngestionFlowFile}. The file is moved to
     * the archive directory located within the same file path.
     *
     * @param ingestionFlowFileDTO the DTO containing details of the file to be archived.
     */
    public void archive(IngestionFlowFile ingestionFlowFileDTO) {
        Path originalFileFolder = FileShareUtils.buildOrganizationBasePath(sharedDirectoryPath,ingestionFlowFileDTO.getOrganizationId())
                .resolve(ingestionFlowFileDTO.getFilePathName());

        Path originalFilePath = originalFileFolder
                .resolve(ingestionFlowFileDTO.getFileName() + AESUtils.CIPHER_EXTENSION);

        Path targetDirectory = originalFileFolder.resolve(archiveFolder);

        archive(List.of(originalFilePath), targetDirectory);
    }

    /**
     * Moves the specified list of files to the target directory and removes the original files after the move.
     * Creates the target directory if it does not exist.
     *
     * @param files2Archive the list of files to move to the target directory.
     * @param targetPath    the directory where the files will be moved.
     */
    public void archive(List<Path> files2Archive, Path targetPath) {
        try {
            Files.createDirectories(targetPath);
            for (Path file : files2Archive) {
                Files.copy(file, targetPath.resolve(file.getFileName()), REPLACE_EXISTING);
                Files.deleteIfExists(file);
            }
        } catch (IOException e){
            throw new IllegalStateException("Cannot archive files: " + files2Archive + " into destination: " + targetPath, e);
        }
    }
}

