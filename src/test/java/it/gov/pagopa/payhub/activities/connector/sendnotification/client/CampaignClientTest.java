package it.gov.pagopa.payhub.activities.connector.sendnotification.client;

import it.gov.pagopa.payhub.activities.connector.sendnotification.config.SendApisHolder;
import it.gov.pagopa.payhub.activities.util.Utilities;
import it.gov.pagopa.pu.sendnotification.client.generated.CampaignApi;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.Mockito.when;

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

        when(sendApisHolderMock.getCampaignApi(accessToken)).thenReturn(campaignApiMock);
        when(campaignApiMock.fetchAllCampaignIds()).thenReturn(campaignIds);

        List<String> actualIds = campaignClient.fetchAllCampaignIds(accessToken);

        Assertions.assertEquals(campaignIds, actualIds);
    }

    @Test
    void whenAlignCampaignThenInvokeWithAccessToken() {
        String accessToken = "accessToken";
        String campaignId = "campaignId";

        when(sendApisHolderMock.getCampaignApi(accessToken)).thenReturn(campaignApiMock);
        Mockito.doNothing().when(campaignApiMock).alignCampaign(campaignId);

        Assertions.assertDoesNotThrow(() -> campaignClient.alignCampaign(campaignId, accessToken));
    }

    @Test
    void whenFindLatestFullRecalculationDateThenInvokeWithAccessToken() {
        //GIVEN
        String accessToken = "accessToken";
        OffsetDateTime expectedLatestFullRecalculationDate = OffsetDateTime.now(Utilities.ZONEID);

        when(sendApisHolderMock.getCampaignApi(accessToken))
                .thenReturn(campaignApiMock);
        when(campaignApiMock.findLatestFullRecalculationDate())
                .thenReturn(expectedLatestFullRecalculationDate);
        //WHEN
        OffsetDateTime actualLatestFullRecalculationDate = campaignClient.findLatestFullRecalculationDate(accessToken);
        //THEN
        Assertions.assertEquals(expectedLatestFullRecalculationDate, actualLatestFullRecalculationDate);
    }

    @Test
    void whenFindIdsOfUpdatedCampaignsByNotificationUpdateDateThenInvokeWithAccessToken() {
        //GIVEN
        String accessToken = "accessToken";
        List<String> expectedCampaignIds = List.of("campaignId");

        when(sendApisHolderMock.getCampaignApi(accessToken))
                .thenReturn(campaignApiMock);
        when(campaignApiMock.findIdsOfUpdatedCampaignsByNotificationUpdateDate(Mockito.any(OffsetDateTime.class)))
                .thenReturn(expectedCampaignIds);
        //WHEN
        List<String> actualCampaignIds = campaignClient.findIdsOfUpdatedCampaignsByNotificationUpdateDate(OffsetDateTime.now(Utilities.ZONEID), accessToken);
        //THEN
        Assertions.assertEquals(expectedCampaignIds, actualCampaignIds);
    }

}
