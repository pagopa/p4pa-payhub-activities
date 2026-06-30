package it.gov.pagopa.payhub.activities.connector.sendnotification.client;

import it.gov.pagopa.payhub.activities.connector.sendnotification.config.SendApisHolder;
import it.gov.pagopa.pu.sendnotification.controller.generated.CampaignApi;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class CampaignClientTest {
    @Mock
    private SendApisHolder sendApisHolderMock;
    @Mock
    private CampaignApi campaignApiMock;

    private CampaignClient campaignClient;

    @BeforeEach
    void setUp() {
        campaignClient = new CampaignClient(sendApisHolderMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(sendApisHolderMock);
    }

    @Test
    void whenFetchAllCampaignIdsThenInvokeWithAccessToken() {
        String accessToken = "accessToken";
        List<String> campaignIds = List.of("campaignId");

        Mockito.when(sendApisHolderMock.getCampaignApi(accessToken)).thenReturn(campaignApiMock);
        Mockito.when(campaignApiMock.fetchAllCampaignIds()).thenReturn(campaignIds);

        List<String> actualIds = campaignClient.fetchAllCampaignIds(accessToken);

        Assertions.assertEquals(campaignIds, actualIds);
    }

    @Test
    void whenAlignCampaignThenInvokeWithAccessToken() {
        String accessToken = "accessToken";
        String campaignId = "campaignId";

        Mockito.when(sendApisHolderMock.getCampaignApi(accessToken)).thenReturn(campaignApiMock);
        Mockito.doNothing().when(campaignApiMock).alignCampaign(campaignId);

        Assertions.assertDoesNotThrow(() -> campaignClient.alignCampaign(campaignId, accessToken));
    }
}
