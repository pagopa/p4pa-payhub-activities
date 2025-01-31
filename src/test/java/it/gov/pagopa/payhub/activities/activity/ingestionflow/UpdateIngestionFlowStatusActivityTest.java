package it.gov.pagopa.payhub.activities.activity.ingestionflow;

import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.IngestionFlowFileNotFoundException;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateIngestionFlowStatusActivityTest {

  @Mock
  private IngestionFlowFileService ingestionFlowFileServiceMock;

  @InjectMocks
  private UpdateIngestionFlowStatusActivityImpl updateIngestionFlowStatusActivity;

  private static final Long VALID_ID=1L;
  private static final Long INVALID_ID=9L;
  private static final IngestionFlowFile.StatusEnum VALID_STATUS = IngestionFlowFile.StatusEnum.PROCESSING;
  private static final String COD_ERROR="CODE_ERROR";
  private static final String DISCARD_FILE_NAME="DISCARDFILENAME";

  @Test
  void givenValidIdAndNewStatusWhenUpdateStatusThenTrue(){
    //given
    Mockito.when(ingestionFlowFileServiceMock.updateStatus(VALID_ID, VALID_STATUS, COD_ERROR,DISCARD_FILE_NAME)).thenReturn(1);
    //when
    updateIngestionFlowStatusActivity.updateStatus(VALID_ID, VALID_STATUS, COD_ERROR,DISCARD_FILE_NAME);
    //verify
    Mockito.verify(ingestionFlowFileServiceMock, Mockito.times(1)).updateStatus(VALID_ID, VALID_STATUS, COD_ERROR, DISCARD_FILE_NAME);
  }

  @Test
  void givenInvalidIdAndNewStatusWhenUpdateStatusThenFalse(){
    //given
    Mockito.when(ingestionFlowFileServiceMock.updateStatus(INVALID_ID, VALID_STATUS, COD_ERROR, DISCARD_FILE_NAME)).thenReturn(0);
    //when
    Assertions.assertThrows(IngestionFlowFileNotFoundException.class, () -> updateIngestionFlowStatusActivity.updateStatus(INVALID_ID, VALID_STATUS, COD_ERROR, DISCARD_FILE_NAME));
  }

}
