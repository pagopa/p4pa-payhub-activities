package it.gov.pagopa.payhub.activities.activity;

import it.gov.pagopa.payhub.activities.activity.ingestionflow.SendEmailIngestionFlowActivityImpl;
import it.gov.pagopa.payhub.activities.service.SendMailService;
import it.gov.pagopa.payhub.activities.service.UserAuthorizationService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowRetrieverService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * test class for send mail for ingestion activity
 */
@ExtendWith(MockitoExtension.class)
class SendEmailIngestionFlowActivityTest {
    private SendEmailIngestionFlowActivityImpl sendEmailIngestionFlowActivity;
    @Mock
    private IngestionFlowRetrieverService ingestionFlowRetrieverService;
    @Mock
    private SendMailService sendMailService ;
    @Mock
    private UserAuthorizationService userAuthorizationService;

    @BeforeEach
    void init() {
        String ingestionFlowId = "100";
        ingestionFlowRetrieverService = mock(IngestionFlowRetrieverService.class);
        sendEmailIngestionFlowActivity = new SendEmailIngestionFlowActivityImpl(
                userAuthorizationService, ingestionFlowRetrieverService, sendMailService);
    }

    /**
     * test mail sending OK
     */
    @Test
    void sendEmailIngestionSuccess() {
        String ingestionFlowId = "100";
        boolean sent = sendEmailIngestionFlowActivity.sendEmail(ingestionFlowId, true);
        assertFalse(sent);
    }

    /**
     * test mail sending KO
     */
    @Test
    void sendEmailIngestionError() {
        String ingestionFlowId = "100";
        boolean sent = sendEmailIngestionFlowActivity.sendEmail(ingestionFlowId, false);
        assertFalse(sent);
    }

}