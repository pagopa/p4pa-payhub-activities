package it.gov.pagopa.payhub.activities.activity;

import it.gov.pagopa.payhub.activities.activity.ingestionflow.SendEmailIngestionFlowActivityImpl;
import it.gov.pagopa.payhub.activities.config.EmailConfig;
import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.MailTo;
import it.gov.pagopa.payhub.activities.dto.UserInfoDTO;
import it.gov.pagopa.payhub.activities.service.SendMailService;
import it.gov.pagopa.payhub.activities.service.UserAuthorizationServiceImpl;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowRetrieverService;
import it.gov.pagopa.payhub.activities.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

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
    private UserAuthorizationServiceImpl userAuthorizationService;
    @Mock
    IngestionFlowFileDao ingestionFlowFileDao;
    @Mock
    private EmailConfig emailConfig;

    @BeforeEach
    void init() {
        userAuthorizationService = new UserAuthorizationServiceImpl();
        ingestionFlowRetrieverService = new IngestionFlowRetrieverService(ingestionFlowFileDao);
        sendEmailIngestionFlowActivity = new SendEmailIngestionFlowActivityImpl(
                userAuthorizationService, ingestionFlowRetrieverService, sendMailService);
    }

    /**
     * test mail sending OK
     */
    @Test
    void sendEmailIngestionSuccess() {
        String ingestionFlowId = "100";
        testSendMail();
        boolean sent = sendEmailIngestionFlowActivity.sendEmail(ingestionFlowId, true);
        assertFalse(sent);
    }

    /**
     * test mail sending KO
     */
    @Test
    void sendEmailIngestionError() {
        String ingestionFlowId = "100";
        testSendMail();
        boolean sent = sendEmailIngestionFlowActivity.sendEmail(ingestionFlowId, false);
        assertFalse(sent);
    }

    @Test
    void testSendMail() {
        MailTo mailto = new MailTo();
        mailto.setMailSubject("Subject");
        mailto.setTo(new String[]{"test_receiver@mailtest.com"});
        mailto.setMailText("Mail Text");
        mailto.setHtmlText("Html Text");
        mailto.setEmailFromAddress("test_sender@mailtest.com");
        mailto.setTemplateName(Constants.TEMPLATE_LOAD_FILE_OK);
        JavaMailSender javaMailSender = emailConfig.getJavaMailSender();

        boolean testOK = true;
        try {
            sendMailService.sendMail(javaMailSender, mailto);
        } catch (Exception e) {
            testOK = false;
        }
        assertTrue(testOK);
    }

}