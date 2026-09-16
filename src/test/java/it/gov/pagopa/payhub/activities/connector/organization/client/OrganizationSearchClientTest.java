package it.gov.pagopa.payhub.activities.connector.organization.client;

import it.gov.pagopa.payhub.activities.connector.organization.config.OrganizationApisHolder;
import it.gov.pagopa.payhub.activities.exception.common.RestInvokeNotFoundException;
import it.gov.pagopa.pu.organization.client.generated.OrganizationEntityControllerApi;
import it.gov.pagopa.pu.organization.client.generated.OrganizationSearchControllerApi;
import it.gov.pagopa.pu.organization.dto.generated.CollectionModelOrganization;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationStatus;
import it.gov.pagopa.pu.organization.dto.generated.PagedModelOrganizationEmbedded;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrganizationSearchClientTest {

    @Mock
    private OrganizationApisHolder organizationApisHolderMock;
    @Mock
    private OrganizationSearchControllerApi organizationSearchControllerApiMock;
    @Mock
    private OrganizationEntityControllerApi organizationEntityControllerApiMock;

    private OrganizationSearchClient organizationSearchClient;

    @BeforeEach
    void setUp() {
        organizationSearchClient = new OrganizationSearchClient(organizationApisHolderMock);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(
                organizationApisHolderMock
        );
    }

    @Test
    void whenFindByIpaCodeThenInvokeWithAccessToken(){
        // Given
        String accessToken = "ACCESSTOKEN";
        String orgIpaCode = "ORGIPACODE";
        Organization expectedResult = new Organization();

        when(organizationApisHolderMock.getOrganizationSearchControllerApi(accessToken))
                .thenReturn(organizationSearchControllerApiMock);
        when(organizationSearchControllerApiMock.crudOrganizationsFindByIpaCode(orgIpaCode))
                .thenReturn(expectedResult);

        // When
        Organization result = organizationSearchClient.findByIpaCode(orgIpaCode, accessToken);

        // Then
        Assertions.assertSame(expectedResult, result);
    }

    @Test
    void givenNotExistentOrganizationWhenFindByIpaCodeThenNull(){
        // Given
        String accessToken = "ACCESSTOKEN";
        String orgIpaCode = "ORGIPACODE";

        when(organizationApisHolderMock.getOrganizationSearchControllerApi(accessToken))
                .thenReturn(organizationSearchControllerApiMock);
        when(organizationSearchControllerApiMock.crudOrganizationsFindByIpaCode(orgIpaCode))
                .thenThrow(new RestInvokeNotFoundException("APPNAME", HttpStatus.NOT_FOUND, "ERROR", "ERRORCODE", "ERRORMESSAGE"));

        // When
        Organization result = organizationSearchClient.findByIpaCode(orgIpaCode, accessToken);

        // Then
        Assertions.assertNull(result);
    }

    @Test
    void whenGetOrgFiscalCodeThenInvokeWithAccessToken(){
        // Given
        String accessToken = "ACCESSTOKEN";
        String orgFiscalCode = "ORGFISCALCODE";
        Organization expectedResult = new Organization();

        when(organizationApisHolderMock.getOrganizationSearchControllerApi(accessToken))
                .thenReturn(organizationSearchControllerApiMock);
        when(organizationSearchControllerApiMock.crudOrganizationsFindByOrgFiscalCode(orgFiscalCode))
                .thenReturn(expectedResult);

        // When
        Organization result = organizationSearchClient.findByOrgFiscalCode(orgFiscalCode, accessToken);

        // Then
        Assertions.assertSame(expectedResult, result);
    }

    @Test
    void givenNotExistentOrganizationWhenGetOrgFiscalCodeThenNull(){
        // Given
        String accessToken = "ACCESSTOKEN";
        String orgFiscalCode = "ORGFISCALCODE";

        when(organizationApisHolderMock.getOrganizationSearchControllerApi(accessToken))
                .thenReturn(organizationSearchControllerApiMock);
        when(organizationSearchControllerApiMock.crudOrganizationsFindByOrgFiscalCode(orgFiscalCode))
                .thenThrow(new RestInvokeNotFoundException("APPNAME", HttpStatus.NOT_FOUND, "ERROR", "ERRORCODE", "ERRORMESSAGE"));

        // When
        Organization result = organizationSearchClient.findByOrgFiscalCode(orgFiscalCode, accessToken);

        // Then
        Assertions.assertNull(result);
    }

    @Test
    void whenFindByIdThenInvokeWithAccessToken() {
        // Given
        String accessToken = "ACCESSTOKEN";
        Long organizationId = 1L;
        Organization expectedResult = new Organization();

        when(organizationApisHolderMock.getOrganizationEntityControllerApi(accessToken))
                .thenReturn(organizationEntityControllerApiMock);
        when(organizationEntityControllerApiMock.crudGetOrganization(String.valueOf(organizationId)))
                .thenReturn(expectedResult);

        // When
        Organization result = organizationSearchClient.findById(organizationId, accessToken);

        // Then
        Assertions.assertSame(expectedResult, result);
    }

    @Test
    void givenNotExistentOrganizationWhenFindByIdThenNull() {
        // Given
        String accessToken = "ACCESSTOKEN";
        Long organizationId = 1L;

        when(organizationApisHolderMock.getOrganizationEntityControllerApi(accessToken))
                .thenReturn(organizationEntityControllerApiMock);
        when(organizationEntityControllerApiMock.crudGetOrganization(String.valueOf(organizationId)))
                .thenThrow(new RestInvokeNotFoundException("APPNAME", HttpStatus.NOT_FOUND, "ERROR", "ERRORCODE", "ERRORMESSAGE"));

        // When
        Organization result = organizationSearchClient.findById(organizationId, accessToken);

        // Then
        Assertions.assertNull(result);
    }

    @Test
    void whenFindActiveOrganizationsByBrokerIdThenInvokeWithAccessToken() {
        // Given
        String accessToken = "ACCESSTOKEN";
        Long brokerId = 1L;
        PagedModelOrganizationEmbedded embedded = new PagedModelOrganizationEmbedded();
        CollectionModelOrganization expectedResult = new CollectionModelOrganization().embedded(embedded);

        when(organizationApisHolderMock.getOrganizationSearchControllerApi(accessToken))
                .thenReturn(organizationSearchControllerApiMock);
        when(organizationSearchControllerApiMock.crudOrganizationsFindByBrokerIdAndStatus(brokerId, OrganizationStatus.ACTIVE))
                .thenReturn(expectedResult);
        // When
        CollectionModelOrganization result = organizationSearchClient.findActiveOrganizationsByBrokerId(brokerId, accessToken);

        // Then
        Assertions.assertSame(expectedResult, result);
    }
}
