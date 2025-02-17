package it.gov.pagopa.payhub.activities.connector.organization.client;

import it.gov.pagopa.payhub.activities.connector.organization.config.OrganizationApisHolder;
import it.gov.pagopa.pu.organization.client.generated.OrganizationEntityControllerApi;
import it.gov.pagopa.pu.organization.client.generated.OrganizationSearchControllerApi;
import it.gov.pagopa.pu.organization.dto.generated.CollectionModelOrganization;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
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
import org.springframework.web.client.HttpClientErrorException;

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

        Mockito.when(organizationApisHolderMock.getOrganizationSearchControllerApi(accessToken))
                .thenReturn(organizationSearchControllerApiMock);
        Mockito.when(organizationSearchControllerApiMock.crudOrganizationsFindByIpaCode(orgIpaCode))
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

        Mockito.when(organizationApisHolderMock.getOrganizationSearchControllerApi(accessToken))
                .thenReturn(organizationSearchControllerApiMock);
        Mockito.when(organizationSearchControllerApiMock.crudOrganizationsFindByIpaCode(orgIpaCode))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));

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

        Mockito.when(organizationApisHolderMock.getOrganizationSearchControllerApi(accessToken))
                .thenReturn(organizationSearchControllerApiMock);
        Mockito.when(organizationSearchControllerApiMock.crudOrganizationsFindByOrgFiscalCode(orgFiscalCode))
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

        Mockito.when(organizationApisHolderMock.getOrganizationSearchControllerApi(accessToken))
                .thenReturn(organizationSearchControllerApiMock);
        Mockito.when(organizationSearchControllerApiMock.crudOrganizationsFindByOrgFiscalCode(orgFiscalCode))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));

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

        Mockito.when(organizationApisHolderMock.getOrganizationEntityControllerApi(accessToken))
                .thenReturn(organizationEntityControllerApiMock);
        Mockito.when(organizationEntityControllerApiMock.crudGetOrganization(String.valueOf(organizationId)))
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

        Mockito.when(organizationApisHolderMock.getOrganizationEntityControllerApi(accessToken))
                .thenReturn(organizationEntityControllerApiMock);
        Mockito.when(organizationEntityControllerApiMock.crudGetOrganization(String.valueOf(organizationId)))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));

        // When
        Organization result = organizationSearchClient.findById(organizationId, accessToken);

        // Then
        Assertions.assertNull(result);
    }

    @Test
    void whenFindOrganizationsByBrokerIdThenInvokeWithAccessToken() {
        // Given
        String accessToken = "ACCESSTOKEN";
        Long brokerId = 1L;
        PagedModelOrganizationEmbedded embedded = new PagedModelOrganizationEmbedded();
        CollectionModelOrganization expectedResult = new CollectionModelOrganization().embedded(embedded);

        Mockito.when(organizationApisHolderMock.getOrganizationSearchControllerApi(accessToken))
                .thenReturn(organizationSearchControllerApiMock);
        Mockito.when(organizationSearchControllerApiMock.crudOrganizationsFindByBrokerId(brokerId))
                .thenReturn(expectedResult);
        // When
        CollectionModelOrganization result = organizationSearchClient.findOrganizationsByBrokerId(brokerId, accessToken);

        // Then
        Assertions.assertSame(expectedResult, result);
    }
}
