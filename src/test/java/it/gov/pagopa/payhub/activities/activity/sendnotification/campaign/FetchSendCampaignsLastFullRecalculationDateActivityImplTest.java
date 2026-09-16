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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FetchSendCampaignsLastFullRecalculationDateActivityImplTest {

    @Mock
    private CampaignService campaignServiceMock;

    private FetchSendCampaignsLastFullRecalculationDateActivity fetchSendCampaignsLastFullRecalculationDateActivity;

    @BeforeEach
    void init() {
        fetchSendCampaignsLastFullRecalculationDateActivity = new FetchSendCampaignsLastFullRecalculationDateActivityImpl(campaignServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(campaignServiceMock);
    }

    @Test
    void givenFullRecalculationDateWhenFetchSendCampaignsLastFullRecalculationDateThenReturnLatestFullRecalculationDate() {
        //GIVEN
        OffsetDateTime expectedLatestFullRecalculationDate = OffsetDateTime.now(Utilities.ZONEID);
        when(campaignServiceMock.findLatestFullRecalculationDate()).thenReturn(expectedLatestFullRecalculationDate);
        //WHEN
        java.time.OffsetDateTime actualLatestFullRecalculationDate  = fetchSendCampaignsLastFullRecalculationDateActivity.fetchSendCampaignsLastFullRecalculationDate();
        //THEN
        assertEquals(expectedLatestFullRecalculationDate, actualLatestFullRecalculationDate);
        verify(campaignServiceMock, times(0)).findFirstCampaignStartDate();
    }

    @Test
    void givenNoFullRecalculationDateWhenFetchSendCampaignsLastFullRecalculationDateThenReturnFirstCampaignStartDate() {
        //GIVEN
        OffsetDateTime expectedFirstCampaignStartDate = OffsetDateTime.now(Utilities.ZONEID);
        when(campaignServiceMock.findLatestFullRecalculationDate()).thenReturn(null);
        when(campaignServiceMock.findFirstCampaignStartDate()).thenReturn(expectedFirstCampaignStartDate);
        //WHEN
        java.time.OffsetDateTime actualLatestFullRecalculationDate  = fetchSendCampaignsLastFullRecalculationDateActivity.fetchSendCampaignsLastFullRecalculationDate();
        //THEN
        assertEquals(expectedFirstCampaignStartDate, actualLatestFullRecalculationDate);
    }
}