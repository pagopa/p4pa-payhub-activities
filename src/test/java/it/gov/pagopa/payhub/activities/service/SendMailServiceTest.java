package it.gov.pagopa.payhub.activities.service;

import it.gov.pagopa.payhub.activities.activity.ingestionflow.SendEmailIngestionFlowActivityImpl;
import it.gov.pagopa.payhub.activities.dto.MailTo;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest(
		classes = {SendEmailIngestionFlowActivityImpl.class},
		webEnvironment = SpringBootTest.WebEnvironment.NONE)
@EnableConfigurationProperties
@ExtendWith(MockitoExtension.class)
class SendMailServiceTest {
	@MockBean
	private SendMailService sendMailService ;
	@MockBean
	JavaMailSenderImpl javaMailSender;

    private MailTo validMailOk;
	private MailTo invalidMailOk;
	private MailTo validMailKo;
	private MailTo invalidMailKo;
	private MailTo invalidBlankMail;

	@BeforeEach
	void setup() {
		String blank = "";
		createTestData();
		//JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
		sendMailService  = new SendMailService("ALFA", blank, blank,blank,blank,blank,blank, javaMailSender);
	}

	@Test
	void testSendEmailFileLoadedSuccess() {
        try {
			assertTrue(sendMailService.sendMail(validMailOk));
        } catch (MessagingException messagingException) {
            try {
				System.out.println("Problems sending mail");
                throw messagingException;
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }
	}

	@Test
	void testSendEmailFileNotLoadedSuccess() {
		try {
			assertTrue(sendMailService.sendMail(validMailKo));
		} catch (MessagingException messagingException) {
			try {
				System.out.println("Problems sending mail");
				throw messagingException;
			} catch (MessagingException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Test
	void testSendEmailFileLoadedFailed() {
		assertThrows(MessagingException.class, () ->
		sendMailService.sendMail(invalidMailOk), "Error in mail data");
	}

	@Test
	void testSendEmailFileNotLoadedFailed() {
		assertThrows(MessagingException.class, () ->
				sendMailService.sendMail(invalidMailKo), "Error in mail data");
	}

	@Test
	void testSendInvalidBlankMail() {
		assertThrows(MessagingException.class, () ->
				sendMailService.sendMail(invalidBlankMail), "Invalid blank mail");
	}


	private void createTestData() {
		invalidBlankMail =  MailTo.builder()
				.emailFromAddress("test_sender@mailtest.com")
				.mailSubject("")
				.to(new String[]{})
				.htmlText("")
				.build();

		validMailOk = MailTo.builder()
				.emailFromAddress("test_sender@mailtest.com")
				.mailSubject("Subject")
				.to(new String[]{"test_receiver@mailtest.com"})
				.htmlText("Html Text")
				.build();

		validMailKo = MailTo.builder()
				.emailFromAddress("test_sender@mailtest.com")
				.mailSubject("Subject")
				.to(new String[]{"test_receiver@mailtest.com"})
				.htmlText("Html Text")
				.build();

		validMailOk = MailTo.builder()
				.emailFromAddress("test_sender@mailtest.com")
				.mailSubject("Subject")
				.to(new String[]{"test_receiver@mailtest.com"})
				.htmlText("Html Text")
				.build();

		validMailKo = MailTo.builder()
				.emailFromAddress("test_sender@mailtest.com")
				.mailSubject("Subject")
				.to(new String[]{"test_receiver@mailtest.com"})
				.htmlText("Html Text")
				.build();

		invalidMailOk = MailTo.builder()
				.emailFromAddress("test_sender@mailtest.com")
				.mailSubject("Subject")
				.to(new String[]{})
				.htmlText("Html Text")
				.build();

		invalidMailKo = MailTo.builder()
				.emailFromAddress("test_sender@mailtest.com")
				.mailSubject("Subject")
				.to(new String[]{})
				.htmlText("Html Text")
				.build();

	}
}
