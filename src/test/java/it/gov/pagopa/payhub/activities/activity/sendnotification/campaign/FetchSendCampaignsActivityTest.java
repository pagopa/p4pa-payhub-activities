package it.gov.pagopa.payhub.activities.activity.sendnotification.campaign;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class FetchSendCampaignsActivityTest {
    private FetchSendCampaignsActivity fetchSendCampaignsActivity;

    @BeforeEach
    void init() {
        fetchSendCampaignsActivity = new FetchSendCampaignsActivityImpl();
    }

    @Test
    void whenFetchSendCampaignIdsThenOk() {
        List<String> res = fetchSendCampaignsActivity.fetchSendCampaignIds();
        assertEquals(Collections.emptyList(), res);
    }
}
