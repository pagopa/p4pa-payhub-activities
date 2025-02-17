package it.gov.pagopa.payhub.activities.connector.pagopapayments.config;

import it.gov.pagopa.payhub.activities.config.ApiClientConfig;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rest.pagopa-payments")
@SuperBuilder
@NoArgsConstructor
public class PagoPaPaymentsApiClientConfig extends ApiClientConfig {
}
