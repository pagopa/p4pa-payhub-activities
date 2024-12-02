package it.gov.pagopa.payhub.activities.activity;

import it.gov.pagopa.payhub.activities.activity.fdr.UpdateIngestionFlowStatusActivity;
import it.gov.pagopa.payhub.activities.dao.IngestionFlowDao;
import it.gov.pagopa.payhub.activities.paymentsreporting.UpdateIngestionFlowStatusActivityImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static it.gov.pagopa.payhub.activities.util.Constants.LOAD;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class UpdateIngestionFlowStatusActivityTest {
    private UpdateIngestionFlowStatusActivity updateIngestionFlowStatusActivity;

    @BeforeEach
    void init() {
        IngestionFlowDao ingestionFlowDao = mock(IngestionFlowDao.class);
        updateIngestionFlowStatusActivity = new UpdateIngestionFlowStatusActivityImpl(ingestionFlowDao);
    }


    @Test
    void updateIngestionFlowStatus() {
        String id = "100";
        boolean result = updateIngestionFlowStatusActivity.updateStatus(id, LOAD);
        assertFalse(result);
    }


}

