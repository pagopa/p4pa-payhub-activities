package it.gov.pagopa.payhub.activities.dao;

import it.gov.pagopa.payhub.activities.dto.reportingflow.IngestionFlowDTO;

import java.util.Optional;

public interface IngestionFlowDao {
	/**
	 * It will return the requested IngestionFlowDTO entity from its id
	 * @param ingestionFlowId
	 * @return
	 */
	Optional<IngestionFlowDTO> getIngestionFlow(Long ingestionFlowId);
}
