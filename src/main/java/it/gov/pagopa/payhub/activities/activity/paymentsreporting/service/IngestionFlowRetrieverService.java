package it.gov.pagopa.payhub.activities.activity.paymentsreporting.service;

import it.gov.pagopa.payhub.activities.dao.IngestionFlowDao;
import it.gov.pagopa.payhub.activities.dto.reportingflow.IngestionFlowDTO;
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

	public IngestionFlowDTO getIngestionFlow(Long ingestionFlowId) {
		Optional<IngestionFlowDTO> ingestionFlow = ingestionFlowDao.getIngestionFlow(ingestionFlowId);

		return ingestionFlow
			.orElseThrow(() -> new IngestionFlowNotFoundException("Cannot found ingestionFlow having id: "+ ingestionFlowId));
	}
}
