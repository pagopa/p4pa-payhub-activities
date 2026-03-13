package it.gov.pagopa.payhub.activities.activity.ingestionflow.debtposition;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface FetchAndMergeNoticesActivity {
    @ActivityMethod
    Integer fetchAndMergeNotices(Long ingestionFlowFileId);
}
