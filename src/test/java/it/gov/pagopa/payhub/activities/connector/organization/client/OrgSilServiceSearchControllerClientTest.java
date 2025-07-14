package it.gov.pagopa.payhub.activities.connector.organization.client;

import it.gov.pagopa.payhub.activities.connector.organization.config.OrganizationApisHolder;
import it.gov.pagopa.pu.organization.client.generated.OrgSilServiceSearchControllerApi;
import it.gov.pagopa.pu.organization.dto.generated.CollectionModelOrgSilService;
import it.gov.pagopa.pu.organization.dto.generated.OrgSilServiceType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrgSilServiceSearchControllerClientTest {

    @Mock
    private OrganizationApisHolder organizationApisHolderMock;
    @Mock
    private OrgSilServiceSearchControllerApi orgSilServiceSearchControllerApi;

    private OrgSilServiceSearchControllerClient orgSilServiceSearchControllerClient;

    @BeforeEach
    void setUp() {
        orgSilServiceSearchControllerClient = new OrgSilServiceSearchControllerClient(organizationApisHolderMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                organizationApisHolderMock
        );
    }

    @Test
    void testFindAllByOrganizationIdAndServiceType() {
        // Given
        String accessToken = "accessToken";
        Long orgId = 123L;
        CollectionModelOrgSilService expectedOrgSilService = new CollectionModelOrgSilService();

        Mockito.when(organizationApisHolderMock.getOrgSilServiceSearchControllerApi(accessToken))
                .thenReturn(orgSilServiceSearchControllerApi);
        Mockito.when(orgSilServiceSearchControllerApi.crudOrgSilServicesFindAllByOrganizationIdAndServiceType(orgId, OrgSilServiceType.ACTUALIZATION))
                .thenReturn(expectedOrgSilService);

        // When
        CollectionModelOrgSilService result = orgSilServiceSearchControllerClient.findAllByOrganizationIdAndServiceType(orgId, OrgSilServiceType.ACTUALIZATION, accessToken);

        // Then
        Assertions.assertSame(expectedOrgSilService, result);
    }

}
