package it.gov.pagopa.payhub.activities.activity;

import it.gov.pagopa.payhub.activities.activity.ingestionflow.SendEmailIngestionFlowActivityImpl;
import it.gov.pagopa.payhub.activities.activity.paymentsreporting.service.IngestionFlowRetrieverService;
import it.gov.pagopa.payhub.activities.exception.IngestionFlowNotFoundException;
import it.gov.pagopa.payhub.activities.service.SendMailService;
import it.gov.pagopa.payhub.activities.service.auth.UserAuthorizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * test class for send mail for ingestion activity
 */
@ExtendWith(MockitoExtension.class)
class SendEmailIngestionFlowActivityTest {
    private SendEmailIngestionFlowActivityImpl sendEmailIngestionFlowActivity;

    @Mock
    UserAuthorizationService authorizationService;
    @Mock
    IngestionFlowRetrieverService ingestionFlowRetrieverService;
    @Mock
    SendMailService sendMailService;

    @BeforeEach
    void init() {

    }

    /**
     * test mail sending OK
     */
    @Test
    void sendEmailIngestionSuccess() {
        String ingestionFlowId = "100";
        boolean success = true;
        try {
            sendEmailIngestionFlowActivity = new SendEmailIngestionFlowActivityImpl(authorizationService, ingestionFlowRetrieverService, sendMailService);
            success = sendEmailIngestionFlowActivity.sendEmail(ingestionFlowId, success);
        } catch (IngestionFlowNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertFalse(success);
    }

    /**
     * test mail sending KO
     */
    @Test
    void sendEmailIngestionError() {
        String ingestionFlowId = "100";
        boolean success = false;
        try {
            sendEmailIngestionFlowActivity = new SendEmailIngestionFlowActivityImpl(
                    authorizationService, ingestionFlowRetrieverService, sendMailService);
            success = sendEmailIngestionFlowActivity.sendEmail(ingestionFlowId, success);
        } catch (IngestionFlowNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertFalse(success);
    }

}