package it.gov.pagopa.payhub.activities.connector.organization.client;

import it.gov.pagopa.payhub.activities.connector.organization.config.OrganizationApisHolder;
import it.gov.pagopa.pu.organization.client.generated.OrganizationApi;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationApiKeys;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrganizationApiClientTest {

    @Mock
    private OrganizationApisHolder organizationApisHolderMock;
    @Mock
    private OrganizationApi organizationApiMock;

    private OrganizationApiClient client;

    @BeforeEach
    void setUp() {
        client = new OrganizationApiClient(organizationApisHolderMock);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(
                organizationApisHolderMock
        );
    }

    @Test
    void encryptAndSaveApiKeyShouldCallApiWithCorrectParameters() {
        Long organizationId = 123L;
        OrganizationApiKeys apiKeys = new OrganizationApiKeys();
        String accessToken = "accessToken";

        Mockito.when(organizationApisHolderMock.getOrganizationApi(accessToken))
            .thenReturn(organizationApiMock);

        client.encryptAndSaveApiKey(organizationId, apiKeys, accessToken);

        Mockito.verify(organizationApisHolderMock).getOrganizationApi(accessToken);
        Mockito.verify(organizationApiMock).encryptAndSaveApiKey(organizationId, apiKeys);
    }

    @Test
    void encryptAndSaveApiKeyShouldHandleNullApiKeys() {
        Long organizationId = 123L;
        String accessToken = "accessToken";

        Mockito.when(organizationApisHolderMock.getOrganizationApi(accessToken))
            .thenReturn(organizationApiMock);

        client.encryptAndSaveApiKey(organizationId, null, accessToken);

        Mockito.verify(organizationApisHolderMock).getOrganizationApi(accessToken);
        Mockito.verify(organizationApiMock).encryptAndSaveApiKey(organizationId, null);
    }

}
