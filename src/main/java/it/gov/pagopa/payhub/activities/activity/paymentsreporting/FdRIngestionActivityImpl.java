package it.gov.pagopa.payhub.activities.activity.paymentsreporting;

import it.gov.pagopa.payhub.activities.activity.paymentsreporting.service.IngestionFileHandlerService;
import it.gov.pagopa.payhub.activities.activity.paymentsreporting.service.IngestionFileValidatorService;
import it.gov.pagopa.payhub.activities.activity.paymentsreporting.service.IngestionFlowRetrieverService;
import it.gov.pagopa.payhub.activities.dto.reportingflow.FdRIngestionActivityResult;
import it.gov.pagopa.payhub.activities.dto.reportingflow.IngestionFlowDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class FdRIngestionActivityImpl implements FdRIngestionActivity {
	private final IngestionFlowRetrieverService ingestionFlowRetrieverService;
	private final IngestionFileValidatorService ingestionFileValidatorService;
	private final IngestionFileHandlerService ingestionFileHandlerService;

	public FdRIngestionActivityImpl(IngestionFlowRetrieverService ingestionFlowRetrieverService,
	                                IngestionFileValidatorService ingestionFileValidatorService,
	                                IngestionFileHandlerService ingestionFileHandlerService) {
		this.ingestionFlowRetrieverService = ingestionFlowRetrieverService;
		this.ingestionFileValidatorService = ingestionFileValidatorService;
		this.ingestionFileHandlerService = ingestionFileHandlerService;
	}

	@Override
	public FdRIngestionActivityResult processFile(String ingestionFlowId) {
		List<String> iufList = new ArrayList<>();
		boolean success = true;

		try {
			IngestionFlowDTO ingestionFlowDTO = ingestionFlowRetrieverService.getIngestionFlow(Long.valueOf(ingestionFlowId));

			ingestionFileValidatorService.validate(ingestionFlowDTO.getFilePathName(), ingestionFlowDTO.getFileName(), ingestionFlowDTO.getRequestTokenCode());

			ingestionFileHandlerService.setUpProcess(ingestionFlowDTO.getFilePathName(), ingestionFlowDTO.getFileName());
		} catch (Exception e) {
			log.error("Error during IngestionActivity flowId {} due to: {}", ingestionFlowId, e.getMessage());
			success = false;
		}
		return new FdRIngestionActivityResult(iufList, success);
	}
}
