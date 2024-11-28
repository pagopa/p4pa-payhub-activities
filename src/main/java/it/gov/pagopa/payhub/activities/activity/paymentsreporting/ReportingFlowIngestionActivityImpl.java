package it.gov.pagopa.payhub.activities.activity.paymentsreporting;

import it.gov.pagopa.payhub.activities.activity.paymentsreporting.service.IngestionFileHandlerService;
import it.gov.pagopa.payhub.activities.activity.paymentsreporting.service.IngestionFileValidatorService;
import it.gov.pagopa.payhub.activities.activity.paymentsreporting.service.IngestionFlowRetrieverService;
import it.gov.pagopa.payhub.activities.dto.reportingflow.ReportingFlowIngestionActivityResult;
import it.gov.pagopa.payhub.activities.dto.reportingflow.IngestionFlowDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the `ReportingFlowIngestionActivity` interface.
 * Manages the ingestion of reporting flow files, including validation and processing.
 */
@Slf4j
@Component
public class ReportingFlowIngestionActivityImpl implements ReportingFlowIngestionActivity {
	private final IngestionFlowRetrieverService ingestionFlowRetrieverService;
	private final IngestionFileValidatorService ingestionFileValidatorService;
	private final IngestionFileHandlerService ingestionFileHandlerService;

	/**
	 * Constructor for `ReportingFlowIngestionActivityImpl`.
	 *
	 * @param ingestionFlowRetrieverService service for retrieving ingestion flow details.
	 * @param ingestionFileValidatorService service for validating ingestion files.
	 * @param ingestionFileHandlerService service for handling ingestion files.
	 */
	public ReportingFlowIngestionActivityImpl(IngestionFlowRetrieverService ingestionFlowRetrieverService,
	                                          IngestionFileValidatorService ingestionFileValidatorService,
	                                          IngestionFileHandlerService ingestionFileHandlerService) {
		this.ingestionFlowRetrieverService = ingestionFlowRetrieverService;
		this.ingestionFileValidatorService = ingestionFileValidatorService;
		this.ingestionFileHandlerService = ingestionFileHandlerService;
	}

	/**
	 * Processes an ingestion flow file.
	 *
	 * @param ingestionFlowId the ID of the ingestion flow to process.
	 * @return the result of the ingestion activity, including status and processed items.
	 */
	@Override
	public ReportingFlowIngestionActivityResult processFile(String ingestionFlowId) {
		List<String> iufList = new ArrayList<>();
		boolean success = true;

		try {
			IngestionFlowDTO ingestionFlowDTO = ingestionFlowRetrieverService.getIngestionFlow(Long.valueOf(ingestionFlowId));

			ingestionFileValidatorService.validate(ingestionFlowDTO.getFilePathName(), ingestionFlowDTO.getFileName(), ingestionFlowDTO.getRequestTokenCode());

			Path xmlWorkingPath = ingestionFileHandlerService.setUpProcess(ingestionFlowDTO.getFilePathName(), ingestionFlowDTO.getFileName());

			ingestionFileHandlerService.finalizeProcess(ingestionFlowDTO.getFilePathName(), xmlWorkingPath);
		} catch (Exception e) {
			log.error("Error during IngestionActivity flowId {} due to: {}", ingestionFlowId, e.getMessage());
			success = false;
		}
		return new ReportingFlowIngestionActivityResult(iufList, success);
	}
}
