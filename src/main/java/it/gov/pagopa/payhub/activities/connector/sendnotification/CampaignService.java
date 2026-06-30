package it.gov.pagopa.payhub.activities.connector.sendnotification;

import java.util.List;

public interface CampaignService {
    List<String> fetchAllCampaignIds();
    void alignCampaign(String campaignId);
}
