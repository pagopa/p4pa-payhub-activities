package it.gov.pagopa.payhub.activities.service.ingestionflow.sendnotification;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import it.gov.pagopa.payhub.activities.config.FoldersPathsConfig;
import it.gov.pagopa.payhub.activities.util.AESUtils;
import it.gov.pagopa.payhub.activities.util.FileShareUtils;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
@Slf4j
public class SendNotificationFileHandlerService {

  private final String dataCipherPsw;
  private final FoldersPathsConfig foldersPathsConfig;

  public SendNotificationFileHandlerService(
      @Value("${cipher.file-encrypt-psw}") String dataCipherPsw,
      FoldersPathsConfig foldersPathsConfig) {
    this.dataCipherPsw = dataCipherPsw;
    this.foldersPathsConfig = foldersPathsConfig;
  }

  /**
   * Move and encrypt all files from source to the target directory.
   * Creates the target directory if it does not exist.
   * @param organizationId organizationId
   * @param sendNotificationId sendNotificationId
   * @param sourceDirPath the directory where files are.
   */
  public void moveAllFilesToSendFolder(Long organizationId, String sendNotificationId, String sourceDirPath) {
    try {
      Path sourceDir = FileShareUtils.buildOrganizationBasePath(foldersPathsConfig.getShared(), organizationId).resolve(sourceDirPath);
      Path targetDir = FileShareUtils.buildOrganizationBasePath(foldersPathsConfig.getShared(), organizationId)
          .resolve(foldersPathsConfig.getPaths().getSendFileFolder().concat("/"+sendNotificationId));

      if (!Files.exists(targetDir)) {
        Files.createDirectories(targetDir);
      }


      try (DirectoryStream<Path> stream = Files.newDirectoryStream(sourceDir)) {
        for (Path file : stream) {
          Path targetFile = targetDir.resolve(sendNotificationId + "_" + file.getFileName());
          Files.copy(file, targetFile, REPLACE_EXISTING);
          AESUtils.encrypt(dataCipherPsw, targetFile.toFile());
          //Files.deleteIfExists(targetFile);
        }
      }


    } catch (IOException e){
      throw new IllegalStateException("Cannot archive files from: " + sourceDirPath + " into destination", e);
    }
  }
}
