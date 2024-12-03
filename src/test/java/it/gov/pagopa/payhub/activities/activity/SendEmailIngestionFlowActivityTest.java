package it.gov.pagopa.payhub.activities.activity;

import it.gov.pagopa.payhub.activities.activity.paymentsreporting.SendEmailIngestionFlowActivityImpl;
import it.gov.pagopa.payhub.activities.config.EmailConfig;
import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import it.gov.pagopa.payhub.activities.dto.MailTo;
import it.gov.pagopa.payhub.activities.service.SendMailService;
import it.gov.pagopa.payhub.activities.service.UserAuthorizationServiceImpl;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowRetrieverService;
import it.gov.pagopa.payhub.activities.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * test class for send mail for ingestion activity
 */
@ExtendWith(MockitoExtension.class)
class SendEmailIngestionFlowActivityTest {
    @InjectMocks
    private SendEmailIngestionFlowActivityImpl sendEmailIngestionFlowActivity;
    @Mock
    private IngestionFlowRetrieverService ingestionFlowRetrieverService;
    @Mock
    private SendMailService sendMailService ;
    @Mock
    private UserAuthorizationServiceImpl userAuthorizationService;
    @Mock
    private IngestionFlowFileDao ingestionFlowFileDao;
    @Mock
    private EmailConfig emailConfig;

    @BeforeEach
    void init() {
    }

    /**
     * test mail sending OK
     */
    @Test
    void sendEmailIngestionSuccess() {
        String ingestionFlowId = "100";
        Mockito.when()
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

