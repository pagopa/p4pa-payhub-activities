package it.gov.pagopa.payhub.activities.service.ingestionflow;

import it.gov.pagopa.payhub.activities.activity.ingestionflow.UpdateIngestionFlowStatusActivityImpl;
import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.reportingflow.PaymentsReportingIngestionFlowFileActivityResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateIngestionFlowFileStatusTest {
    @Mock
    IngestionFlowFileDao ingestionFlowFileDao;

    private UpdateIngestionFlowStatusActivityImpl updateIngestionFlowStatusActivity;

    private IngestionFlowFileDTO ingestionFlowFileDTO;

    @BeforeEach
    void init() {
        IngestionFlowFileDao ingestionFlowFileDao = mock(IngestionFlowFileDao.class);
        updateIngestionFlowStatusActivity = new UpdateIngestionFlowStatusActivityImpl(ingestionFlowFileDao);
    }

    @Test
    void updateIngestionFlowStatus() {
        String id = "100";
        String newStatus = "NEW_STATUS";
        Long ingestionFlowFileId = Long.valueOf(id);

        boolean resultStatus = true;
        when(ingestionFlowFileDao.updateStatus(ingestionFlowFileId, newStatus)).thenReturn(Optional.of(resultStatus));

        //assertTrue(updateIngestionFlowStatusActivity.updateStatus(id, newStatus));
    }

}

