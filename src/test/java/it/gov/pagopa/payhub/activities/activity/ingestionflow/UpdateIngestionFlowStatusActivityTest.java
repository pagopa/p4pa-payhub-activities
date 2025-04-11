package it.gov.pagopa.payhub.activities.activity.ingestionflow;

import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.ingestion.IngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.IngestionFlowFileNotFoundException;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFileStatus;
import org.junit.jupiter.api.AfterEach;
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
  private static final IngestionFlowFileStatus OLD_STATUS = IngestionFlowFileStatus.UPLOADED;
  private static final IngestionFlowFileStatus NEW_STATUS = IngestionFlowFileStatus.PROCESSING;

  @AfterEach
  void verifyNoMoreInteractions(){
    Mockito.verifyNoMoreInteractions(ingestionFlowFileServiceMock);
  }

  @Test
  void givenValidIdAndNewStatusWhenUpdateStatusThenTrue(){
    // Given
    IngestionFlowFileResult ingestionFlowFileResult = new IngestionFlowFileResult();

    Mockito.when(ingestionFlowFileServiceMock.updateStatus(Mockito.same(VALID_ID), Mockito.same(OLD_STATUS), Mockito.same(NEW_STATUS), Mockito.same(ingestionFlowFileResult)))
            .thenReturn(1);

    // When, Then
    Assertions.assertDoesNotThrow(() -> updateIngestionFlowStatusActivity.updateStatus(VALID_ID, OLD_STATUS, NEW_STATUS, ingestionFlowFileResult));
  }

  @Test
  void givenInvalidIdAndNewStatusWhenUpdateStatusThenFalse(){
    // Given
    IngestionFlowFileResult ingestionFlowFileResult = new IngestionFlowFileResult();

    Mockito.when(ingestionFlowFileServiceMock.updateStatus(Mockito.same(INVALID_ID), Mockito.same(OLD_STATUS), Mockito.same(NEW_STATUS), Mockito.same(ingestionFlowFileResult)))
            .thenReturn(0);

    // When, Then
    Assertions.assertThrows(IngestionFlowFileNotFoundException.class, () -> updateIngestionFlowStatusActivity.updateStatus(INVALID_ID, OLD_STATUS, NEW_STATUS, ingestionFlowFileResult));
  }

}
