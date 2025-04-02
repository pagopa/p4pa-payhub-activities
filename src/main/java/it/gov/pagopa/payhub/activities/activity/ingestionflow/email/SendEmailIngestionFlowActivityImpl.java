package it.gov.pagopa.payhub.activities.activity.ingestionflow.email;

import it.gov.pagopa.payhub.activities.activity.email.SendEmailActivity;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.email.TemplatedEmailDTO;
import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.IngestionFlowFileNotFoundException;
import it.gov.pagopa.payhub.activities.service.ingestionflow.email.IngestionFlowFileEmailContentConfigurerService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.email.IngestionFlowFileEmailDestinationRetrieverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.email.IngestionFlowFileEmailTemplateResolverService;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Map;

@Lazy
@Slf4j
@Component
public class SendEmailIngestionFlowActivityImpl implements SendEmailIngestionFlowActivity {

    private final IngestionFlowFileService ingestionFlowFileService;
    private final IngestionFlowFileEmailTemplateResolverService emailTemplateResolverService;
    private final IngestionFlowFileEmailDestinationRetrieverService destinationRetrieverService;
    private final IngestionFlowFileEmailContentConfigurerService contentConfigurerService;
    private final SendEmailActivity sendEmailActivity;

    public SendEmailIngestionFlowActivityImpl(IngestionFlowFileService ingestionFlowFileService, IngestionFlowFileEmailTemplateResolverService emailTemplateResolverService, IngestionFlowFileEmailDestinationRetrieverService destinationRetrieverService, IngestionFlowFileEmailContentConfigurerService contentConfigurerService, SendEmailActivity sendEmailActivity) {
        this.ingestionFlowFileService = ingestionFlowFileService;
        this.emailTemplateResolverService = emailTemplateResolverService;
        this.destinationRetrieverService = destinationRetrieverService;
        this.contentConfigurerService = contentConfigurerService;
        this.sendEmailActivity = sendEmailActivity;
    }


    @Override
    public void sendEmail(Long ingestionFlowFileId, boolean success) {
        log.info("Sending email for IngestionFlowFile {} with success {}", ingestionFlowFileId, success);
        IngestionFlowFile ingestionFlowFileDTO = retrieveIngestionFlowFileRecord(ingestionFlowFileId);

        EmailTemplateName templateName = emailTemplateResolverService.resolve(ingestionFlowFileDTO, success);
        Map<String, String> params = contentConfigurerService.configureParams(ingestionFlowFileDTO, success);
        Pair<String[], String[]> destinations = destinationRetrieverService.retrieveEmailDestinations(ingestionFlowFileDTO);

        sendEmailActivity.sendTemplatedEmail(new TemplatedEmailDTO(
                templateName, destinations.getKey(), destinations.getValue(), params
        ));
    }

    private IngestionFlowFile retrieveIngestionFlowFileRecord(Long ingestionFlowFileId) {
        return ingestionFlowFileService.findById(ingestionFlowFileId)
                .orElseThrow(() -> new IngestionFlowFileNotFoundException("Cannot find ingestionFlow having id: " + ingestionFlowFileId));
    }
}