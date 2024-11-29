package it.gov.pagopa.payhub.activities.activity.paymentsreporting;

import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFileHandlerService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowRetrieverService;
import it.gov.pagopa.payhub.activities.dto.reportingflow.ReportingFlowIngestionActivityResult;
import it.gov.pagopa.payhub.activities.dto.ingestionflow.IngestionFlowDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ReportingFlowIngestionActivityImpl implements ReportingFlowIngestionActivity {
	private final IngestionFlowRetrieverService ingestionFlowRetrieverService;
	private final IngestionFileHandlerService ingestionFileHandlerService;

	public ReportingFlowIngestionActivityImpl(IngestionFlowRetrieverService ingestionFlowRetrieverService,
	                                          IngestionFileHandlerService ingestionFileHandlerService) {
		this.ingestionFlowRetrieverService = ingestionFlowRetrieverService;
		this.ingestionFileHandlerService = ingestionFileHandlerService;
	}

	@Override
	public ReportingFlowIngestionActivityResult processFile(String ingestionFlowId) {
		List<String> iufList = new ArrayList<>();
		boolean success = true;

		try {
			IngestionFlowDTO ingestionFlowDTO = ingestionFlowRetrieverService.getIngestionFlow(Long.valueOf(ingestionFlowId));
			ingestionFileHandlerService.setUpProcess(ingestionFlowDTO.getFilePathName(), ingestionFlowDTO.getFileName());

		} catch (Exception e) {
			log.error("Error during IngestionActivity flowId {} due to: {}", ingestionFlowId, e.getMessage());
			success = false;
		}
		return new ReportingFlowIngestionActivityResult(iufList, success);
	}
}
