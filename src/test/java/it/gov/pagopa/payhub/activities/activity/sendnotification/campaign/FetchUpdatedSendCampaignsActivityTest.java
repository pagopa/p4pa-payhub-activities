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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class FetchUpdatedSendCampaignsActivityTest {
    @Mock
    private CampaignService campaignServiceMock;

    private FetchUpdatedSendCampaignsActivity fetchUpdatedSendCampaignsActivity;

    @BeforeEach
    void init() {
        fetchUpdatedSendCampaignsActivity = new FetchUpdatedSendCampaignsActivityImpl(campaignServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(campaignServiceMock);
    }

    @Test
    void givenLatestFullRecalculationDateWhenFetchIdsForUpdatedSendCampaignsThenReturnFilteredId() {
        //GIVEN
        List<String> expectedCampaignIds = List.of("campaignId1", "campaignId2");
        OffsetDateTime latestFullRecalculationDate = OffsetDateTime.now(Utilities.ZONEID);
        Mockito.when(campaignServiceMock.findLatestFullRecalculationDate()).thenReturn(latestFullRecalculationDate);
        Mockito.when(campaignServiceMock.findIdsOfUpdatedCampaignsByNotificationUpdateDate(Mockito.any(OffsetDateTime.class))).thenReturn(expectedCampaignIds);
        //WHEN
        List<String> actualCampaignIds = fetchUpdatedSendCampaignsActivity.fetchIdsForUpdatedSendCampaigns();
        //THEN
        assertEquals(expectedCampaignIds, actualCampaignIds);
    }

    @Test
    void givenNoLatestFullRecalculationDateWhenFetchIdsForUpdatedSendCampaignsThenReturnFilteredId() {
        //GIVEN
        List<String> expectedCampaignIds = List.of("campaignId1", "campaignId2", "campaignId3");
        Mockito.when(campaignServiceMock.findLatestFullRecalculationDate()).thenReturn(null);
        Mockito.when(campaignServiceMock.fetchAllCampaignIds()).thenReturn(expectedCampaignIds);
        //WHEN
        List<String> actualCampaignIds = fetchUpdatedSendCampaignsActivity.fetchIdsForUpdatedSendCampaigns();
        //THEN
        assertEquals(expectedCampaignIds, actualCampaignIds);
    }
}
