package it.gov.pagopa.payhub.activities.helper;

import java.io.InputStream;
import java.util.Properties;

public class EmailHelper {
    /**
     *
     * @return Properties
     */
    public Properties getProperties() {
        Properties properties = new Properties();
        try {
            InputStream inputStream = EmailHelper.class.getClassLoader().getResourceAsStream("mail-templates.properties");
            properties.load(inputStream);
        } catch (Exception e) {
            return null;
        }
        return properties;
    }
}
