package it.gov.pagopa.payhub.activities.connector.organization;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.organization.client.OrgSilServiceSearchClient;
import it.gov.pagopa.payhub.activities.connector.organization.client.OrganizationSilServiceClient;
import it.gov.pagopa.pu.organization.dto.generated.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class OrgSilServiceServiceTest {

    @Mock
    private AuthnService authnServiceMock;
    @Mock
    private OrgSilServiceSearchClient orgSilServiceSearchClientMock;
    @Mock
    private OrganizationSilServiceClient organizationSilServiceClientMock;

    private OrgSilServiceService orgSilServiceService;

    private final String accessToken = "ACCESSTOKEN";

    @BeforeEach
    void init(){
        orgSilServiceService = new OrgSilServiceServiceImpl(
                authnServiceMock,
                orgSilServiceSearchClientMock,
                organizationSilServiceClientMock
                );

        Mockito.when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(
                authnServiceMock,
                orgSilServiceSearchClientMock,
                organizationSilServiceClientMock
        );
    }

//region getOrgSilServiceByOrgIdAndServiceType tests
    @Test
    void givenExistentOrgIdAndOrgSilServiceTypeWhenGetOrgSilServiceByOrgIdAndServiceTypeThenThenReturnOrgSilService(){
        // Given
        Long orgId = 0L;
        OrgSilServiceType serviceType = OrgSilServiceType.ACTUALIZATION;
        CollectionModelOrgSilService expectedResult = new CollectionModelOrgSilService();
        PagedModelOrgSilServiceEmbedded embedded = new  PagedModelOrgSilServiceEmbedded();
        embedded.setOrgSilServices(List.of(OrgSilService.builder()
                        .organizationId(orgId)
                        .applicationName("Test Application")
                        .serviceUrl("http://test.service.url")
                        .serviceType(serviceType)
                        .flagLegacy(false)
                        .build())
        );
        expectedResult.setEmbedded(embedded);


        Mockito.when(orgSilServiceSearchClientMock.findAllByOrganizationIdAndServiceType(orgId, serviceType, accessToken))
                .thenReturn(expectedResult);

        // When
        List <OrgSilService> result = orgSilServiceService.getOrgSilServiceByOrgIdAndServiceType(orgId, serviceType);

        // Then
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertSame(expectedResult.getEmbedded().getOrgSilServices(), result);
    }
    //endregion

    //region createOrUpdateOrgSilService tests
    @Test
    void createOrUpdateOrgSilServiceWithValidOrgSilServiceDTOReturnsCreatedOrgSilService() {
        // Given
        OrgSilServiceDTO orgSilServiceDTO = new OrgSilServiceDTO();
        Mockito.when(organizationSilServiceClientMock.createOrUpdateOrgSilService(orgSilServiceDTO, accessToken))
                .thenReturn(orgSilServiceDTO);

        // When
        OrgSilServiceDTO result = orgSilServiceService.createOrUpdateOrgSilService(orgSilServiceDTO);

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(orgSilServiceDTO, result);
    }
    //endregion


}
