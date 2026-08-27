package it.gov.pagopa.payhub.activities.activity.sendnotification.campaign;

import it.gov.pagopa.payhub.activities.connector.sendnotification.CampaignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Component
@Lazy
public class FetchUpdatedSendCampaignsActivityImpl implements FetchUpdatedSendCampaignsActivity {

    private final CampaignService campaignService;

    public FetchUpdatedSendCampaignsActivityImpl(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @Override
    public List<String> fetchIdsForUpdatedSendCampaigns() {
        log.info("Fetch ids for updated SEND campaigns");
        OffsetDateTime latestFullRecalculationDate = campaignService.findLatestFullRecalculationDate();
        if(latestFullRecalculationDate!=null) {
            return campaignService.findIdsOfUpdatedCampaignsByNotificationUpdateDate(latestFullRecalculationDate);
        } else {
            return campaignService.fetchAllCampaignIds();
        }
    }

}
