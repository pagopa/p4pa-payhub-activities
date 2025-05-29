package it.gov.pagopa.payhub.activities.activity.exportflow;

import it.gov.pagopa.payhub.activities.connector.processexecutions.ExportFileService;
import it.gov.pagopa.payhub.activities.dto.exportflow.UpdateStatusRequest;
import it.gov.pagopa.payhub.activities.exception.exportflow.ExportFileNotFoundException;
import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFileStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;

@ExtendWith(MockitoExtension.class)
class UpdateExportFileStatusActivityTest {

  @Mock
  private ExportFileService exportFileServiceMock;

  @InjectMocks
  private UpdateExportFileStatusActivityImpl updateExportFileStatusActivity;

  private static final Long VALID_ID=1L;
  private static final Long INVALID_ID=9L;
  private static final ExportFileStatus OLD_STATUS = ExportFileStatus.PROCESSING;
  private static final ExportFileStatus NEW_STATUS = ExportFileStatus.COMPLETED;
  private static final String FILE_PATH = "filePath";
  private static final String FILE_NAME = "fileName";
  private static final Long FILE_SIZE = 20L;
  private static final Long EXPORTED_ROWS = 2L;
  private static final String ERROR_DESCRIPTION = "errorDescription";
  private static final OffsetDateTime EXPIRATION_DATE = OffsetDateTime.now().plusDays(5L);

  @Test
  void givenValidIdAndNewStatusWhenUpdateStatusThenTrue(){
    UpdateStatusRequest updateStatusRequest = new UpdateStatusRequest(VALID_ID, OLD_STATUS,
        NEW_STATUS, FILE_PATH, FILE_NAME, FILE_SIZE, EXPORTED_ROWS, ERROR_DESCRIPTION, EXPIRATION_DATE);
    //given
    Mockito.when(
        exportFileServiceMock.updateStatus(updateStatusRequest)).thenReturn(1);
    //when
    updateExportFileStatusActivity.updateStatus(updateStatusRequest);
    //verify
    Mockito.verify(exportFileServiceMock, Mockito.times(1)).updateStatus(updateStatusRequest);
  }

  @Test
  void givenInvalidIdAndNewStatusWhenUpdateStatusThenFalse(){
    UpdateStatusRequest updateStatusRequest = new UpdateStatusRequest(INVALID_ID, OLD_STATUS, NEW_STATUS, FILE_PATH, FILE_NAME, FILE_SIZE, EXPORTED_ROWS, ERROR_DESCRIPTION, EXPIRATION_DATE);
    //given
    Mockito.when(
        exportFileServiceMock.updateStatus(updateStatusRequest)).thenReturn(0);
    //when
    Assertions.assertThrows(ExportFileNotFoundException.class, () -> updateExportFileStatusActivity.updateStatus(updateStatusRequest));
  }

}
