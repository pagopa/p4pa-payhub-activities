package it.gov.pagopa.payhub.activities.connector.sendnotification;

import java.time.OffsetDateTime;
import java.util.List;

public interface CampaignService {
    List<String> fetchAllCampaignIds();
    OffsetDateTime findLatestFullRecalculationDate();
    OffsetDateTime findFirstCampaignStartDate();
    List<String> findIdsOfUpdatedCampaignsByNotificationUpdateDate(OffsetDateTime fullRecalculationDate);
    void alignCampaign(String campaignId, OffsetDateTime countersRecalculationDate);
}
