package it.gov.pagopa.payhub.activities.activity.ingestionflow.notice;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface DeleteNoticeRetentionFileActivity {

    @ActivityMethod
    void deleteNoticeRetentionFile(Long ingestionFlowFileId);

}
