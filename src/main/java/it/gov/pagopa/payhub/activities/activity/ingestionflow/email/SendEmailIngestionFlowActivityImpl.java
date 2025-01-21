package it.gov.pagopa.payhub.activities.activity.ingestionflow.email;

import it.gov.pagopa.payhub.activities.activity.email.SendEmailActivity;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.email.EmailDTO;
import it.gov.pagopa.payhub.activities.exception.IngestionFlowFileNotFoundException;
import it.gov.pagopa.payhub.activities.service.ingestionflow.email.IngestionFlowFileEmailContentConfigurerService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.email.IngestionFlowFileEmailDestinationRetrieverService;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Slf4j
@Component
public class SendEmailIngestionFlowActivityImpl implements SendEmailIngestionFlowActivity {

    private final IngestionFlowFileService ingestionFlowFileService;
    private final IngestionFlowFileEmailDestinationRetrieverService destinationRetrieverService;
    private final IngestionFlowFileEmailContentConfigurerService contentConfigurerService;
    private final SendEmailActivity sendEmailActivity;

    public SendEmailIngestionFlowActivityImpl(IngestionFlowFileService ingestionFlowFileService, IngestionFlowFileEmailDestinationRetrieverService destinationRetrieverService, IngestionFlowFileEmailContentConfigurerService contentConfigurerService, SendEmailActivity sendEmailActivity) {
        this.ingestionFlowFileService = ingestionFlowFileService;
        this.destinationRetrieverService = destinationRetrieverService;
        this.contentConfigurerService = contentConfigurerService;
        this.sendEmailActivity = sendEmailActivity;
    }


    @Override
    public void sendEmail(Long ingestionFlowFileId, boolean success) {
        log.info("Sending email for IngestionFlowFile {} with success {}", ingestionFlowFileId, success);
        IngestionFlowFile ingestionFlowFileDTO = retrieveIngestionFlowFileRecord(ingestionFlowFileId);

        EmailDTO emailDTO = contentConfigurerService.configure(ingestionFlowFileDTO, success);
        destinationRetrieverService.configure(ingestionFlowFileDTO, emailDTO);

        sendEmailActivity.send(emailDTO);
    }

    private IngestionFlowFile retrieveIngestionFlowFileRecord(Long ingestionFlowFileId) {
        return ingestionFlowFileService.findById(ingestionFlowFileId)
                .orElseThrow(() -> new IngestionFlowFileNotFoundException("Cannot find ingestionFlow having id: " + ingestionFlowFileId));
    }
}