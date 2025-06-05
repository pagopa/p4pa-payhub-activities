package it.gov.pagopa.payhub.activities.activity.exportflow.email;

import it.gov.pagopa.payhub.activities.activity.email.SendEmailActivity;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.ExportFileService;
import it.gov.pagopa.payhub.activities.dto.email.TemplatedEmailDTO;
import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;
import it.gov.pagopa.payhub.activities.exception.exportflow.ExportFileNotFoundException;
import it.gov.pagopa.payhub.activities.exception.organization.OrganizationNotFoundException;
import it.gov.pagopa.payhub.activities.service.exportflow.email.ExportFileEmailContentConfigurerService;
import it.gov.pagopa.payhub.activities.service.exportflow.email.ExportFileEmailDestinationRetrieverService;
import it.gov.pagopa.payhub.activities.service.exportflow.email.ExportFlowFileEmailTemplateResolverService;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class SendEmailExportFileActivityImplTest {

    @Mock
    private ExportFileService exportFileServiceMock;
    @Mock
    private OrganizationService organizationServiceMock;
    @Mock
    private ExportFileEmailDestinationRetrieverService destinationRetrieverServiceMock;
    @Mock
    private ExportFileEmailContentConfigurerService contentConfigurerServiceMock;
    @Mock
    private ExportFlowFileEmailTemplateResolverService exportFlowFileEmailTemplateResolverServiceMock;
    @Mock
    private SendEmailActivity sendEmailActivityMock;

    private PodamFactory podamFactory;
    private SendEmailExportFileActivityImpl sendEmailExportFileActivity;

    @BeforeEach
    void setUp() {
        sendEmailExportFileActivity = new SendEmailExportFileActivityImpl(exportFileServiceMock, organizationServiceMock, destinationRetrieverServiceMock, contentConfigurerServiceMock, exportFlowFileEmailTemplateResolverServiceMock, sendEmailActivityMock);
        podamFactory = new PodamFactoryImpl();
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                exportFileServiceMock,
                organizationServiceMock,
                destinationRetrieverServiceMock,
                contentConfigurerServiceMock,
                exportFlowFileEmailTemplateResolverServiceMock,
                sendEmailActivityMock);
    }

    @Test
    void givenNotExportFileWhenSendEmailThenExportFileNotFoundException() {
        // Given
        long exportFileId = 1L;
        Mockito.when(exportFileServiceMock.findById(exportFileId))
                .thenReturn(Optional.empty());

        // When, Then
        ExportFileNotFoundException ex = Assertions.assertThrows(ExportFileNotFoundException.class,
        () -> sendEmailExportFileActivity.sendEmail(exportFileId, true));
        assertEquals("Cannot find ExportFile having id: 1", ex.getMessage());
    }

    @Test
    void givenNotOrganizationWhenSendEmailThenOrganizationNotFoundException() {
        // Given
        long exportFileId = 1L;
        long organizationId = 1L;

        ExportFile exportFile = podamFactory.manufacturePojo(ExportFile.class);
        exportFile.setOrganizationId(organizationId);

        Mockito.when(exportFileServiceMock.findById(exportFileId))
                .thenReturn(Optional.of(exportFile));

        Mockito.when(organizationServiceMock.getOrganizationById(organizationId))
                .thenReturn(Optional.empty());

        // When, Then
        OrganizationNotFoundException ex = assertThrows(OrganizationNotFoundException.class,
                () -> sendEmailExportFileActivity.sendEmail(exportFileId, true));
        assertEquals("Cannot find Organization having id: 1", ex.getMessage());
    }

    @Test
    void givenCompleteConfigurationWhenSendEmailThenOk() {
        // Given
        long organizationId = 1L;
        ExportFile exportFile = podamFactory.manufacturePojo(ExportFile.class);
        exportFile.setOrganizationId(1L);
        Organization organization = podamFactory.manufacturePojo(Organization.class);
        organization.setOrganizationId(organizationId);
        boolean success = true;
        EmailTemplateName templateName = EmailTemplateName.EXPORT_PAID_OK;
        Map<String, String> params = Map.of();
        String[] to = new String[0];
        String[] cc = new String[0];

        TemplatedEmailDTO templatedEmailDTO = new TemplatedEmailDTO();
        templatedEmailDTO.setTo(to);
        templatedEmailDTO.setCc(cc);

        TemplatedEmailDTO expectedTemplatedEmail = templatedEmailDTO;
        templatedEmailDTO.setTemplateName(templateName);
        templatedEmailDTO.setParams(params);

        Mockito.when(exportFileServiceMock.findById(exportFile.getExportFileId()))
                .thenReturn(Optional.of(exportFile));
        Mockito.when(organizationServiceMock.getOrganizationById(organizationId))
                .thenReturn(Optional.of(organization));
        Mockito.when(exportFlowFileEmailTemplateResolverServiceMock.resolve(Mockito.same(exportFile), Mockito.same(success)))
                .thenReturn(templateName);
        Mockito.when(contentConfigurerServiceMock.configureParams(exportFile,organization, success))
                .thenReturn(params);
        Mockito.when(destinationRetrieverServiceMock.retrieveEmailDestinations(exportFile, organization))
                .thenReturn(templatedEmailDTO);

        // When
        sendEmailExportFileActivity.sendEmail(exportFile.getExportFileId(), success);

        // Then
        Mockito.verify(sendEmailActivityMock).sendTemplatedEmail(expectedTemplatedEmail);

    }
}