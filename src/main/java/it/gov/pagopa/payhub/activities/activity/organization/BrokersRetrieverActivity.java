package it.gov.pagopa.payhub.activities.activity.organization;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.pu.organization.dto.generated.Broker;

import java.util.List;

/**
 * Interface for retrieving all PagoPA brokers.
 */
@ActivityInterface
public interface BrokersRetrieverActivity {
	/**
	 * Fetch all PagoPA brokers.
	 *
	 * @return a list of brokers
	 */
	@ActivityMethod
	List<Broker> fetchAll();
}
