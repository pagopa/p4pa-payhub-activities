package it.gov.pagopa.payhub.activities.activity.ingestionflow.notice;

import it.gov.pagopa.payhub.activities.config.FoldersPathsConfig;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.IngestionFlowFileNotFoundException;
import it.gov.pagopa.payhub.activities.util.AESUtils;
import it.gov.pagopa.payhub.activities.util.FileShareUtils;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class DeleteMassiveNoticesFileActivityTest {

    @Mock
    private IngestionFlowFileService ingestionFlowFileServiceMock;
    @Mock
    private FoldersPathsConfig foldersPathsConfigMock;

    private DeleteMassiveNoticesFileActivity activity;

    private static final Path SHARED_PATH = Path.of("/shared");

    @BeforeEach
    void setUp() {
        activity = new DeleteMassiveNoticesFileActivityImpl(ingestionFlowFileServiceMock, foldersPathsConfigMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(ingestionFlowFileServiceMock, foldersPathsConfigMock);
    }

    @Test
    void givenIngestionFlowFileNotFoundWhenDeleteNoticeRetentionFileThenThrowsException() {
        Long ingestionFlowFileId = 1L;

        Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(
                IngestionFlowFileNotFoundException.class,
                () -> activity.deleteMassiveNoticesFile(ingestionFlowFileId)
        );
    }

    @Test
    void givenNoticeRetentionFileDoesNotExistWhenDeleteNoticeRetentionFileThenDoNothing() {
        Long ingestionFlowFileId = 1L;
        Long organizationId = 2L;

        IngestionFlowFile ingestionFlowFile = new IngestionFlowFile();
        ingestionFlowFile.setIngestionFlowFileId(ingestionFlowFileId);
        ingestionFlowFile.setOrganizationId(organizationId);
        ingestionFlowFile.setFilePathName("filePathName");
        ingestionFlowFile.setFileName("ingestionFile.zip");

        FoldersPathsConfig.ProcessTargetSubFolders processTargetSubFolders =
                FoldersPathsConfig.ProcessTargetSubFolders.builder()
                        .archive("archive")
                        .build();

        Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId))
                .thenReturn(Optional.of(ingestionFlowFile));
        Mockito.when(foldersPathsConfigMock.getShared())
                .thenReturn(SHARED_PATH);
        Mockito.when(foldersPathsConfigMock.getProcessTargetSubFolders())
                .thenReturn(processTargetSubFolders);

        Path expectedPath = FileShareUtils.buildOrganizationBasePath(SHARED_PATH, organizationId)
                .resolve("filePathName")
                .resolve("archive")
                .resolve("ingestionFile_notice.zip" + AESUtils.CIPHER_EXTENSION);

        try (MockedStatic<Files> filesMock = Mockito.mockStatic(Files.class)) {
            filesMock.when(() -> Files.exists(expectedPath)).thenReturn(false);

            activity.deleteMassiveNoticesFile(ingestionFlowFileId);

            filesMock.verify(() -> Files.exists(expectedPath));
            filesMock.verifyNoMoreInteractions();
        }
    }

    @Test
    void givenNoticeRetentionFileExistsWhenDeleteNoticeRetentionFileThenDeletesFile() {
        Long ingestionFlowFileId = 1L;
        Long organizationId = 2L;

        IngestionFlowFile ingestionFlowFile = new IngestionFlowFile();
        ingestionFlowFile.setIngestionFlowFileId(ingestionFlowFileId);
        ingestionFlowFile.setOrganizationId(organizationId);
        ingestionFlowFile.setFilePathName("filePathName");
        ingestionFlowFile.setFileName("ingestionFile.zip");

        FoldersPathsConfig.ProcessTargetSubFolders processTargetSubFolders =
                FoldersPathsConfig.ProcessTargetSubFolders.builder()
                        .archive("archive")
                        .build();

        Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId))
                .thenReturn(Optional.of(ingestionFlowFile));
        Mockito.when(foldersPathsConfigMock.getShared())
                .thenReturn(SHARED_PATH);
        Mockito.when(foldersPathsConfigMock.getProcessTargetSubFolders())
                .thenReturn(processTargetSubFolders);

        Path expectedPath = FileShareUtils.buildOrganizationBasePath(SHARED_PATH, organizationId)
                .resolve("filePathName")
                .resolve("archive")
                .resolve("ingestionFile_notice.zip" + AESUtils.CIPHER_EXTENSION);

        try (MockedStatic<Files> filesMock = Mockito.mockStatic(Files.class)) {
            filesMock.when(() -> Files.exists(expectedPath)).thenReturn(true);
            filesMock.when(() -> Files.delete(expectedPath)).thenAnswer(invocation -> null);

            activity.deleteMassiveNoticesFile(ingestionFlowFileId);

            filesMock.verify(() -> Files.exists(expectedPath));
            filesMock.verify(() -> Files.delete(expectedPath));
            filesMock.verifyNoMoreInteractions();
        }
    }

    @Test
    void givenDeleteFailsWhenDeleteNoticeRetentionFileThenThrowsIllegalStateException() {
        Long ingestionFlowFileId = 1L;
        Long organizationId = 2L;

        IngestionFlowFile ingestionFlowFile = new IngestionFlowFile();
        ingestionFlowFile.setIngestionFlowFileId(ingestionFlowFileId);
        ingestionFlowFile.setOrganizationId(organizationId);
        ingestionFlowFile.setFilePathName("filePathName");
        ingestionFlowFile.setFileName("ingestionFile.zip");

        FoldersPathsConfig.ProcessTargetSubFolders processTargetSubFolders =
                FoldersPathsConfig.ProcessTargetSubFolders.builder()
                        .archive("archive")
                        .build();

        Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId))
                .thenReturn(Optional.of(ingestionFlowFile));
        Mockito.when(foldersPathsConfigMock.getShared())
                .thenReturn(SHARED_PATH);
        Mockito.when(foldersPathsConfigMock.getProcessTargetSubFolders())
                .thenReturn(processTargetSubFolders);

        Path expectedPath = FileShareUtils.buildOrganizationBasePath(SHARED_PATH, organizationId)
                .resolve("filePathName")
                .resolve("archive")
                .resolve("ingestionFile_notice.zip" + AESUtils.CIPHER_EXTENSION);

        try (MockedStatic<Files> filesMock = Mockito.mockStatic(Files.class)) {
            filesMock.when(() -> Files.exists(expectedPath)).thenReturn(true);
            filesMock.when(() -> Files.delete(expectedPath)).thenThrow(new IOException("Cannot delete file"));

            Assertions.assertThrows(
                    IllegalStateException.class,
                    () -> activity.deleteMassiveNoticesFile(ingestionFlowFileId)
            );

            filesMock.verify(() -> Files.exists(expectedPath));
            filesMock.verify(() -> Files.delete(expectedPath));
            filesMock.verifyNoMoreInteractions();
        }
    }
}
