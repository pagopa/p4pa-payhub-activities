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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FetchUpdatedSendCampaignsActivityTest {
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
        when(campaignServiceMock.findIdsOfUpdatedCampaignsByNotificationUpdateDate(latestFullRecalculationDate)).thenReturn(expectedCampaignIds);
        //WHEN
        List<String> actualCampaignIds = fetchUpdatedSendCampaignsActivity.fetchIdsForUpdatedSendCampaigns(latestFullRecalculationDate);
        //THEN
        assertEquals(expectedCampaignIds, actualCampaignIds);
    }
}
