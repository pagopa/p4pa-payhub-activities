package it.gov.pagopa.payhub.activities.service.exportflow.email;

import it.gov.pagopa.payhub.activities.connector.auth.AuthzService;
import it.gov.pagopa.payhub.activities.dto.email.TemplatedEmailDTO;
import it.gov.pagopa.pu.auth.dto.generated.UserInfo;
import it.gov.pagopa.pu.auth.dto.generated.UserOrganizationRoles;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExportFileEmailDestinationRetrieverServiceTest {

    @Mock
    private AuthzService authzServiceMock;

    private PodamFactory podamFactory;
    private ExportFileEmailDestinationRetrieverService exportFileEmailDestinationRetrieverService;

    @BeforeEach
    void setUp() {
        exportFileEmailDestinationRetrieverService = new ExportFileEmailDestinationRetrieverService(authzServiceMock);
        podamFactory = new PodamFactoryImpl();
    }

    @Test
    void givenExportFileAndOrganizationWhenRetrieveEmailDestinationsThenReturnTemplateEmailDTO() {
        //given
        ExportFile exportFile = podamFactory.manufacturePojo(ExportFile.class);
        exportFile.setOperatorExternalId("operatorExternalId");
        exportFile.setOrganizationId(1L);

        Organization organization = podamFactory.manufacturePojo(Organization.class);
        organization.setOrganizationId(1L);
        organization.setOrgEmail("orgMail@test,com");
        UserOrganizationRoles userOrganizationRoles = podamFactory.manufacturePojo(UserOrganizationRoles.class);
        userOrganizationRoles.setOrganizationId(1L);
        userOrganizationRoles.setEmail("userOrgMail@test.com");
        UserInfo userInfo = new UserInfo();
        userInfo.setOrganizations(List.of(userOrganizationRoles));

        Mockito.when(authzServiceMock.getOperatorInfo(exportFile.getOperatorExternalId())).thenReturn(userInfo);
        //when
        TemplatedEmailDTO result = exportFileEmailDestinationRetrieverService.retrieveEmailDestinations(exportFile, organization);
        //then
        String to = Arrays.stream(result.getTo()).findFirst().get();
        String cc = Arrays.stream(result.getCc()).findFirst().get();
        assertNotNull(result);
        assertEquals(1, result.getTo().length);
        assertEquals(1, result.getCc().length);
        assertEquals("userOrgMail@test.com", to);
        assertEquals("orgMail@test,com", cc );
        assertNull(result.getTemplateName());
        assertNull(result.getParams());
    }

    @Test
    void givenNullOperatorEmailWhenRetrieveEmailDestinationsThenReturnTemplatedEmailDTOWithNullElement(){
        //given
        ExportFile exportFile = podamFactory.manufacturePojo(ExportFile.class);
        exportFile.setOperatorExternalId("operatorExternalId");
        exportFile.setOrganizationId(1L);

        UserInfo userInfo = new UserInfo();
        Organization organization = podamFactory.manufacturePojo(Organization.class);

        Mockito.when(authzServiceMock.getOperatorInfo(exportFile.getOperatorExternalId())).thenReturn(userInfo);
        //when
        TemplatedEmailDTO result = exportFileEmailDestinationRetrieverService.retrieveEmailDestinations(exportFile, organization);

        //then
        assertNotNull(result);
        assertNull(result.getTo()[0]);
        assertEquals(organization.getOrgEmail(), Arrays.stream(result.getCc()).findFirst().get());
    }

    @Test
    void givenBlankOrgMailEmailWhenRetrieveEmailDestinationsThenReturnTemplatedEmailDTOWithNullElement(){
        //given
        ExportFile exportFile = podamFactory.manufacturePojo(ExportFile.class);
        exportFile.setOperatorExternalId("operatorExternalId");
        exportFile.setOrganizationId(1L);

        Organization organization = podamFactory.manufacturePojo(Organization.class);
        organization.setOrganizationId(1L);
        organization.setOrgEmail(" ");
        UserOrganizationRoles userOrganizationRoles = podamFactory.manufacturePojo(UserOrganizationRoles.class);
        userOrganizationRoles.setOrganizationId(1L);
        userOrganizationRoles.setEmail("userOrgMail@test.com");
        UserInfo userInfo = new UserInfo();
        userInfo.setOrganizations(List.of(userOrganizationRoles));

        Mockito.when(authzServiceMock.getOperatorInfo(exportFile.getOperatorExternalId())).thenReturn(userInfo);
        //when
        TemplatedEmailDTO result = exportFileEmailDestinationRetrieverService.retrieveEmailDestinations(exportFile, organization);
        //then
        String to = Arrays.stream(result.getTo()).findFirst().get();
        assertNotNull(result);
        assertEquals("userOrgMail@test.com", to);
        assertNull(result.getCc());
        assertNull(result.getTemplateName());
        assertNull(result.getParams());
    }

    @Test
    void givenOperatorMailEqualToOrgMailEmailWhenRetrieveEmailDestinationsThenReturnTemplatedEmailDTOWithNullElement(){
        //given
        ExportFile exportFile = podamFactory.manufacturePojo(ExportFile.class);
        exportFile.setOperatorExternalId("operatorExternalId");
        exportFile.setOrganizationId(1L);

        Organization organization = podamFactory.manufacturePojo(Organization.class);
        organization.setOrganizationId(1L);
        organization.setOrgEmail("userOrgMail@test.com");
        UserOrganizationRoles userOrganizationRoles = podamFactory.manufacturePojo(UserOrganizationRoles.class);
        userOrganizationRoles.setOrganizationId(1L);
        userOrganizationRoles.setEmail("userOrgMail@test.com");
        UserInfo userInfo = new UserInfo();
        userInfo.setOrganizations(List.of(userOrganizationRoles));

        Mockito.when(authzServiceMock.getOperatorInfo(exportFile.getOperatorExternalId())).thenReturn(userInfo);
        //when
        TemplatedEmailDTO result = exportFileEmailDestinationRetrieverService.retrieveEmailDestinations(exportFile, organization);
        //then
        String to = Arrays.stream(result.getTo()).findFirst().get();
        assertNotNull(result);
        assertEquals("userOrgMail@test.com", to);
        assertNull(result.getCc());
        assertNull(result.getTemplateName());
        assertNull(result.getParams());
    }
}