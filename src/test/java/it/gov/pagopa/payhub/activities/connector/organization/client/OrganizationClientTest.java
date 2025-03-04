package it.gov.pagopa.payhub.activities.connector.organization.client;

import it.gov.pagopa.payhub.activities.connector.organization.config.OrganizationApisHolder;
import it.gov.pagopa.pu.organization.client.generated.OrganizationApi;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrganizationClientTest {

    @Mock
    private OrganizationApisHolder organizationApisHolderMock;
    @Mock
    private OrganizationApi organizationApiMock;

    private OrganizationClient organizationClient;

    @BeforeEach
    void setUp() {
        organizationClient = new OrganizationClient(organizationApisHolderMock);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(
                organizationApisHolderMock,
                organizationApiMock
        );
    }

    @Test
    void whenFindByIpaCodeThenInvokeWithAccessToken(){
        // Given
        String accessToken = "ACCESSTOKEN";

        Mockito.when(organizationApisHolderMock.getOrganizationApi(accessToken))
                .thenReturn(organizationApiMock);
        Mockito.when(organizationApiMock.getOrganizationApiKey(1L, "operationType"))
                .thenReturn("expectedResult");

        // When
        String result = organizationClient.getOrganizationApiKey(1L, "operationType", accessToken);

        // Then
        Assertions.assertSame("expectedResult", result);
    }
}
