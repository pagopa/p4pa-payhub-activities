package it.gov.pagopa.payhub.activities.activity.reportingflow.service;

import it.gov.pagopa.payhub.activities.dto.reportingflow.IngestionFlowDTO;
import it.gov.pagopa.payhub.activities.exception.SendMailException;
import it.gov.pagopa.payhub.activities.model.MailParams;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AsyncSendMailService {
  @Value("${async.sendMail.corePoolSize:2}")
  private String corePoolSize;
  @Value("${async.sendMail.maxPoolSize:10}")
  private String maxPoolSize;
  @Value("${async.sendMail.queueCapacity:500}")
  private String queueCapacity;

  public void sendMail(JavaMailSender javaMailSender, MailParams mailParams) {
    try {
      javaMailSender.send( mimeMessage -> {
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        message.setFrom(mailParams.getEmailFromAddress(), mailParams.getEmailFromName());
        message.setTo(mailParams.getTo());
        if(ArrayUtils.isNotEmpty(mailParams.getCc()))
          message.setCc(mailParams.getCc());
        message.setSubject(mailParams.getMailSubject());
        String plainText = Jsoup.clean(mailParams.getHtmlText(), "", Safelist.none(), new Document.OutputSettings().prettyPrint(false));
        message.setText(plainText, mailParams.getHtmlText());
        log.debug("sending mail message");
      } );
      log.info("MAIL has been send");
    }
    catch (Exception e) {
      log.info("MAIL error");
      throw new SendMailException("Error in mail sending");
    }
  }
}
