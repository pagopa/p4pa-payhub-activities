package it.gov.pagopa.payhub.activities.connector.organization.client;

import it.gov.pagopa.payhub.activities.connector.organization.config.OrganizationApisHolder;
import it.gov.pagopa.pu.organization.client.generated.OrganizationSilServiceApi;
import it.gov.pagopa.pu.organization.dto.generated.OrgSilServiceDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrganizationSilServiceClientTest {

    @Mock
    private OrganizationApisHolder organizationApisHolderMock;
    @Mock
    private OrganizationSilServiceApi organizationSilServiceApiMock;

    private OrganizationSilServiceClient organizationSilServiceClient;

    @BeforeEach
    void setUp() {
        organizationSilServiceClient = new OrganizationSilServiceClient(organizationApisHolderMock);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(
                organizationApisHolderMock
        );
    }

    @Test
    void testCreateOrUpdateOrgSilService() {
        // Given
        String accessToken = "accessToken";
        OrgSilServiceDTO expectedOrgSilService = new OrgSilServiceDTO();
        OrgSilServiceDTO orgSilService = new OrgSilServiceDTO();

        Mockito.when(organizationApisHolderMock.getOrganizationSilServiceApi(accessToken))
            .thenReturn(organizationSilServiceApiMock);
        Mockito.when(organizationSilServiceApiMock.createOrUpdateOrgSilService(orgSilService))
            .thenReturn(expectedOrgSilService);

        // When
        OrgSilServiceDTO result = organizationSilServiceClient.createOrUpdateOrgSilService(orgSilService, accessToken);

        // Then
        Assertions.assertSame(expectedOrgSilService, result);
    }

}
