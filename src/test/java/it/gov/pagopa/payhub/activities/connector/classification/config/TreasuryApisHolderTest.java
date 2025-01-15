package it.gov.pagopa.payhub.activities.connector.classification.config;

import it.gov.pagopa.pu.classification.client.generated.TreasuryEntityControllerApi;
import it.gov.pagopa.pu.classification.client.generated.TreasuryEntityExtendedControllerApi;
import it.gov.pagopa.pu.classification.client.generated.TreasurySearchControllerApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TreasuryApisHolderTest {

    @Mock
    private RestTemplateBuilder restTemplateBuilder;

    private TreasuryApisHolder treasuryApisHolder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(restTemplateBuilder.build()).thenReturn(mock(org.springframework.web.client.RestTemplate.class));
        treasuryApisHolder = new TreasuryApisHolder("http://localhost", restTemplateBuilder);
    }

    @Test
    void testGetTreasurySearchApi() {
        String accessToken = "accessToken";
        TreasurySearchControllerApi api = treasuryApisHolder.getTreasurySearchApi(accessToken);
        assertNotNull(api);
    }

    @Test
    void testGetTreasuryEntityControllerApi() {
        String accessToken = "accessToken";
        TreasuryEntityControllerApi api = treasuryApisHolder.getTreasuryEntityControllerApi(accessToken);
        assertNotNull(api);
    }

    @Test
    void testGetTreasuryEntityExtendedControllerApi() {
        String accessToken = "accessToken";
        TreasuryEntityExtendedControllerApi api = treasuryApisHolder.getTreasuryEntityExtendedControllerApi(accessToken);
        assertNotNull(api);
    }

}