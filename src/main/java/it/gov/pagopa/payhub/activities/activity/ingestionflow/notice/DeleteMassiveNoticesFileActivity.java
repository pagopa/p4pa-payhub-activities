package it.gov.pagopa.payhub.activities.activity.ingestionflow.notice;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface DeleteMassiveNoticesFileActivity {

    @ActivityMethod
    void deleteMassiveNoticesFile(Long ingestionFlowFileId);

}
