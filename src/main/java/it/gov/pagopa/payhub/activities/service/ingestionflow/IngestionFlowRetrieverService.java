package it.gov.pagopa.payhub.activities.service.ingestionflow;

import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.exception.IngestionFlowNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class responsible for retrieving ingestion flow data from the data source.
 */
@Lazy
@Slf4j
@Service
public class IngestionFlowRetrieverService {

	private final IngestionFlowFileDao ingestionFlowFileDao;

	public IngestionFlowRetrieverService(IngestionFlowFileDao ingestionFlowFileDao) {
		this.ingestionFlowFileDao = ingestionFlowFileDao;
	}

	/**
	 * Retrieves the ingestion flow data for the specified ID.
	 *
	 * @param ingestionFlowId the ID of the ingestion flow to retrieve.
	 * @return an {@link IngestionFlowFileDTO} containing the details of the ingestion flow.
	 * @throws IngestionFlowNotFoundException if no ingestion flow is found with the specified ID.
	 */
	public IngestionFlowFileDTO getIngestionFlow(Long ingestionFlowId) {
		Optional<IngestionFlowFileDTO> ingestionFlow = ingestionFlowFileDao.getIngestionFlow(ingestionFlowId);

		return ingestionFlow
			.orElseThrow(() -> new IngestionFlowNotFoundException("Cannot found ingestionFlow having id: "+ ingestionFlowId));
	}
}
