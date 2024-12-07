package it.gov.pagopa.payhub.activities.service;

import it.gov.pagopa.payhub.activities.activity.utility.SendEmailIngestionFlowActivityImpl;
import it.gov.pagopa.payhub.activities.dto.MailTo;
import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.internet.PreencodedMimeBodyPart;
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
import org.springframework.mail.javamail.MimeMessageHelper;

import java.io.File;

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
	
	private JavaMailSenderImpl javaMailSender;
	private MimeMessage mimeMessage;
	private MailTo validMailOk;
	private MailTo validMailOkAttachment;
	private MailTo invalidMailOk;
	private MailTo validMailKoAttachment;
	private MailTo validMailKo;
	private MailTo invalidMailKo;
	private MailTo invalidBlankMail;

	@BeforeEach
	void setup() {
		createBeans();
		javaMailSender = new JavaMailSenderImpl();
		sendMailService  = new SendMailService(javaMailSender);
		mimeMessage = javaMailSender.createMimeMessage();
	}

	@Test
	void testSendEmailFileLoadedSuccess() throws MessagingException {
		setMimeMessage(validMailOk);
		assertThrows(MailSendException.class, () ->
				sendMailService.sendMail(validMailOk), "Mail sender error encountered");
	}

	@Test
	void testSendEmailFileNotLoadedSuccess() throws MessagingException {
		setMimeMessage(validMailKo);
		assertThrows(MailSendException.class, () ->
				sendMailService.sendMail(validMailKo), "Mail sender error encountered");
	}

	@Test
	void testSendEmailFileLoadedAttachSuccess() throws MessagingException {
		setMimeMessage(validMailOkAttachment);
		assertThrows(MessagingException.class, () ->
				sendMailService.sendMail(validMailOkAttachment), "Mail sender error encountered");
	}

	@Test
	void testSendEmailFileNotLoadedAttachSuccess() throws MessagingException {
		setMimeMessage(validMailKoAttachment);
		assertThrows(MessagingException.class, () ->
				sendMailService.sendMail(validMailKoAttachment), "Mail sender error encountered");
	}

	@Test
	void testSendEmailFileLoadedFailed() throws MessagingException {
		setMimeMessage(invalidMailOk);
		assertThrows(MessagingException.class, () ->
		sendMailService.sendMail(invalidMailOk), "Error in mail data");
	}

	@Test
	void testSendEmailFileNotLoadedFailed() throws MessagingException {
		setMimeMessage(invalidMailKo);
		assertThrows(MessagingException.class, () ->
				sendMailService.sendMail(invalidMailKo), "Error in mail data");
	}

	@Test
	void testSendInvalidBlankMail() throws MessagingException {
		setMimeMessage(invalidBlankMail);
		assertThrows(MessagingException.class, () ->
				sendMailService.sendMail(invalidBlankMail), "Invalid blank mail");
	}

	private void createBeans() {
		invalidBlankMail =  MailTo.builder()
				.emailFromAddress("test_sender@mailtest.com")
				.mailSubject("")
				.to(new String[]{})
				.mailText("")
				.htmlText("")
				.attachmentPath("")
				.build();

		validMailOk = MailTo.builder()
				.emailFromAddress("test_sender@mailtest.com")
				.mailSubject("Subject")
				.to(new String[]{"test_receiver@mailtest.com"})
				.mailText("Mail Text")
				.htmlText("Html Text")
				.attachmentPath(null)
				.build();

		validMailKo = MailTo.builder()
				.emailFromAddress("test_sender@mailtest.com")
				.mailSubject("Subject")
				.to(new String[]{"test_receiver@mailtest.com"})
				.mailText("Mail Text")
				.htmlText("Html Text")
				.attachmentPath(null)
				.build();

		validMailOk = MailTo.builder()
				.emailFromAddress("test_sender@mailtest.com")
				.mailSubject("Subject")
				.to(new String[]{"test_receiver@mailtest.com"})
				.mailText("Mail Text")
				.htmlText("Html Text")
				.attachmentPath("/TMP")
				.build();

		validMailKo = MailTo.builder()
				.emailFromAddress("test_sender@mailtest.com")
				.mailSubject("Subject")
				.to(new String[]{"test_receiver@mailtest.com"})
				.mailText("Mail Text")
				.htmlText("Html Text")
				.attachmentPath("/TMP")
				.build();

		invalidMailOk = MailTo.builder()
				.emailFromAddress("test_sender@mailtest.com")
				.mailSubject("Subject")
				.to(new String[]{})
				.mailText("Mail Text")
				.htmlText("Html Text")
				.attachmentPath("/tmp/reportingFlow.log")
				.build();

		invalidMailKo = MailTo.builder()
				.emailFromAddress("test_sender@mailtest.com")
				.mailSubject("Subject")
				.to(new String[]{})
				.mailText("Mail Text")
				.htmlText("Html Text")
				.attachmentPath("/tmp/reportingFlow.log")
				.build();

		validMailOkAttachment = MailTo.builder()
				.emailFromAddress("test_sender@mailtest.com")
				.mailSubject("Subject")
				.to(new String[]{})
				.mailText("Mail Text")
				.htmlText("Html Text")
				.attachmentPath("/tmp/reportingFlow.log")
				.build();

		validMailKoAttachment = MailTo.builder()
				.emailFromAddress("test_sender@mailtest.com")
				.mailSubject("Subject")
				.to(new String[]{})
				.mailText("Mail Text")
				.htmlText("Html Text")
				.attachmentPath("/tmp/reportingFlow.log")
				.build();
	}

	private void setMimeMessage(MailTo mailTo) throws MessagingException {
		mimeMessage = javaMailSender.createMimeMessage();

		MimeMultipart multipart = new MimeMultipart();
		BodyPart mimeBodyPart = new PreencodedMimeBodyPart("8bit");
		mimeBodyPart.setContent("", "text/html");
		multipart.addBodyPart(mimeBodyPart);

		mimeMessage.setContent(multipart);
		mimeMessage.setFrom("FROM");
		mimeMessage.setSubject("SUBJECT");

		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
		if (mailTo!=null) {
			helper.setFrom(mailTo.getEmailFromAddress());
			helper.setTo(mailTo.getTo());
			helper.setSubject(mailTo.getMailSubject());
			helper.setText(mailTo.getHtmlText());
			if (mailTo.getAttachmentPath()!=null){
				File attachment = new File(mailTo.getAttachmentPath());
				helper.addAttachment(attachment.getName(), attachment);
			}
		}
	}
}
