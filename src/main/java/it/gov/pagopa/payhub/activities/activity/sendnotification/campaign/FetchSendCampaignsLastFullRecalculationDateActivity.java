package it.gov.pagopa.payhub.activities.activity.sendnotification.campaign;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

import java.time.OffsetDateTime;

@ActivityInterface
public interface FetchSendCampaignsLastFullRecalculationDateActivity {
    @ActivityMethod
    OffsetDateTime fetchSendCampaignsLastFullRecalculationDate();
}

