package it.gov.pagopa.payhub.activities.connector.sendnotification.client;

import it.gov.pagopa.payhub.activities.connector.sendnotification.config.SendApisHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Lazy
@Service
@Slf4j
public class CampaignClient {
    private final SendApisHolder sendApisHolder;

    public CampaignClient(SendApisHolder sendApisHolder) {
        this.sendApisHolder = sendApisHolder;
    }

    public List<String> fetchAllCampaignIds(String accessToken) {
        return sendApisHolder.getCampaignApi(accessToken).fetchAllCampaignIds();
    }

    public void alignCampaign(String campaignId, OffsetDateTime countersRecalculationDate, String accessToken) {
        sendApisHolder.getCampaignApi(accessToken).alignCampaign(campaignId, countersRecalculationDate);
    }

    public OffsetDateTime findLatestFullRecalculationDate(String accessToken) {
        return sendApisHolder.getCampaignApi(accessToken).findLatestFullRecalculationDate();
    }

    public OffsetDateTime findFirstCampaignStartDate(String accessToken) {
        return sendApisHolder.getCampaignApi(accessToken).findFirstCampaignStartDate();
    }

    public List<String> findIdsOfUpdatedCampaignsByNotificationUpdateDate(OffsetDateTime latestFullRecalculationDate, String accessToken) {
        return sendApisHolder.getCampaignApi(accessToken).findIdsOfUpdatedCampaignsByNotificationUpdateDate(latestFullRecalculationDate);
    }
}
