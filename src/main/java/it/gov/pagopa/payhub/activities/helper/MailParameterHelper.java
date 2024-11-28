package it.gov.pagopa.payhub.activities.helper;

import it.gov.pagopa.payhub.activities.dto.reportingflow.IngestionFlowDTO;
import it.gov.pagopa.payhub.activities.exception.SendMailException;
import it.gov.pagopa.payhub.activities.model.MailParams;
import it.gov.pagopa.payhub.activities.utils.Constants;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.util.Assert;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public final class MailParameterHelper {
    private MailParameterHelper() {
    }

    /**
     * helper for e-mail
     * @param mailParams  parameters not updated
     * @return MailParams parameters updated
     */
    public static MailParams getMailParams(MailParams mailParams) {
        IngestionFlowDTO ingestionFlowDTO = mailParams.getIngestionFlowDTO();

        String fileName = ingestionFlowDTO.getFileName();
        DateFormat parser = new SimpleDateFormat("EEE, MMM dd yyyy, hh:mm:ss");
        String actualDate = parser.format(new Date());

        try {
            Properties mailProperties = EmailHelper.getProperties();
            Assert.notEmpty(mailProperties.values(), "Wrong mail configuration");
            String templateName = mailParams.getTemplateName();
            String subject = mailProperties.getProperty("template."+templateName+".subject");
            String body = mailProperties.getProperty("template."+templateName+".body");
            Assert.notNull(subject, "Invalid email template (missing subject) "+templateName);
            Assert.notNull(body, "Invalid email template (missing body) "+templateName);

            String mailSubject = StringSubstitutor.replace(subject, mailParams.getParams(), "{", "}");
            String htmlText = StringSubstitutor.replace(body, mailParams.getParams(), "{", "}");

            //MailParams params = new MailParams();
            Map<String,String> map = new HashMap<>();
            map.put(Constants.MAIL_TEXT, mailParams.getMailText());
            map.put(Constants.ACTUAL_DATE,actualDate);
            map.put(Constants.FILE_NAME, fileName);
            mailParams.setMailSubject(mailSubject);
            mailParams.setHtmlText(htmlText);
            mailParams.setParams(map);
            mailParams.setSuccess(true);
            return mailParams;
        }
        catch (SendMailException sendMailException) {
            throw sendMailException;
        }
        catch (Exception e) {
            throw new SendMailException("Error in mail parameters");
        }
    }
}
