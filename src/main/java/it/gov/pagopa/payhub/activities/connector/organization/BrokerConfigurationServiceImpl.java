package it.gov.pagopa.payhub.activities.connector.organization;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.organization.client.BrokerConfigurationClient;
import it.gov.pagopa.pu.organization.dto.generated.EmailServerConfigDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Slf4j
@Service
public class BrokerConfigurationServiceImpl implements BrokerConfigurationService {
	private final AuthnService authnService;
	private final BrokerConfigurationClient brokerConfigurationClient;

	public BrokerConfigurationServiceImpl(AuthnService authnService, BrokerConfigurationClient brokerConfigurationClient) {
		this.authnService = authnService;
		this.brokerConfigurationClient = brokerConfigurationClient;
	}

	@Override
	public EmailServerConfigDTO getBrokerEmailServerConfig(Long brokerId) {
		log.debug("Get BrokerEmailServerConfig by brokerId: {}", brokerId);
		return brokerConfigurationClient.getBrokerEmailServerConfig(brokerId, authnService.getAccessToken());
	}
}
