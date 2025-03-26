package it.gov.pagopa.payhub.activities.activity.exportflow;

import it.gov.pagopa.payhub.activities.connector.processexecutions.ExportFileService;
import it.gov.pagopa.payhub.activities.exception.exportflow.ExportFileNotFoundException;
import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFileStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

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
  private static final String ERROR_DESCRIPTION ="ERROR_DESCRIPTION";
  private static final String DISCARD_FILE_NAME="DISCARD_FILENAME";

  @Test
  void givenValidIdAndNewStatusWhenUpdateStatusThenTrue(){
    //given
    Mockito.when(
        exportFileServiceMock.updateStatus(VALID_ID, OLD_STATUS, NEW_STATUS, null)).thenReturn(1);
    //when
    updateExportFileStatusActivity.updateStatus(VALID_ID, OLD_STATUS, NEW_STATUS);
    //verify
    Mockito.verify(exportFileServiceMock, Mockito.times(1)).updateStatus(VALID_ID, OLD_STATUS, NEW_STATUS, null);
  }

  @Test
  void givenInvalidIdAndNewStatusWhenUpdateStatusThenFalse(){
    //given
    Mockito.when(
        exportFileServiceMock.updateStatus(INVALID_ID, OLD_STATUS, NEW_STATUS, null)).thenReturn(0);
    //when
    Assertions.assertThrows(ExportFileNotFoundException.class, () -> updateExportFileStatusActivity.updateStatus(INVALID_ID, OLD_STATUS, NEW_STATUS));
  }

}
