package it.gov.pagopa.payhub.activities.connector.debtposition.config;

import it.gov.pagopa.payhub.activities.connector.config.ClientConfig;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rest.debt-position")
@SuperBuilder
@NoArgsConstructor
public class DebtPositionClientConfig extends ClientConfig {
}
