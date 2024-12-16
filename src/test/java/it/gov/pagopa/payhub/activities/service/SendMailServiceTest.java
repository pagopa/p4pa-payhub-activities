package it.gov.pagopa.payhub.activities.service;

import it.gov.pagopa.payhub.activities.activity.ingestionflow.SendEmailIngestionFlowActivityImpl;
import it.gov.pagopa.payhub.activities.dto.MailTo;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import static it.gov.pagopa.payhub.activities.utility.faker.MailFaker.buildMailTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest(
		classes = {SendEmailIngestionFlowActivityImpl.class},
		webEnvironment = SpringBootTest.WebEnvironment.NONE)
@EnableConfigurationProperties
@ExtendWith(MockitoExtension.class)
class SendMailServiceTest {
	@MockBean
	private SendMailService sendMailService ;

	@BeforeEach
	void setup() {
		String blank = "";
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
		sendMailService  = new SendMailService(blank, blank, blank,blank,blank,blank,blank, javaMailSender);
	}


	@ParameterizedTest
	@ValueSource(strings = {"01","02","03","04","05","06","07","08","09"})
	void testSendEmailParametrized(String param) {
		MailTo mailTo = buildMailTo();;
		String errDataNotValued = "Mail data null or not valued";
		switch (param)  {
			case "01":
				sendMailSendExceptionError(mailTo);
				break;
			case "02":
				mailTo.setMailSubject("");
				sendMessagingExceptionError(mailTo, errDataNotValued);
				break;
			case "03":
				mailTo.setMailSubject(null);
				sendMessagingExceptionError(mailTo, errDataNotValued);
				break;
			case "04":
				mailTo.setTo(new String[]{});
				sendMessagingExceptionError(mailTo,  errDataNotValued);
				break;
			case "05":
				mailTo.setCc(new String[]{});
				sendMailSendExceptionError(mailTo);
				break;
			case "06":
				mailTo.setHtmlText("");
				sendMessagingExceptionError(mailTo, errDataNotValued);
				break;
			case "07":
				mailTo.setHtmlText(null);
				sendMessagingExceptionError(mailTo,  errDataNotValued);
				break;
			case "09":
				mailTo.setEmailFromAddress(null);
				sendMessagingExceptionError(mailTo,  errDataNotValued);
				break;
			default:
				break;
		}
	}

	void sendMessagingExceptionError(MailTo mailData, String error) {
		MessagingException exception = assertThrows(MessagingException.class, () ->
				sendMailService.sendMail(mailData), error);
		assertEquals(error, exception.getMessage());
	}

	void sendMailSendExceptionError(MailTo mailData) {
		MailSendException exception = assertThrows(MailSendException.class, () ->
				sendMailService.sendMail(mailData));
		System.out.println("Error: "+exception.getMessage());
	}
}
