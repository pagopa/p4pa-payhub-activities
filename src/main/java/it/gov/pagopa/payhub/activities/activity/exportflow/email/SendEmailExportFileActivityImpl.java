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
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Map;

@Lazy
@Slf4j
@Component
public class SendEmailExportFileActivityImpl implements SendEmailExportFileActivity{

    private final ExportFileService exportFileService;
    private final OrganizationService organizationService;
    private final ExportFileEmailDestinationRetrieverService destinationRetrieverService;
    private final ExportFileEmailContentConfigurerService contentConfigurerService;
    private final ExportFlowFileEmailTemplateResolverService exportFlowFileEmailTemplateResolverService;
    private final SendEmailActivity sendEmailActivity;

    public SendEmailExportFileActivityImpl(ExportFileService exportFileService, OrganizationService organizationService,
                                           ExportFileEmailDestinationRetrieverService destinationRetrieverService,
                                           ExportFileEmailContentConfigurerService contentConfigurerService, ExportFlowFileEmailTemplateResolverService exportFlowFileEmailTemplateResolverService,
                                           SendEmailActivity sendEmailActivity) {
        this.exportFileService = exportFileService;
        this.organizationService = organizationService;
        this.destinationRetrieverService = destinationRetrieverService;
        this.contentConfigurerService = contentConfigurerService;
        this.exportFlowFileEmailTemplateResolverService = exportFlowFileEmailTemplateResolverService;
        this.sendEmailActivity = sendEmailActivity;
    }


    @Override
    public void sendExportCompletedEmail(Long exportFileId, boolean success) {
        log.info("Sending email for ExportFile {} with success {}", exportFileId, success);
        ExportFile exportFile = retrieveExportFile(exportFileId);

        Organization organization = retrieveOrganization(exportFile.getOrganizationId());

        EmailTemplateName emailTemplateName = exportFlowFileEmailTemplateResolverService.resolve(exportFile, success);
        Map<String, String> params = contentConfigurerService.configureParams(exportFile, organization, success);
        TemplatedEmailDTO templatedEmailDTO = destinationRetrieverService.retrieveEmailDestinations(exportFile, organization);

        templatedEmailDTO.setTemplateName(emailTemplateName);
        templatedEmailDTO.setParams(params);
        sendEmailActivity.sendTemplatedEmail(templatedEmailDTO);
    }

    private ExportFile retrieveExportFile(Long exportFileId) {
        return exportFileService.findById(exportFileId)
                .orElseThrow(() -> new ExportFileNotFoundException("Cannot find ExportFile having id: " + exportFileId));
    }

    private Organization retrieveOrganization(Long organizationId) {
        return organizationService.getOrganizationById(organizationId)
                .orElseThrow(() -> new OrganizationNotFoundException("Cannot find Organization having id: " + organizationId));
    }
}
