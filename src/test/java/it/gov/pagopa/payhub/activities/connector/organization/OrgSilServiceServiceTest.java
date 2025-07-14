package it.gov.pagopa.payhub.activities.connector.organization;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.organization.client.OrgSilServiceSearchControllerClient;
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

import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class OrgSilServiceServiceTest {

    @Mock
    private AuthnService authnServiceMock;
    @Mock
    private OrganizationSilServiceClient organizationSilServiceClientMock;

    @Mock
    private OrgSilServiceSearchControllerClient orgSilServiceSearchControllerClientMock;

    private OrgSilServiceService orgSilServiceService;

    private final String accessToken = "ACCESSTOKEN";

    @BeforeEach
    void init(){
        orgSilServiceService = new OrgSilServiceServiceImpl(
                authnServiceMock,
                organizationSilServiceClientMock,
                orgSilServiceSearchControllerClientMock
                );

        Mockito.when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(
                authnServiceMock,
                organizationSilServiceClientMock,
                orgSilServiceSearchControllerClientMock
        );
    }

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


    //region findAllByOrganizationIdAndServiceType tests
    @Test
    void givenNotMatchingDataWhenFindAllByOrganizationIdAndServiceTypeThenReturnEmpty(){
        // Given
        Long orgId = 123L;
        PagedModelOrgSilServiceEmbedded embedded = mock(PagedModelOrgSilServiceEmbedded.class);
        CollectionModelOrgSilService expectedResponse = new CollectionModelOrgSilService().embedded(embedded);
        Mockito.when(orgSilServiceSearchControllerClientMock.findAllByOrganizationIdAndServiceType(orgId, OrgSilServiceType.ACTUALIZATION,accessToken))
                .thenReturn(expectedResponse);

        // When
        List<OrgSilService> result = orgSilServiceService.getAllByOrganizationIdAndServiceType(orgId, OrgSilServiceType.ACTUALIZATION);

        // Then
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void givenMatchingDataWhenFindAllByOrganizationIdAndServiceTypeThenReturnList(){
        // Given
        Long orgId = 123L;
        List<OrgSilService> organizations = List.of(new OrgSilService());
        PagedModelOrgSilServiceEmbedded embedded = new PagedModelOrgSilServiceEmbedded(organizations);
        CollectionModelOrgSilService expectedResponse = new CollectionModelOrgSilService().embedded(embedded);
        Mockito.when(orgSilServiceSearchControllerClientMock.findAllByOrganizationIdAndServiceType(orgId, OrgSilServiceType.ACTUALIZATION, accessToken))
                .thenReturn(expectedResponse);

        // When
        List<OrgSilService> result = orgSilServiceService.getAllByOrganizationIdAndServiceType(orgId, OrgSilServiceType.ACTUALIZATION);

        // Then
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertSame(embedded.getOrgSilServices(), result);
    }
    //end region

}
