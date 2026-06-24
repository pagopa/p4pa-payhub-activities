package it.gov.pagopa.payhub.activities.activity.sendnotification.campaign;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface AlignSendCampaignActivity {
    @ActivityMethod
    void alignSendCampaign(String campaignId);
}
