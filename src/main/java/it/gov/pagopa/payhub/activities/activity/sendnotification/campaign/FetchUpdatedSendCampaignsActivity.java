package it.gov.pagopa.payhub.activities.activity.sendnotification.campaign;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

import java.util.List;

@ActivityInterface
public interface FetchUpdatedSendCampaignsActivity {
    @ActivityMethod
    List<String> fetchIdsForUpdatedSendCampaigns();
}
