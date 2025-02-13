package it.gov.pagopa.payhub.activities.activity.ingestionflow;

import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.IngestionFlowFileNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IngestionFlowFileLockerActivityTest {

  @Mock
  private IngestionFlowFileService ingestionFlowFileServiceMock;

  @InjectMocks
  private IngestionFlowFileLockerActivityImpl ingestionFlowFileLockerActivity;

  private static final Long VALID_ID=1L;
  private static final Long INVALID_ID=9L;

  @Test
  void givenValidIdWhenUpdateStatusThenTrue(){
    //given
    Mockito.when(ingestionFlowFileServiceMock.updateProcessingIfNoOtherProcessing(VALID_ID)).thenReturn(1);
    //when
    ingestionFlowFileLockerActivity.updateProcessingIfNoOtherProcessing(VALID_ID);
    //verify
    Mockito.verify(ingestionFlowFileServiceMock, Mockito.times(1)).updateProcessingIfNoOtherProcessing(VALID_ID);
  }

  @Test
  void givenInvalidIdAndNewStatusWhenUpdateStatusThenFalse(){
    //given
    Mockito.when(ingestionFlowFileServiceMock.updateProcessingIfNoOtherProcessing(INVALID_ID)).thenReturn(0);
    //when
    Assertions.assertThrows(IngestionFlowFileNotFoundException.class, () -> ingestionFlowFileLockerActivity.updateProcessingIfNoOtherProcessing(INVALID_ID));
  }

}
