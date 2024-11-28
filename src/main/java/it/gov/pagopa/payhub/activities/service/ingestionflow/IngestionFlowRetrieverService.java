package it.gov.pagopa.payhub.activities.service.ingestionflow;

import it.gov.pagopa.payhub.activities.dao.IngestionFlowDao;
import it.gov.pagopa.payhub.activities.dto.ingestionflow.IngestionFlowDTO;
import it.gov.pagopa.payhub.activities.exception.IngestionFlowNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class responsible for retrieving ingestion flow data from the data source.
 */
@Service
@Slf4j
public class IngestionFlowRetrieverService {

	private final IngestionFlowDao ingestionFlowDao;

	/**
	 * Constructor for `IngestionFlowRetrieverService`.
	 *
	 * @param ingestionFlowDao the DAO used to retrieve ingestion flow data.
	 */
	public IngestionFlowRetrieverService(IngestionFlowDao ingestionFlowDao) {
		this.ingestionFlowDao = ingestionFlowDao;
	}

	/**
	 * Retrieves the ingestion flow data for the specified ID.
	 *
	 * @param ingestionFlowId the ID of the ingestion flow to retrieve.
	 * @return an {@link IngestionFlowDTO} containing the details of the ingestion flow.
	 * @throws IngestionFlowNotFoundException if no ingestion flow is found with the specified ID.
	 */
	public IngestionFlowDTO getIngestionFlow(Long ingestionFlowId) {
		Optional<IngestionFlowDTO> ingestionFlow = ingestionFlowDao.getIngestionFlow(ingestionFlowId);

		return ingestionFlow
			.orElseThrow(() -> new IngestionFlowNotFoundException("Cannot found ingestionFlow having id: "+ ingestionFlowId));
	}
}
