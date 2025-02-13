package it.gov.pagopa.payhub.activities.activity.ingestionflow;

import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class IngestionFlowFileProcessingLockerActivityTest {

  @Mock
  private IngestionFlowFileService ingestionFlowFileServiceMock;

  @InjectMocks
  private IngestionFlowFileProcessingLockerActivityImpl ingestionFlowFileLockerActivity;

  private static final Long VALID_ID=1L;
  private static final Long INVALID_ID=9L;

  @Test
  void givenValidIdWhenUpdateStatusThenTrue(){
    //given
    Mockito.when(ingestionFlowFileServiceMock.updateProcessingIfNoOtherProcessing(VALID_ID)).thenReturn(1);
    //when
    boolean result = ingestionFlowFileLockerActivity.acquireProcessingLock(VALID_ID);
    //verify
    Mockito.verify(ingestionFlowFileServiceMock, Mockito.times(1)).updateProcessingIfNoOtherProcessing(VALID_ID);
    assertTrue(result);
  }

  @Test
  void givenInvalidIdAndNewStatusWhenUpdateStatusThenFalse(){
    //given
    Mockito.when(ingestionFlowFileServiceMock.updateProcessingIfNoOtherProcessing(INVALID_ID)).thenReturn(0);
    //when
    boolean result = ingestionFlowFileLockerActivity.acquireProcessingLock(INVALID_ID);
    assertFalse(result);
  }

}
