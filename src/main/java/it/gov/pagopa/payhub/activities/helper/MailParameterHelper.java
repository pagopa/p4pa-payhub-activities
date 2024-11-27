package it.gov.pagopa.payhub.activities.helper;

import it.gov.pagopa.payhub.activities.dto.reportingflow.IngestionFlowDTO;
import it.gov.pagopa.payhub.activities.exception.SendMailException;
import it.gov.pagopa.payhub.activities.model.MailParams;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MailParameterHelper {
    public static MailParams getMailParams(MailParams mailParams) {

        IngestionFlowDTO  ingestionFlowDTO = mailParams.getIngestionFlowDTO();


        String fileName = ingestionFlowDTO.getFileName();
        Long fileSize = ingestionFlowDTO.getDownloadedFileSize();
        Long totalRowsNumber = ingestionFlowDTO.getTotalRowsNumber();
        DateFormat parser = new SimpleDateFormat("EEE, MMM dd yyyy, hh:mm:ss");
        String actualDate = parser.format(new Date());
        String mailText = "Il caricamento del file " + fileName;
        if (fileSize>0 && totalRowsNumber>0) {
            mailText += " è andato a buon fine, tutti i " + totalRowsNumber + " dati presenti sono stati caricati correttamente.";
        }
        else  {
            mailText += " NON è andato a buon fine";
        }

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
            Map<String,String> map = new HashMap<>();
            map.put("testomail", mailText);
            map.put("dataAttuale",actualDate);
            map.put("nomeFile", fileName);
            params.setMailSubject(mailSubject);
            params.setHtmlText(htmlText);
            params.setParams(map);
            params.setSuccess(true);
            return params;
        }
        catch (Exception e) {
            throw new SendMailException("Error in mail configuration");
        }
    }
}
