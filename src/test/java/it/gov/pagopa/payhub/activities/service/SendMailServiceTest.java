package it.gov.pagopa.payhub.activities.service;

import it.gov.pagopa.payhub.activities.activity.utility.SendEmailIngestionFlowActivityImpl;
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

    private MailTo validMailOk;
	private MailTo validMailOkAttachment;
	private MailTo invalidMailOk;
	private MailTo validMailKo;
	private MailTo invalidMailKo;
	private MailTo invalidBlankMail;

	private MailTo validMailOkCC;
	private MailTo invalidMailOkCC;
	private MailTo validMailKoCC;
	private MailTo invalidMailKoCC;
	private MailTo invalidBlankMailCC;

	@BeforeEach
	void setup() {
		String blank = "";
		createBeans();
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
		sendMailService  = new SendMailService("ALFA", blank, blank,blank,blank,blank,blank, javaMailSender);
	}

	@Test
	void testSendEmailFileLoadedSuccess() {
		assertThrows(MailSendException.class, () ->
				sendMailService.sendMail(validMailOk), "Mail sender error encountered");
	}

	@Test
	void testSendEmailFileNotLoadedSuccess() {
		assertThrows(MailSendException.class, () ->
				sendMailService.sendMail(validMailKo), "Mail sender error encountered");
	}

	@Test
	void testSendEmailFileLoadedAttachSuccess() {
		assertThrows(MessagingException.class, () ->
				sendMailService.sendMail(validMailOkAttachment), "Mail sender error encountered");
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

	// cc present
	@Test
	void testSendEmailFileLoadedSuccessCC() {
		assertThrows(MailSendException.class, () ->
				sendMailService.sendMail(validMailOkCC), "Mail sender error encountered");
	}

	@Test
	void testSendEmailFileNotLoadedSuccessCC() {
		assertThrows(MailSendException.class, () ->
				sendMailService.sendMail(validMailKoCC), "Mail sender error encountered");
	}

	@Test
	void testSendEmailFileLoadedFailedCC() {
		assertThrows(MessagingException.class, () ->
				sendMailService.sendMail(invalidMailOkCC), "Error in mail data");
	}

	@Test
	void testSendEmailFileNotLoadedFailedCC() {
		assertThrows(MessagingException.class, () ->
				sendMailService.sendMail(invalidMailKoCC), "Error in mail data");
	}

	@Test
	void testSendInvalidBlankMailCC() {
		assertThrows(MessagingException.class, () ->
				sendMailService.sendMail(invalidBlankMailCC), "Invalid blank mail");
	}

	private void createBeans() {
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

		validMailOkAttachment = MailTo.builder()
				.emailFromAddress("test_sender@mailtest.com")
				.mailSubject("Subject")
				.to(new String[]{})
				.htmlText("Html Text")
				.build();

		invalidBlankMailCC = MailTo.builder()
				.emailFromAddress("test_sender@mailtest.com")
				.mailSubject("")
				.to(new String[]{})
				.cc(new String[]{"test_cc@mailtest.com"})
				.htmlText("")
				.build();

		validMailOkCC = MailTo.builder()
				.emailFromAddress("test_sender@mailtest.com")
				.mailSubject("Subject")
				.to(new String[]{"test_receiver@mailtest.com"})
				.cc(new String[]{"test_cc@mailtest.com"})
				.htmlText("Html Text")
				.build();

		validMailKoCC = MailTo.builder()
				.emailFromAddress("test_sender@mailtest.com")
				.mailSubject("Subject")
				.to(new String[]{"test_receiver@mailtest.com"})
				.cc(new String[]{"test_cc@mailtest.com"})
				.htmlText("Html Text")
				.build();

		validMailOkCC = MailTo.builder()
				.emailFromAddress("test_sender@mailtest.com")
				.mailSubject("Subject")
				.to(new String[]{"test_receiver@mailtest.com"})
				.cc(new String[]{"test_cc@mailtest.com"})
				.htmlText("Html Text")
				.build();

		validMailKoCC = MailTo.builder()
				.emailFromAddress("test_sender@mailtest.com")
				.mailSubject("Subject")
				.to(new String[]{"test_receiver@mailtest.com"})
				.cc(new String[]{"test_cc@mailtest.com"})
				.htmlText("Html Text")
				.build();

		invalidMailOkCC = MailTo.builder()
				.emailFromAddress("test_sender@mailtest.com")
				.mailSubject("Subject")
				.to(new String[]{})
				.cc(new String[]{"test_cc@mailtest.com"})
				.htmlText("Html Text")
				.build();

		invalidMailKoCC = MailTo.builder()
				.emailFromAddress("test_sender@mailtest.com")
				.mailSubject("Subject")
				.to(new String[]{})
				.cc(new String[]{"test_cc@mailtest.com"})
				.htmlText("Html Text")
				.build();


	}
}
