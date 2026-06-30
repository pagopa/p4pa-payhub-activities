package it.gov.pagopa.payhub.activities.activity.sendnotification.campaign;

import it.gov.pagopa.payhub.activities.connector.sendnotification.CampaignService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class FetchSendCampaignsActivityTest {
    @Mock
    private CampaignService campaignServiceMock;

    private FetchSendCampaignsActivity fetchSendCampaignsActivity;

    @BeforeEach
    void init() {
        fetchSendCampaignsActivity = new FetchSendCampaignsActivityImpl(campaignServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(campaignServiceMock);
    }

    @Test
    void whenFetchSendCampaignIdsThenOk() {
        List<String> campaignIds = List.of("campaignId1", "campaignId2");
        Mockito.when(campaignServiceMock.fetchAllCampaignIds()).thenReturn(campaignIds);

        List<String> res = fetchSendCampaignsActivity.fetchSendCampaignIds();

        assertEquals(campaignIds, res);
    }
}
