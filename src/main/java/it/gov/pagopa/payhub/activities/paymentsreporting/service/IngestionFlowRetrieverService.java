package it.gov.pagopa.payhub.activities.paymentsreporting.service;

import it.gov.pagopa.payhub.activities.dao.IngestionFlowDao;
import it.gov.pagopa.payhub.activities.exception.IngestionFlowNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class IngestionFlowRetrieverService {
	private final IngestionFlowDao ingestionFlowDao;

	public IngestionFlowRetrieverService(IngestionFlowDao ingestionFlowDao) {
		this.ingestionFlowDao = ingestionFlowDao;
	}

	/**
	 * Updates the status of a IngestionFlow record identified by the provided ID.
	 *
	 * @param ingestionFlowId  the unique identifier of the record to update.
	 * @param status the new status to set.
	 * @return true if the update was successful, false otherwise.
	 */
	public Boolean updateIngestionFlow(Long ingestionFlowId, String status) {
		Optional<Boolean> updateIngestionFlow = ingestionFlowDao.updateIngestionFlow(ingestionFlowId, status);
		return updateIngestionFlow
				.orElseThrow(() -> new IngestionFlowNotFoundException("Cannot update ingestionFlow having id: "+ ingestionFlowId));
	}
}
