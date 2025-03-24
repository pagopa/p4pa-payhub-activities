package it.gov.pagopa.payhub.activities.service.exportflow;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import it.gov.pagopa.payhub.activities.connector.processexecutions.ExportFileService;
import it.gov.pagopa.payhub.activities.exception.exportflow.ExportFileNotFoundException;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFileStatus;
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
class ExportFileExpirationHandlerServiceTest {
  private static final String SHARED_PATH = "/tmp";

  @Mock
  private ExportFileService exportFileServiceMock;

  private ExportFileExpirationHandlerService exportFileExpirationHandlerService;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  @BeforeEach
  void setup() {
    exportFileExpirationHandlerService = new ExportFileExpirationHandlerService(SHARED_PATH, exportFileServiceMock);
  }

  @Test
  void testHandleExpirationOk() {
    // given
    ExportFile exportFile = podamFactory.manufacturePojo(ExportFile.class);
    exportFile.setStatus(ExportFileStatus.COMPLETED);
    when(exportFileServiceMock.findById(exportFile.getExportFileId())).thenReturn(Optional.of(exportFile));

    when(exportFileServiceMock.updateStatus(eq(exportFile.getExportFileId()), eq(exportFile.getStatus()), eq(ExportFileStatus.EXPIRED), anyString()))
        .thenReturn(1);

    try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
      mockedFiles.when(() -> Files.deleteIfExists(any(Path.class))).thenReturn(true);

      // when
      exportFileExpirationHandlerService.handleExpiration(exportFile.getExportFileId(), "");

      // then
      Mockito.verifyNoMoreInteractions(exportFileServiceMock);
    }
  }

  @Test
  void givenErrorWhenFilesDeleteThenThrowException() {
    // given
    ExportFile exportFile = podamFactory.manufacturePojo(ExportFile.class);
    exportFile.setStatus(ExportFileStatus.COMPLETED);
    when(exportFileServiceMock.findById(exportFile.getExportFileId())).thenReturn(Optional.of(exportFile));

    try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
      mockedFiles.when(() -> Files.deleteIfExists(any(Path.class))).thenThrow(new IllegalStateException("DUMMY"));

      Assertions.assertThrows(IllegalStateException.class, () -> exportFileExpirationHandlerService.handleExpiration(
          exportFile.getExportFileId(), ""));
      Mockito.verifyNoMoreInteractions(exportFileServiceMock);
    }
  }

  @Test
  void whenExportFileNotFoundThenThrowException() {
    // given
    when(exportFileServiceMock.findById(anyLong())).thenReturn(Optional.empty());
    // when
    Assertions.assertThrows(ExportFileNotFoundException.class, () -> exportFileExpirationHandlerService.handleExpiration(1L, ""));
    // then
    Mockito.verifyNoMoreInteractions(exportFileServiceMock);
  }

  @Test
  void whenUpdateStatusKoThenThrowException() {
    // given
    ExportFile exportFile = podamFactory.manufacturePojo(ExportFile.class);
    exportFile.setStatus(ExportFileStatus.COMPLETED);
    when(exportFileServiceMock.findById(exportFile.getExportFileId())).thenReturn(Optional.of(exportFile));

    when(exportFileServiceMock.updateStatus(exportFile.getExportFileId(), exportFile.getStatus(), ExportFileStatus.EXPIRED, ""))
        .thenThrow(new ExportFileNotFoundException("DUMMY"));

    try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
      mockedFiles.when(() -> Files.deleteIfExists(any(Path.class))).thenReturn(true);

      // when
      Assertions.assertThrows(ExportFileNotFoundException.class, () -> exportFileExpirationHandlerService.handleExpiration(exportFile.getExportFileId(), ""));

      // then
      Mockito.verifyNoMoreInteractions(exportFileServiceMock);
    }
  }
}