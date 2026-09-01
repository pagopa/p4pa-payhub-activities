package it.gov.pagopa.payhub.activities.activity.sendnotification.campaign;

import it.gov.pagopa.payhub.activities.connector.sendnotification.CampaignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Slf4j
@Component
@Lazy
public class FetchSendCampaignsLastFullRecalculationDateActivityImpl implements FetchSendCampaignsLastFullRecalculationDateActivity {

    private final CampaignService campaignService;

    public FetchSendCampaignsLastFullRecalculationDateActivityImpl(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    public OffsetDateTime fetchSendCampaignsLastFullRecalculationDate() {
        log.info("Fetch last full recalculation date for SEND campaigns");
        return campaignService.findLatestFullRecalculationDate();
    }
}
