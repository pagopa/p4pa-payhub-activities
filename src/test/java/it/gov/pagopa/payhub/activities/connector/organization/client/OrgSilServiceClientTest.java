package it.gov.pagopa.payhub.activities.connector.organization.client;

import it.gov.pagopa.payhub.activities.connector.organization.config.OrganizationApisHolder;
import it.gov.pagopa.pu.organization.client.generated.OrgSilServiceEntityControllerApi;
import it.gov.pagopa.pu.organization.dto.generated.OrgSilService;
import it.gov.pagopa.pu.organization.dto.generated.OrgSilServiceRequestBody;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrgSilServiceClientTest {

    @Mock
    private OrganizationApisHolder organizationApisHolderMock;
    @Mock
    private OrgSilServiceEntityControllerApi orgSilServiceEntityControllerApiMock;

    private OrgSilServiceClient client;

    @BeforeEach
    void setUp() {
        client = new OrgSilServiceClient(organizationApisHolderMock);
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
        OrgSilService expectedOrgSilService = new OrgSilService();
        OrgSilServiceRequestBody requestBody = new OrgSilServiceRequestBody();

        Mockito.when(organizationApisHolderMock.getOrgSilServiceEntityControllerApi(accessToken))
            .thenReturn(orgSilServiceEntityControllerApiMock);
        Mockito.when(orgSilServiceEntityControllerApiMock.crudCreateOrgsilservice(requestBody))
            .thenReturn(expectedOrgSilService);

        // When
        OrgSilService result = client.createOrgSilService(requestBody, accessToken);

        // Then
        Assertions.assertSame(expectedOrgSilService, result);
    }

}
