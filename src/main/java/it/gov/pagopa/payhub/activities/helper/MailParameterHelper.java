package it.gov.pagopa.payhub.activities.helper;

import it.gov.pagopa.payhub.activities.exception.SendMailException;
import it.gov.pagopa.payhub.activities.model.MailParams;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.util.Assert;

import java.util.Properties;

public class MailParameterHelper {
    public static MailParams getMailParams(MailParams mailParams) {
        try {
            EmailHelper emailHelper = new EmailHelper();
            Properties mailProperties = emailHelper.getProperties();
            Assert.notEmpty(mailProperties.values(), "Wrong mail configuration");
            String templateName = mailParams.getTemplateName();
            String subject = mailProperties.getProperty("template."+templateName+".subject");
            String body = mailProperties.getProperty("template."+templateName+".body");
            Assert.notNull(subject, "Invalid email template (missing subject) "+templateName);
            Assert.notNull(body, "Invalid email template (missing body) "+templateName);

            String mailSubject = StringSubstitutor.replace(subject, mailParams.getParams(), "{", "}");
            String htmlText = StringSubstitutor.replace(body, mailParams.getParams(), "{", "}");

            MailParams params = new MailParams();
            params.setMailSubject(mailSubject);
            params.setHtmlText(htmlText);
            params.setSuccess(true);
            return params;
        }
        catch (Exception e) {
            throw new SendMailException("Error in mail configuration");
        }
    }
}
