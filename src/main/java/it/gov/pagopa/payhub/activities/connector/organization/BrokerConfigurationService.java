package it.gov.pagopa.payhub.activities.connector.organization;

import it.gov.pagopa.pu.organization.dto.generated.EmailServerConfigDTO;

public interface BrokerConfigurationService {
	EmailServerConfigDTO getBrokerEmailServerConfig(Long brokerId);
}
