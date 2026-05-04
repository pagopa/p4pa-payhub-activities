package it.gov.pagopa.payhub.activities.connector.organization.client;

import it.gov.pagopa.payhub.activities.connector.organization.config.OrganizationApisHolder;
import it.gov.pagopa.pu.organization.dto.generated.EmailServerConfigDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@Lazy
@Service
public class BrokerConfigurationClient {
	private final OrganizationApisHolder organizationApisHolder;

	public BrokerConfigurationClient(OrganizationApisHolder organizationApisHolder) {
		this.organizationApisHolder = organizationApisHolder;
	}

	public EmailServerConfigDTO getBrokerEmailServerConfig(Long brokerId, String accessToken) {
		try {
			return organizationApisHolder.getBrokerConfigurationApi(accessToken).getBrokerEmailServerConfig(brokerId);
		} catch (HttpClientErrorException.NotFound e) {
			log.info("BrokerConfiguration having brokerId {} not found", brokerId);
			return null;
		}
	}
}
