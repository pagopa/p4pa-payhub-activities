package it.gov.pagopa.payhub.activities.activity.ingestionflow;

import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
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
  private IngestionFlowFileDao ingestionFlowFileDao;

  @InjectMocks
  private UpdateIngestionFlowStatusActivityImpl updateIngestionFlowStatusActivity;

  private static final Long VALID_ID=1L;
  private static final Long INVALID_ID=9L;
  private static final String VALID_STATUS="VALID";

  @Test
  void givenValidIdAndNewStatusWhenUpdateStatusThenTrue(){
    //given
    Mockito.when(ingestionFlowFileDao.updateStatus(VALID_ID, VALID_STATUS)).thenReturn(true);
    //when
    boolean result = updateIngestionFlowStatusActivity.updateStatus(VALID_ID, VALID_STATUS);
    //verify
    Assertions.assertTrue(result);
    Mockito.verify(ingestionFlowFileDao, Mockito.times(1)).updateStatus(VALID_ID, VALID_STATUS);
  }

  @Test
  void givenInvalidIdAndNewStatusWhenUpdateStatusThenFalse(){
    //given
    Mockito.when(ingestionFlowFileDao.updateStatus(INVALID_ID, VALID_STATUS)).thenReturn(false);
    //when
    boolean result = updateIngestionFlowStatusActivity.updateStatus(INVALID_ID, VALID_STATUS);
    //verify
    Assertions.assertFalse(result);
    Mockito.verify(ingestionFlowFileDao, Mockito.times(1)).updateStatus(INVALID_ID, VALID_STATUS);
  }

  @Test
  void givenNullIdAndNewStatusWhenUpdateStatusThenException(){
    String expectedError = "A null IngestionFlowFile was provided when updating its status to " + VALID_STATUS;
    //verify
    IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
      () -> updateIngestionFlowStatusActivity.updateStatus(null, VALID_STATUS));
    Assertions.assertEquals(expectedError, exception.getMessage());
  }

  @Test
  void givenIdAndNullNewStatusWhenUpdateStatusThenException(){
    String expectedError = "A null IngestionFlowFile status was provided when updating the id " + VALID_ID;
    //verify
    IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
      () -> updateIngestionFlowStatusActivity.updateStatus(VALID_ID, null));
    Assertions.assertEquals(expectedError, exception.getMessage());
  }

}
