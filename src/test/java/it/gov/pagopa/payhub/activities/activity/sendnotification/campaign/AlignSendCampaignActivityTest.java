package it.gov.pagopa.payhub.activities.activity.sendnotification.campaign;

import it.gov.pagopa.payhub.activities.connector.sendnotification.CampaignService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
class AlignSendCampaignActivityTest {
    @Mock
    private CampaignService campaignServiceMock;

    private AlignSendCampaignActivity alignSendCampaignActivity;

    @BeforeEach
    void init() {
        alignSendCampaignActivity = new AlignSendCampaignActivityImpl(campaignServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(campaignServiceMock);
    }

    @Test
    void whenAlignSendCampaignThenOk() {
        String campaignId = "campaignId";

        Mockito.doNothing().when(campaignServiceMock).alignCampaign(campaignId);

        assertDoesNotThrow(() -> alignSendCampaignActivity.alignSendCampaign(campaignId));
    }
}
