package it.gov.pagopa.payhub.activities.activity.sendnotification.campaign;

import it.gov.pagopa.payhub.activities.connector.sendnotification.CampaignService;

import java.util.List;

public class FetchSendCampaignsActivityImpl implements FetchSendCampaignsActivity {
    private CampaignService campaignService;

    public FetchSendCampaignsActivityImpl(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @Override
    public List<String> fetchSendCampaignIds() {
        return campaignService.fetchAllCampaignIds();
    }
}
