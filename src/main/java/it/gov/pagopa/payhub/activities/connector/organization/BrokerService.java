package it.gov.pagopa.payhub.activities.connector.organization;

import it.gov.pagopa.pu.organization.dto.generated.Broker;

import java.util.List;

/**
 * This interface provides methods that manage Brokers
 */
public interface BrokerService {
	List<Broker> fetchAll();
}
