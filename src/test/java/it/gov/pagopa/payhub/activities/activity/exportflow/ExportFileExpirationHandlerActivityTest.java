package it.gov.pagopa.payhub.activities.activity.exportflow;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import it.gov.pagopa.payhub.activities.connector.processexecutions.ExportFileService;
import it.gov.pagopa.payhub.activities.dto.exportflow.UpdateStatusRequest;
import it.gov.pagopa.payhub.activities.exception.exportflow.ExportFileNotFoundException;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFileStatus;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

@ExtendWith(MockitoExtension.class)
class ExportFileExpirationHandlerActivityTest {
  private static final String SHARED_PATH = "/tmp";
  @Mock
  private ExportFileService exportFileServiceMock;

  private ExportFileExpirationHandlerActivityImpl exportFileExpirationHandlerActivity;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  @BeforeEach
  void setup() {
    exportFileExpirationHandlerActivity = new ExportFileExpirationHandlerActivityImpl(SHARED_PATH, exportFileServiceMock);
  }

  @Test
  void testHandleExportExpirationOk() {
    // given
    ExportFile exportFile = podamFactory.manufacturePojo(ExportFile.class);
    exportFile.setStatus(ExportFileStatus.COMPLETED);
    UpdateStatusRequest updateStatusRequest = new UpdateStatusRequest(exportFile.getExportFileId(), exportFile.getStatus(), ExportFileStatus.EXPIRED,
        exportFile.getFilePathName(), exportFile.getFileName(), exportFile.getFileSize(), exportFile.getNumTotalRows(),exportFile.getErrorDescription(), exportFile.getExpirationDate());

    when(exportFileServiceMock.findById(exportFile.getExportFileId())).thenReturn(Optional.of(exportFile));

    when(exportFileServiceMock.updateStatus(Mockito.argThat(u-> {
      TestUtils.reflectionEqualsByName(updateStatusRequest, u);
      return true;
    })))
        .thenReturn(1);

    try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
      mockedFiles.when(() -> Files.deleteIfExists(any(Path.class))).thenReturn(true);

      // when
      exportFileExpirationHandlerActivity.handleExportExpiration(exportFile.getExportFileId());

      // then
      Mockito.verifyNoMoreInteractions(exportFileServiceMock);
    }
  }

  @Test
  void givenErrorWhenFilesDeleteThenThrowException() {
    // given
    ExportFile exportFile = podamFactory.manufacturePojo(ExportFile.class);
    exportFile.setStatus(ExportFileStatus.COMPLETED);
    Long exportFileId = exportFile.getExportFileId();
    when(exportFileServiceMock.findById(exportFileId)).thenReturn(Optional.of(exportFile));

    try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
      mockedFiles.when(() -> Files.deleteIfExists(any(Path.class))).thenThrow(new IOException("DUMMY"));

      Assertions.assertThrows(IllegalStateException.class, () -> exportFileExpirationHandlerActivity.handleExportExpiration(
          exportFileId));
      Mockito.verifyNoMoreInteractions(exportFileServiceMock);
    }
  }

  @Test
  void whenExportFileNotFoundThenThrowException() {
    // given
    when(exportFileServiceMock.findById(anyLong())).thenReturn(Optional.empty());
    // when
    Assertions.assertThrows(ExportFileNotFoundException.class, () -> exportFileExpirationHandlerActivity.handleExportExpiration(1L));
    // then
    Mockito.verifyNoMoreInteractions(exportFileServiceMock);
  }

  @Test
  void whenUpdateStatusKoThenThrowException() {
    // given
    ExportFile exportFile = podamFactory.manufacturePojo(ExportFile.class);
    exportFile.setStatus(ExportFileStatus.COMPLETED);
    UpdateStatusRequest updateStatusRequest = new UpdateStatusRequest(exportFile.getExportFileId(), exportFile.getStatus(), ExportFileStatus.EXPIRED,
        exportFile.getFilePathName(), exportFile.getFileName(), exportFile.getFileSize(), exportFile.getNumTotalRows(),exportFile.getErrorDescription(), exportFile.getExpirationDate());
    Long exportFileId = exportFile.getExportFileId();
    when(exportFileServiceMock.findById(exportFileId)).thenReturn(Optional.of(exportFile));

    when(exportFileServiceMock.updateStatus(Mockito.argThat(u-> {
        TestUtils.reflectionEqualsByName(updateStatusRequest, u);
        return true;
      })))
        .thenReturn(0);

    try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
      mockedFiles.when(() -> Files.deleteIfExists(any(Path.class))).thenReturn(true);

      // when
      Assertions.assertThrows(ExportFileNotFoundException.class, () -> exportFileExpirationHandlerActivity.handleExportExpiration(
          exportFileId));

      // then
      Mockito.verifyNoMoreInteractions(exportFileServiceMock);
    }
  }

  @Test
  void givenEmptyFileName_whenGetFilePath_thenUpdateStatusWithoutDeleting() {
    // given
    ExportFile exportFile = podamFactory.manufacturePojo(ExportFile.class);
    exportFile.setStatus(ExportFileStatus.COMPLETED);
    exportFile.setFilePathName("");
    UpdateStatusRequest updateStatusRequest = new UpdateStatusRequest(exportFile.getExportFileId(), exportFile.getStatus(), ExportFileStatus.EXPIRED,
        exportFile.getFilePathName(), exportFile.getFileName(), exportFile.getFileSize(), exportFile.getNumTotalRows(),exportFile.getErrorDescription(), exportFile.getExpirationDate());
    Long exportFileId = exportFile.getExportFileId();
    when(exportFileServiceMock.findById(exportFileId)).thenReturn(Optional.of(exportFile));

    when(exportFileServiceMock.updateStatus(Mockito.argThat(u-> {
      TestUtils.reflectionEqualsByName(updateStatusRequest, u);
      return true;
    })))
        .thenReturn(1);

    // when
    exportFileExpirationHandlerActivity.handleExportExpiration(exportFile.getExportFileId());

    // then
    Mockito.verifyNoMoreInteractions(exportFileServiceMock);
  }

}
