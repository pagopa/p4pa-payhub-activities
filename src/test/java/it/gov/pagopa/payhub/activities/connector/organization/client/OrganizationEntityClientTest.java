package it.gov.pagopa.payhub.activities.connector.organization.client;

import it.gov.pagopa.payhub.activities.connector.organization.config.OrganizationApisHolder;
import it.gov.pagopa.pu.organization.client.generated.OrganizationEntityControllerApi;
import it.gov.pagopa.pu.organization.client.generated.OrganizationSearchControllerApi;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationRequestBody;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrganizationEntityClientTest {

    @Mock
    private OrganizationApisHolder organizationApisHolderMock;
    @Mock
    private OrganizationSearchControllerApi organizationSearchControllerApiMock;
    @Mock
    private OrganizationEntityControllerApi organizationEntityControllerApiMock;

    private OrganizationEntityClient organizationEntityClient;

    @BeforeEach
    void setUp() {
        organizationEntityClient = new OrganizationEntityClient(organizationApisHolderMock);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(
                organizationApisHolderMock
        );
    }

    @Test
    void testCreateOrganization() {
        // Given
        String accessToken = "accessToken";
        Organization expectedOrganization = new Organization();
        OrganizationRequestBody requestBody = new OrganizationRequestBody();

        Mockito.when(organizationApisHolderMock.getOrganizationEntityControllerApi(accessToken))
            .thenReturn(organizationEntityControllerApiMock);
        Mockito.when(organizationEntityControllerApiMock.crudCreateOrganization(requestBody))
            .thenReturn(expectedOrganization);

        // When
        Organization result = organizationEntityClient.createOrganization(requestBody, accessToken);

        // Then
        Assertions.assertSame(expectedOrganization, result);
        Mockito.verify(organizationApisHolderMock).getOrganizationEntityControllerApi(accessToken);
        Mockito.verify(organizationEntityControllerApiMock).crudCreateOrganization(requestBody);
    }

}
