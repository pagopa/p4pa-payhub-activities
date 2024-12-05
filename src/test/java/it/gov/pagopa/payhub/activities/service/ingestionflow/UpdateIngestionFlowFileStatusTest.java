package it.gov.pagopa.payhub.activities.service.ingestionflow;

import it.gov.pagopa.payhub.activities.activity.ingestionflow.UpdateIngestionFlowStatusActivityImpl;
import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class UpdateIngestionFlowFileStatusTest {
    @Mock
    IngestionFlowFileDao ingestionFlowDao;

    private UpdateIngestionFlowStatusActivityImpl updateIngestionFlowStatusActivity;

    @BeforeEach
    void init() {
        IngestionFlowFileDao ingestionFlowFileDao = mock(IngestionFlowFileDao.class);
        updateIngestionFlowStatusActivity = new UpdateIngestionFlowStatusActivityImpl(ingestionFlowDao);
    }

    @Test
    void updateIngestionFlowStatus() {
        String id = "100";
        assertTrue(updateIngestionFlowStatusActivity.updateStatus(id, "NEW_STATUS"));
    }


}

