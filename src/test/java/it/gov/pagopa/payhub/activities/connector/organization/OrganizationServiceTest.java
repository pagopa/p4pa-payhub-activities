package it.gov.pagopa.payhub.activities.connector.organization;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.organization.client.OrganizationSearchClient;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class OrganizationServiceTest {

    @Mock
    private AuthnService authnServiceMock;
    @Mock
    private OrganizationSearchClient organizationSearchClientMock;

    private OrganizationService organizationService;

    private final String accessToken = "ACCESSTOKEN";

    @BeforeEach
    void init(){
        organizationService = new OrganizationServiceImpl(
                authnServiceMock,
                organizationSearchClientMock
        );

        Mockito.when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(
                authnServiceMock,
                organizationSearchClientMock
        );
    }

//region getOrganizationByFiscalCode tests
    @Test
    void givenNotExistentFiscalCodeWhenGetOrganizationByFiscalCodeThenEmpty(){
        // Given
        String orgFiscalCode = "ORGFISCALCODE";
        Mockito.when(organizationSearchClientMock.findByOrgFiscalCode(orgFiscalCode, accessToken))
                .thenReturn(null);

        // When
        Optional<Organization> result = organizationService.getOrganizationByFiscalCode(orgFiscalCode);

        // Then
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void givenExistentFiscalCodeWhenGetOrganizationByFiscalCodeThenEmpty(){
        // Given
        String orgFiscalCode = "ORGFISCALCODE";
        Organization expectedResult = new Organization();
        Mockito.when(organizationSearchClientMock.findByOrgFiscalCode(orgFiscalCode, accessToken))
                .thenReturn(expectedResult);

        // When
        Optional<Organization> result = organizationService.getOrganizationByFiscalCode(orgFiscalCode);

        // Then
        Assertions.assertTrue(result.isPresent());
        Assertions.assertSame(expectedResult, result.get());
    }
//endregion

//region getOrganizationByIpaCode tests
    @Test
    void givenNotExistentFiscalCodeWhenGetOrganizationByIpaCodeThenEmpty(){
        // Given
        String orgIpaCode = "ORGIPACODE";
        Mockito.when(organizationSearchClientMock.findByIpaCode(orgIpaCode, accessToken))
                .thenReturn(null);

        // When
        Optional<Organization> result = organizationService.getOrganizationByIpaCode(orgIpaCode);

        // Then
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void givenExistentFiscalCodeWhenGetOrganizationByIpaCodeThenEmpty(){
        // Given
        String orgIpaCode = "ORGIPACODE";
        Organization expectedResult = new Organization();
        Mockito.when(organizationSearchClientMock.findByIpaCode(orgIpaCode, accessToken))
                .thenReturn(expectedResult);

        // When
        Optional<Organization> result = organizationService.getOrganizationByIpaCode(orgIpaCode);

        // Then
        Assertions.assertTrue(result.isPresent());
        Assertions.assertSame(expectedResult, result.get());
    }
//endregion

//region getOrganizationByOrganizationId tests
    @Test
    void givenNotExistentFiscalCodeWhenGetOrganizationByOrganizationIdThenEmpty(){
        // Given
        Long orgIpaCode = 123L;
        Mockito.when(organizationSearchClientMock.findById(orgIpaCode, accessToken))
                .thenReturn(null);

        // When
        Optional<Organization> result = organizationService.getOrganizationById(orgIpaCode);

        // Then
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void givenExistentFiscalCodeWhenGetOrganizationByOrganizationIdThenEmpty(){
        // Given
        Long orgIpaCode = 123L;
        Organization expectedResult = new Organization();
        Mockito.when(organizationSearchClientMock.findById(orgIpaCode, accessToken))
                .thenReturn(expectedResult);

        // When
        Optional<Organization> result = organizationService.getOrganizationById(orgIpaCode);

        // Then
        Assertions.assertTrue(result.isPresent());
        Assertions.assertSame(expectedResult, result.get());
    }
//endregion
}
