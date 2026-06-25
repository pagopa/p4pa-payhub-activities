package it.gov.pagopa.payhub.activities.connector.sendnotification.client;

import it.gov.pagopa.payhub.activities.connector.sendnotification.config.SendApisHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Lazy
@Service
@Slf4j
public class CampaignClient {
    private final SendApisHolder sendApisHolder;

    private CampaignClient(SendApisHolder sendApisHolder) {
        this.sendApisHolder = sendApisHolder;
    }

    public List<String> fetchAllCampaignIds(String accessToken) {
        return sendApisHolder.getCampaignApi(accessToken).fetchAllCampaignIds();
    }
}
