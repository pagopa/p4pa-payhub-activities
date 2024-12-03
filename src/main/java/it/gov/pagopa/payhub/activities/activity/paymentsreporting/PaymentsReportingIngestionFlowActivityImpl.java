package it.gov.pagopa.payhub.activities.activity.paymentsreporting;

import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowRetrieverService;
import it.gov.pagopa.payhub.activities.dto.reportingflow.PaymentsReportingIngestionFlowActivityResult;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.IngestionFlowFileDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Lazy
@Component
public class PaymentsReportingIngestionFlowActivityImpl implements PaymentsReportingIngestionFlowActivity {
	private final IngestionFlowRetrieverService ingestionFlowRetrieverService;
	private final IngestionFileRetrieverService ingestionFileRetrieverService;

	public PaymentsReportingIngestionFlowActivityImpl(IngestionFlowRetrieverService ingestionFlowRetrieverService,
	                                                  IngestionFileRetrieverService ingestionFileRetrieverService) {
		this.ingestionFlowRetrieverService = ingestionFlowRetrieverService;
		this.ingestionFileRetrieverService = ingestionFileRetrieverService;
	}

	@Override
	public PaymentsReportingIngestionFlowActivityResult processFile(Long ingestionFlowId) {
		List<String> iufList = new ArrayList<>();
		boolean success = true;

		try {
			IngestionFlowFileDTO ingestionFlowFileDTO = ingestionFlowRetrieverService.getIngestionFlow(ingestionFlowId);
			ingestionFileRetrieverService.retrieveFile(Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());

		} catch (Exception e) {
			log.error("Error during IngestionActivity ingestionFlowId {} due to: {}", ingestionFlowId, e.getMessage());
			success = false;
		}
		return new PaymentsReportingIngestionFlowActivityResult(iufList, success);
	}
}
