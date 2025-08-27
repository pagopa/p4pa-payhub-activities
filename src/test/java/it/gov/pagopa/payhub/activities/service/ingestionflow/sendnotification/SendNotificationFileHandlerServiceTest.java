package it.gov.pagopa.payhub.activities.service.ingestionflow.sendnotification;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.gov.pagopa.payhub.activities.config.FoldersPathsConfig;
import it.gov.pagopa.payhub.activities.util.FileShareUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SendNotificationFileHandlerServiceTest {

  @Mock
  private FoldersPathsConfig foldersPathsConfigMock;
  private FoldersPathsConfig.FoldersPaths foldersPaths;
  @TempDir
  private Path tmpDir;

  private SendNotificationFileHandlerService service;



  @BeforeEach
  void setUp() {
    foldersPathsConfigMock = mock(FoldersPathsConfig.class);
    foldersPaths = FoldersPathsConfig.FoldersPaths.builder()
        .sendFileFolder("send/path")
        .build();
    when(foldersPathsConfigMock.getShared()).thenReturn(tmpDir);
    when(foldersPathsConfigMock.getPaths()).thenReturn(foldersPaths);
    service = new SendNotificationFileHandlerService("PSW", tmpDir.toString(), foldersPathsConfigMock);
  }

  @AfterEach
  void verifyNoMoreInteractions(){
    Mockito.verifyNoMoreInteractions(foldersPathsConfigMock);
  }

  @Test
  void whenMoveAllFilesToSendFolderThenVerifyFileExists() throws IOException {
    Long organizationId = 1L;
    String sourceDirPath = "data/send_notification";
    String sendNotificationId = "sendNotificationId";

    Path orgBase = FileShareUtils.buildOrganizationBasePath(tmpDir, organizationId);
    Path trueSourceDir = orgBase.resolve(sourceDirPath);
    System.out.println(trueSourceDir);
    Files.createDirectories(trueSourceDir);

    Files.createFile(trueSourceDir.resolve("test.pdf"));

    Path expectedTargetDir = orgBase.resolve("send/path/" + sendNotificationId);
    Path expectedTargetFile = expectedTargetDir.resolve(sendNotificationId + "_test.pdf");

    service.moveAllFilesToSendFolder(organizationId, sendNotificationId, sourceDirPath);

    assertTrue(Files.exists(expectedTargetDir));
    assertTrue(Files.exists(expectedTargetFile));
  }

  @Test
  void whenMoveAllFilesToSendFolderThenError() {
    Long organizationId = 1L;
    String sourceDirPath = "ingest/send";
    String sendNotificationId = "sendNotificationId";

    Assertions.assertThrows(IllegalStateException.class, () ->
    service.moveAllFilesToSendFolder(organizationId, sendNotificationId, sourceDirPath));

  }


}