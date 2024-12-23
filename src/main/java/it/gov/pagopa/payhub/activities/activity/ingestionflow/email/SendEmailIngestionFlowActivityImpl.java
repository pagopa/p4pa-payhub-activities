package it.gov.pagopa.payhub.activities.activity.ingestionflow.email;

import it.gov.pagopa.payhub.activities.activity.email.SendEmailActivity;
import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.email.EmailDTO;
import it.gov.pagopa.payhub.activities.exception.IngestionFlowFileNotFoundException;
import it.gov.pagopa.payhub.activities.service.ingestionflow.email.IngestionFlowFileEmailContentConfigurerService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.email.IngestionFlowFileEmailDestinationRetrieverService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Slf4j
@Component
public class SendEmailIngestionFlowActivityImpl implements SendEmailIngestionFlowActivity {

    private final IngestionFlowFileDao ingestionFlowFileDao;
    private final IngestionFlowFileEmailDestinationRetrieverService destinationRetrieverService;
    private final IngestionFlowFileEmailContentConfigurerService contentConfigurerService;
    private final SendEmailActivity sendEmailActivity;

    public SendEmailIngestionFlowActivityImpl(IngestionFlowFileDao ingestionFlowFileDao, IngestionFlowFileEmailDestinationRetrieverService destinationRetrieverService, IngestionFlowFileEmailContentConfigurerService contentConfigurerService, SendEmailActivity sendEmailActivity) {
        this.ingestionFlowFileDao = ingestionFlowFileDao;
        this.destinationRetrieverService = destinationRetrieverService;
        this.contentConfigurerService = contentConfigurerService;
        this.sendEmailActivity = sendEmailActivity;
    }


    @Override
    public void sendEmail(Long ingestionFlowFileId, boolean success) {
        log.info("Sending email for IngestionFlowFile {} with success {}", ingestionFlowFileId, success);
        IngestionFlowFileDTO ingestionFlowFileDTO = retrieveIngestionFlowFileRecord(ingestionFlowFileId);

        EmailDTO emailDTO = contentConfigurerService.configure(ingestionFlowFileDTO, success);
        destinationRetrieverService.configure(ingestionFlowFileDTO, emailDTO);

        sendEmailActivity.send(emailDTO);
    }

    private IngestionFlowFileDTO retrieveIngestionFlowFileRecord(Long ingestionFlowFileId) {
        return ingestionFlowFileDao.findById(ingestionFlowFileId)
                .orElseThrow(() -> new IngestionFlowFileNotFoundException("Cannot find ingestionFlow having id: " + ingestionFlowFileId));
    }
}