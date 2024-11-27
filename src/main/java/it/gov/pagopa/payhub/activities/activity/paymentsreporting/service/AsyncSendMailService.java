package it.gov.pagopa.payhub.activities.activity.paymentsreporting.service;

import it.gov.pagopa.payhub.activities.exception.SendMailException;
import it.gov.pagopa.payhub.activities.model.MailParams;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executor;

@Service
@Slf4j
public class AsyncSendMailService {
  @Value("${async.sendMail.corePoolSize:2}")
  private String corePoolSize;
  @Value("${async.sendMail.maxPoolSize:10}")
  private String maxPoolSize;
  @Value("${async.sendMail.queueCapacity:500}")
  private String queueCapacity;


  @Async("sendMailTaskExecutor")
  @Retryable(value = MailException.class, maxAttemptsExpression = "${async.sendMail.retry.maxAttempts}",
          backoff = @Backoff(random = true, delayExpression = "${async.sendMail.retry.delay}",
                  maxDelayExpression = "${async.sendMail.retry.maxDelay}", multiplierExpression = "${async.sendMail.retry.multiplier}"))
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
        log.info("sending mail message");
      } );
      log.info("MAIL has been send");
    }
    catch (Exception e) {
      log.info("MAIL error");
      throw new SendMailException("Error in mail sending");
    }
  }

  @Recover
  private void recover(MailException e, String[] to, String[] cc, String subject, String htmlText){
    //TODO write fail to db or queue for retry, in case
  }

  @Bean("sendMailTaskExecutor")
  public Executor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(Integer.parseInt(corePoolSize));
    executor.setMaxPoolSize(Integer.parseInt(maxPoolSize));
    executor.setQueueCapacity(Integer.parseInt(queueCapacity));
    executor.setThreadNamePrefix("BatchSendMail-");
    executor.initialize();
    return executor;
  }

}
