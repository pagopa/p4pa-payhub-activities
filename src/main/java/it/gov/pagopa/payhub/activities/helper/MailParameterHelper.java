package it.gov.pagopa.payhub.activities.helper;

import it.gov.pagopa.payhub.activities.dto.MailDTO;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.util.Assert;

import java.io.InputStream;
import java.util.Properties;

public final class MailParameterHelper {
    private MailParameterHelper() {
    }

    /**
     *  helper for composing e-mail parameters
     *
     * @param mailDTO parameters not updated
     * @return parameters updated
     */
    public static MailDTO getMailParameters(MailDTO mailDTO) throws Exception {
        Properties mailProperties = getProperties();
        Assert.notEmpty(mailProperties.values(), "Wrong mail configuration");
        String templateName = mailDTO.getTemplateName();
        String subject = mailProperties.getProperty("template."+templateName+".subject");
        String body = mailProperties.getProperty("template."+templateName+".body");
        Assert.notNull(subject, "Invalid email template (missing subject) "+templateName);
        Assert.notNull(body, "Invalid email template (missing body) "+templateName);
        mailDTO.setMailSubject(StringSubstitutor.replace(subject, mailDTO.getParams(), "{", "}"));
        mailDTO.setHtmlText(StringSubstitutor.replace(body, mailDTO.getParams(), "{", "}"));
        return mailDTO;
    }

    /**
     * helper for loading mail template properties
     *
     * @return Properties for mail templates
     */
    public static Properties getProperties() throws Exception {
        Properties templateProperties = new Properties();
        InputStream inputStream = MailParameterHelper.class.getClassLoader().getResourceAsStream("mail-templates.properties");
        templateProperties.load(inputStream);
        return templateProperties;
    }
}
