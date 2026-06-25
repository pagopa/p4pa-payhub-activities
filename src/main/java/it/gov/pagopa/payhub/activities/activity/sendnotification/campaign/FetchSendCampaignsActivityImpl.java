package it.gov.pagopa.payhub.activities.activity.sendnotification.campaign;

import it.gov.pagopa.payhub.activities.connector.sendnotification.CampaignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@Lazy
public class FetchSendCampaignsActivityImpl implements FetchSendCampaignsActivity {
    private final CampaignService campaignService;

    public FetchSendCampaignsActivityImpl(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @Override
    public List<String> fetchSendCampaignIds() {
        log.info("Fetch send campaign ids");
        return campaignService.fetchAllCampaignIds();
    }
}
