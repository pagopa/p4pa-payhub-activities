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
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

@ExtendWith(MockitoExtension.class)
class OrgSilServiceSearchClientTest {

    @Mock
    private OrganizationApisHolder organizationApisHolderMock;
    @Mock
    private OrgSilServiceSearchControllerApi orgSilServiceSearchControllerApiMock;

    private OrgSilServiceSearchClient orgSilServiceSearchClient;

    @BeforeEach
    void setUp() {
        orgSilServiceSearchClient = new OrgSilServiceSearchClient(organizationApisHolderMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                organizationApisHolderMock
        );
    }

    @Test
    void whenFindAllByOrganizationIdAndServiceTypeThenInvokeWithAccessToken() {
        // Given
        String accessToken = "ACCESSTOKEN";
        Long orgId = 0L;
        OrgSilServiceType serviceType = OrgSilServiceType.PAID_NOTIFICATION_OUTCOME;
        CollectionModelOrgSilService expectedResult = new CollectionModelOrgSilService();

        Mockito.when(organizationApisHolderMock.getOrgSilServiceSearchControllerApi(accessToken))
                .thenReturn(orgSilServiceSearchControllerApiMock);
        Mockito.when(orgSilServiceSearchControllerApiMock.crudOrgSilServicesFindAllByOrganizationIdAndServiceType(orgId, serviceType))
                .thenReturn(expectedResult);

        // When
        CollectionModelOrgSilService result = orgSilServiceSearchClient.findAllByOrganizationIdAndServiceType(orgId, serviceType, accessToken);

        // Then
        Assertions.assertSame(expectedResult, result);
    }

    @Test
    void whenFindAllByOrganizationIdAndServiceTypeThenNull() {
        // Given
        String accessToken = "ACCESSTOKEN";
        Long orgId = 0L;
        OrgSilServiceType serviceType = OrgSilServiceType.PAID_NOTIFICATION_OUTCOME;
        Mockito.when(organizationApisHolderMock.getOrgSilServiceSearchControllerApi(accessToken))
                .thenReturn(orgSilServiceSearchControllerApiMock);
        Mockito.when(orgSilServiceSearchControllerApiMock.crudOrgSilServicesFindAllByOrganizationIdAndServiceType(orgId,serviceType))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));

        // When
        CollectionModelOrgSilService result = orgSilServiceSearchClient.findAllByOrganizationIdAndServiceType(orgId, serviceType, accessToken);

        // Then
        Assertions.assertNull(result);
    }
}
