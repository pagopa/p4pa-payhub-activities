package it.gov.pagopa.payhub.activities.connector.organization;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.organization.client.OrganizationSilServiceClient;
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
class OrgSilServiceServiceTest {

    @Mock
    private AuthnService authnServiceMock;
    @Mock
    private OrganizationSilServiceClient organizationSilServiceClientMock;

    private OrgSilServiceService orgSilServiceService;

    private final String accessToken = "ACCESSTOKEN";

    @BeforeEach
    void init(){
        orgSilServiceService = new OrgSilServiceServiceImpl(
                authnServiceMock,
                organizationSilServiceClientMock
                );

        Mockito.when(authnServiceMock.getAccessToken())
                .thenReturn(accessToken);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(
                authnServiceMock,
                organizationSilServiceClientMock
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


}
