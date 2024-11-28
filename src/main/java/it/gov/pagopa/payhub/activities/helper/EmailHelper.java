package it.gov.pagopa.payhub.activities.helper;

import it.gov.pagopa.payhub.activities.exception.SendMailException;

import java.io.InputStream;
import java.util.Properties;

public final class EmailHelper {
    /**
     *
     * @return mail template Properties from properties
     */
    public Properties getProperties() {
        Properties properties = new Properties();
        try {
            InputStream inputStream = EmailHelper.class.getClassLoader().getResourceAsStream("mail-templates.properties");
            properties.load(inputStream);
        } catch (Exception e) {
            throw new SendMailException("Error in mail template configuration");
        }
        return properties;
    }
}
