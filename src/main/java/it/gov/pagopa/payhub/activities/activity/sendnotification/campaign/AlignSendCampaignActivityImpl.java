package it.gov.pagopa.payhub.activities.activity.sendnotification.campaign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Lazy
public class AlignSendCampaignActivityImpl implements AlignSendCampaignActivity {
    @Override
    public void alignSendCampaign(String campaignId) {
        // TODO: P4ADEV-4793
    }
}
