package it.gov.pagopa.payhub.activities.activity.sendnotification.campaign;

import it.gov.pagopa.payhub.activities.connector.sendnotification.CampaignService;
import it.gov.pagopa.payhub.activities.util.Utilities;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;

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
        OffsetDateTime countersRecalculationDate = OffsetDateTime.now(Utilities.ZONEID);

        Mockito.doNothing().when(campaignServiceMock).alignCampaign(campaignId, countersRecalculationDate);

        assertDoesNotThrow(() -> alignSendCampaignActivity.alignSendCampaign(campaignId, countersRecalculationDate));
    }
}
