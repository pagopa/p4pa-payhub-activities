package it.gov.pagopa.payhub.activities.connector.organization;

import it.gov.pagopa.pu.organization.dto.generated.Broker;
import it.gov.pagopa.pu.organization.dto.generated.BrokerConfiguration;

import java.util.List;

/**
 * This interface provides methods that manage Brokers
 */
public interface BrokerService {
	List<Broker> fetchAll();

	Broker getBrokerByFiscalCode(String fiscalCode);

	Broker getBrokerById(Long brokerId);

	BrokerConfiguration getBrokerConfigurationsById(Long brokerId);
}
