package it.gov.pagopa.payhub.activities.activity;

import it.gov.pagopa.payhub.activities.activity.ingestionflow.SendEmailIngestionFlowActivityImpl;
import it.gov.pagopa.payhub.activities.activity.paymentsreporting.service.IngestionFlowRetrieverService;
import it.gov.pagopa.payhub.activities.service.SendMailService;
import it.gov.pagopa.payhub.activities.utility.UserAuthorizationActivity;
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
    UserAuthorizationActivity userAuthorizationActivity;
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
        sendEmailIngestionFlowActivity = new SendEmailIngestionFlowActivityImpl(userAuthorizationActivity, ingestionFlowRetrieverService, sendMailService);
        assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowId, true));
    }

    /**
     * test mail sending KO
     */
    @Test
    void sendEmailIngestionError() {
        String ingestionFlowId = "100";
        sendEmailIngestionFlowActivity = new SendEmailIngestionFlowActivityImpl(
                userAuthorizationActivity, ingestionFlowRetrieverService, sendMailService);
        assertFalse(sendEmailIngestionFlowActivity.sendEmail(ingestionFlowId, false));
    }

}