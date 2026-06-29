package it.gov.pagopa.payhub.activities.activity.sendnotification.campaign;

import it.gov.pagopa.payhub.activities.connector.sendnotification.CampaignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Lazy
public class AlignSendCampaignActivityImpl implements AlignSendCampaignActivity {
    private final CampaignService campaignService;

    public AlignSendCampaignActivityImpl(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @Override
    public void alignSendCampaign(String campaignId) {
        log.info("Align campaign with id {}", campaignId);
        campaignService.alignCampaign(campaignId);
    }
}
