package it.gov.pagopa.payhub.activities.activity.ingestionflow;

import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
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
  private static final String VALID_STATUS="VALID";
  private static final String COD_ERROR="CODE_ERROR";
  private static final String DISCARD_FILE_NAME="DISCARDFILENAME";

  @Test
  void givenValidIdAndNewStatusWhenUpdateStatusThenTrue(){
    //given
    Mockito.when(ingestionFlowFileServiceMock.updateStatus(VALID_ID, VALID_STATUS, COD_ERROR,DISCARD_FILE_NAME)).thenReturn(1);
    //when
    boolean result = updateIngestionFlowStatusActivity.updateStatus(VALID_ID, VALID_STATUS, COD_ERROR,DISCARD_FILE_NAME);
    //verify
    Assertions.assertTrue(result);
    Mockito.verify(ingestionFlowFileServiceMock, Mockito.times(1)).updateStatus(VALID_ID, VALID_STATUS, COD_ERROR, DISCARD_FILE_NAME);
  }

  @Test
  void givenInvalidIdAndNewStatusWhenUpdateStatusThenFalse(){
    //given
    Mockito.when(ingestionFlowFileServiceMock.updateStatus(INVALID_ID, VALID_STATUS, COD_ERROR, DISCARD_FILE_NAME)).thenReturn(0);
    //when
    boolean result = updateIngestionFlowStatusActivity.updateStatus(INVALID_ID, VALID_STATUS, COD_ERROR, DISCARD_FILE_NAME);
    //verify
    Assertions.assertFalse(result);
    Mockito.verify(ingestionFlowFileServiceMock, Mockito.times(1)).updateStatus(INVALID_ID, VALID_STATUS, COD_ERROR, DISCARD_FILE_NAME);
  }

  @Test
  void givenNullIdAndNewStatusWhenUpdateStatusThenException(){
    String expectedError = "A null IngestionFlowFile was provided when updating its status to " + VALID_STATUS;
    //verify
    IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
      () -> updateIngestionFlowStatusActivity.updateStatus(null, VALID_STATUS, COD_ERROR, DISCARD_FILE_NAME));
    Assertions.assertEquals(expectedError, exception.getMessage());
  }

  @Test
  void givenIdAndNullNewStatusWhenUpdateStatusThenException(){
    String expectedError = "A null IngestionFlowFile status was provided when updating the id " + VALID_ID;
    //verify
    IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
      () -> updateIngestionFlowStatusActivity.updateStatus(VALID_ID, null, COD_ERROR, DISCARD_FILE_NAME));
    Assertions.assertEquals(expectedError, exception.getMessage());
  }

}
