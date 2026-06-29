package it.gov.pagopa.payhub.activities.connector.sendnotification;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.sendnotification.client.CampaignClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Lazy
@Service
public class CampaignServiceImpl implements CampaignService {
    private final CampaignClient campaignClient;
    private final AuthnService authnService;

    public CampaignServiceImpl(CampaignClient campaignClient, AuthnService authnService) {
        this.campaignClient = campaignClient;
        this.authnService = authnService;
    }

    @Override
    public List<String> fetchAllCampaignIds() {
        return campaignClient.fetchAllCampaignIds(authnService.getAccessToken());
    }

    @Override
    public void alignCampaign(String campaignId) {
        campaignClient.alignCampaign(campaignId, authnService.getAccessToken());
    }
}
