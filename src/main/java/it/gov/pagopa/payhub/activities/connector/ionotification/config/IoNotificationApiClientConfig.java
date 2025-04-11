package it.gov.pagopa.payhub.activities.connector.ionotification.config;

import it.gov.pagopa.payhub.activities.config.rest.ApiClientConfig;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rest.io-notification")
@SuperBuilder
@NoArgsConstructor
public class IoNotificationApiClientConfig extends ApiClientConfig {
}
