package it.gov.pagopa.payhub.activities.activity.sendnotification.campaign;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
class AlignSendCampaignActivityTest {
    private AlignSendCampaignActivity alignSendCampaignActivity;

    @BeforeEach
    void init() {
        alignSendCampaignActivity = new AlignSendCampaignActivityImpl();
    }

    @Test
    void whenAlignSendCampaignThenOk() {
        String campaignId = "campaignId";

        assertDoesNotThrow(() -> alignSendCampaignActivity.alignSendCampaign(campaignId));
    }
}
