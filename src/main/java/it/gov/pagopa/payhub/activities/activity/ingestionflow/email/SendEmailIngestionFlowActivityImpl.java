package it.gov.pagopa.payhub.activities.activity.ingestionflow.email;

import it.gov.pagopa.payhub.activities.activity.email.SendEmailActivity;
import it.gov.pagopa.payhub.activities.connector.organization.BrokerService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.email.TemplatedEmailDTO;
import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.IngestionFlowFileNotFoundException;
import it.gov.pagopa.payhub.activities.exception.organization.OrganizationNotFoundException;
import it.gov.pagopa.payhub.activities.service.ingestionflow.email.IngestionFlowFileEmailContentConfigurerService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.email.IngestionFlowFileEmailDestinationRetrieverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.email.IngestionFlowFileEmailTemplateResolverService;
import it.gov.pagopa.pu.organization.dto.generated.BrokerConfiguration;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Lazy
@Slf4j
@Component
public class SendEmailIngestionFlowActivityImpl implements SendEmailIngestionFlowActivity {

    private final IngestionFlowFileService ingestionFlowFileService;
    private final IngestionFlowFileEmailTemplateResolverService emailTemplateResolverService;
    private final IngestionFlowFileEmailDestinationRetrieverService destinationRetrieverService;
    private final IngestionFlowFileEmailContentConfigurerService contentConfigurerService;
    private final SendEmailActivity sendEmailActivity;
    private final OrganizationService organizationService;
    private final BrokerService brokerService;

    public SendEmailIngestionFlowActivityImpl(IngestionFlowFileService ingestionFlowFileService, IngestionFlowFileEmailTemplateResolverService emailTemplateResolverService, IngestionFlowFileEmailDestinationRetrieverService destinationRetrieverService, IngestionFlowFileEmailContentConfigurerService contentConfigurerService, SendEmailActivity sendEmailActivity, OrganizationService organizationService, BrokerService brokerService) {
        this.ingestionFlowFileService = ingestionFlowFileService;
        this.emailTemplateResolverService = emailTemplateResolverService;
        this.destinationRetrieverService = destinationRetrieverService;
        this.contentConfigurerService = contentConfigurerService;
        this.sendEmailActivity = sendEmailActivity;
        this.organizationService = organizationService;
        this.brokerService = brokerService;
    }


    @Override
    public void sendIngestionFlowFileCompleteEmail(Long ingestionFlowFileId, boolean success) {
        log.info("Sending email for IngestionFlowFile {} with success {}", ingestionFlowFileId, success);
        IngestionFlowFile ingestionFlowFileDTO = retrieveIngestionFlowFileRecord(ingestionFlowFileId);
        Organization organization = retrieveOrganization(ingestionFlowFileDTO.getOrganizationId());
        String mailSenderAddress = Optional.ofNullable(brokerService.getBrokerConfigurationsById(organization.getBrokerId()))
                .map(BrokerConfiguration::getMailSenderAddress)
                .orElse(null);

        EmailTemplateName templateName = emailTemplateResolverService.resolve(ingestionFlowFileDTO, success);
        Map<String, String> params = contentConfigurerService.configureParams(ingestionFlowFileDTO, success);
        Pair<String[], String[]> destinations = destinationRetrieverService.retrieveEmailDestinations(ingestionFlowFileDTO);

        TemplatedEmailDTO templatedEmail = new TemplatedEmailDTO(templateName, mailSenderAddress, destinations.getKey(), destinations.getValue(), params, null);
        sendEmailActivity.sendTemplatedEmail(organization.getBrokerId(), templatedEmail);
    }

    private IngestionFlowFile retrieveIngestionFlowFileRecord(Long ingestionFlowFileId) {
        return ingestionFlowFileService.findById(ingestionFlowFileId)
                .orElseThrow(() -> new IngestionFlowFileNotFoundException("Cannot find ingestionFlow having id: " + ingestionFlowFileId));
    }

    private Organization retrieveOrganization(Long organizationId) {
        return organizationService.getOrganizationById(organizationId)
                .orElseThrow(() -> new OrganizationNotFoundException("Cannot find Organization having id: " + organizationId));
    }
}