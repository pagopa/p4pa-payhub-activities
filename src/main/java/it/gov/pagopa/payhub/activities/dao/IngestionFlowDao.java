package it.gov.pagopa.payhub.activities.dao;

import java.util.Optional;

public interface IngestionFlowDao {
	/**
	 * update ingestionFlow status
	 * @param ingestionFlowId ingestion flow id
	 * @param status new status
	 * @return boolean true if the status has been updated
	 */
	Optional<Boolean> updateIngestionFlow(Long ingestionFlowId, String status);
}
