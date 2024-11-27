/*
package it.gov.pagopa.payhub.activities.activity.reportingflow.service;


import it.gov.pagopa.payhub.activities.exception.SendMailException;
import it.gov.pagopa.payhub.activities.helper.EmailHelper;
import it.gov.pagopa.payhub.activities.helper.MailParameterHelper;
import it.gov.pagopa.payhub.activities.model.MailParams;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Properties;

@Service
@Slf4j
public class IngestionFlowMailService {
  private final AsyncSendMailService asyncSendMailService;
  private final MailParams mailParams;
  private final JavaMailSender javaMailSender;

  public IngestionFlowMailService(AsyncSendMailService asyncSendMailService, MailParams mailParams, JavaMailSender javaMailSender) {
    this.asyncSendMailService = asyncSendMailService;
    this.mailParams = mailParams;
    this.javaMailSender = javaMailSender;
  }

  public boolean sendEmail(String ingestionFlowId, boolean success){
    // verify if previous operation is success
    if (success){
      try {
        // get e-mail parameters
        MailParams params = MailParameterHelper.getMailParams(mailParams);

        // send e-mail if there are no errors in parameters
        if (params.isSuccess()){
          mailParams.setHtmlText(params.getHtmlText());
          mailParams.setMailSubject(params.getMailSubject());
          mailParams.setIngestionFlowId(ingestionFlowId);
          asyncSendMailService.sendMail(javaMailSender, mailParams);
          return true;
        }
      } catch (Exception e) {
          throw new SendMailException("Error sending mail for id: "+ingestionFlowId);
      }
    }
    return false;
  }

}

 */