package it.gov.pagopa.payhub.activities.service;

import it.gov.pagopa.payhub.activities.config.EmailConfig;
import it.gov.pagopa.payhub.activities.dto.MailTo;
import it.gov.pagopa.payhub.activities.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
class SendMailServiceTest {
	@Mock
	private SendMailService sendMailService;
	@Mock
	private EmailConfig emailConfig;

	@BeforeEach
	void setup() {
		sendMailService = new SendMailService();
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
		assertFalse(testOK);
    }

}