package it.gov.pagopa.payhub.activities.connector.sendnotification;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.sendnotification.client.CampaignClient;
import it.gov.pagopa.payhub.activities.util.Utilities;
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

@ExtendWith(MockitoExtension.class)
class CampaignServiceTest {
    @Mock
    private CampaignClient campaignClientMock;
    @Mock
    private AuthnService authnServiceMock;

    private CampaignService campaignService;

    @BeforeEach
    void setUp() {
        campaignService = new CampaignServiceImpl(campaignClientMock, authnServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(campaignClientMock, authnServiceMock);
    }

    @Test
    void whenFindLatestFullRecalculationDateThenOk() {
        //GIVEN
        String accessToken = "accessToken";
        OffsetDateTime expectedLatestFullRecalculationDate = OffsetDateTime.now(Utilities.ZONEID);

        Mockito.when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);
        Mockito.when(campaignClientMock.findLatestFullRecalculationDate(Mockito.eq(accessToken)))
                .thenReturn(expectedLatestFullRecalculationDate);
        //WHEN
        OffsetDateTime actualLatestFullRecalculationDate = campaignService.findLatestFullRecalculationDate();
        //THEN
        Assertions.assertEquals(expectedLatestFullRecalculationDate, actualLatestFullRecalculationDate);
    }

    @Test
    void whenFindIdsOfUpdatedCampaignsByNotificationUpdateDateThenOk() {
        //GIVEN
        String accessToken = "accessToken";
        List<String> expectedCampaignIds = List.of("campaignId");

        Mockito.when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);
        Mockito.when(
                campaignClientMock.findIdsOfUpdatedCampaignsByNotificationUpdateDate(
                        Mockito.any(OffsetDateTime.class),
                        Mockito.eq(accessToken)
                )
        ).thenReturn(expectedCampaignIds);
        //WHEN
        List<String> actualCampaignIds = campaignService.findIdsOfUpdatedCampaignsByNotificationUpdateDate(OffsetDateTime.now(Utilities.ZONEID));
        //THEN
        Assertions.assertEquals(expectedCampaignIds, actualCampaignIds);
    }

    @Test
    void whenFetchAllCampaignIdsThenOk() {
        String accessToken = "accessToken";
        List<String> campaignIds = List.of("campaignId");

        Mockito.when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
        Mockito.when(campaignClientMock.fetchAllCampaignIds(accessToken)).thenReturn(campaignIds);

        List<String> actualCampaignIds = campaignService.fetchAllCampaignIds();

        Assertions.assertEquals(campaignIds, actualCampaignIds);
    }

    @Test
    void whenAlignCampaignThenOk() {
        String accessToken = "accessToken";
        String campaignId = "campaignId";

        Mockito.when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
        Mockito.doNothing().when(campaignClientMock).alignCampaign(campaignId, accessToken);

        Assertions.assertDoesNotThrow(() -> campaignService.alignCampaign(campaignId));
    }
}
