package it.gov.pagopa.payhub.activities.activity.ingestionflow;

import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import it.gov.pagopa.payhub.activities.exception.ActivitiesException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * Implementation for the activity to update the ingestion flow status
 */

@Slf4j
@Component
public class UpdateIngestionFlowStatusActivityImpl implements UpdateIngestionFlowStatusActivity {
    private final IngestionFlowFileDao ingestionFlowFileDao;

    public UpdateIngestionFlowStatusActivityImpl(IngestionFlowFileDao ingestionFlowFileDao) {
        this.ingestionFlowFileDao = ingestionFlowFileDao;
    }

    @Override
    public boolean updateStatus(Long id, String newStatus) {
        if(id==null){
            throw new ActivitiesException("id is null");
        }
        if(StringUtils.isBlank(newStatus)){
            throw new ActivitiesException("newStatus is null");
        }
        return ingestionFlowFileDao.updateStatus(id, newStatus);
    }
}
