package it.gov.pagopa.payhub.activities.activity.paymentsreporting;

import it.gov.pagopa.payhub.activities.connector.pagopapayments.PaymentsReportingPagoPaService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.pu.pagopapayments.dto.generated.PaymentsReportingIdDTO;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile.IngestionFlowFileTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Lazy
@Slf4j
@Service
public class OrganizationPaymentsReportingPagoPaListRetrieverActivityImpl implements OrganizationPaymentsReportingPagoPaListRetrieverActivity {
	private final PaymentsReportingPagoPaService paymentsReportingPagoPaService;
	private final IngestionFlowFileService ingestionFlowFileService;

	public OrganizationPaymentsReportingPagoPaListRetrieverActivityImpl(PaymentsReportingPagoPaService paymentsReportingPagoPaService,
	                                                                    IngestionFlowFileService ingestionFlowFileService) {
		this.paymentsReportingPagoPaService = paymentsReportingPagoPaService;
		this.ingestionFlowFileService = ingestionFlowFileService;
	}

	@Override
	public List<PaymentsReportingIdDTO> retrieveNotImportedPagoPaPaymentsReportingIds(Long organizationId) {
		log.info("Retrieving payments reporting from PagoPA for the organization ID: {}", organizationId);
		List<PaymentsReportingIdDTO> paymentsReportingIds = paymentsReportingPagoPaService.getPaymentsReportingList(organizationId);
		if (paymentsReportingIds.isEmpty()) {
			return Collections.emptyList();
		}
		OffsetDateTime oldestDate = findOldestDate(paymentsReportingIds);
		List<IngestionFlowFile> ingestionFlowFiles = ingestionFlowFileService.findByOrganizationIdFlowTypeCreateDate(organizationId, IngestionFlowFileTypeEnum.PAYMENTS_REPORTING_PAGOPA, oldestDate);

		return getNotImportedFilteredByFileName(ingestionFlowFiles, paymentsReportingIds);
	}

	/**
	 * Filters the list of PaymentsReportingIdDTOs to find those that have not been imported yet based on file names.
	 *
	 * @param ingestionFlowFiles the list of ingestion flow files
	 * @param paymentsReportingIds the list of payments reporting IDs
	 * @return a list of PaymentsReportingIdDTOs that have not been imported
	 */
	private List<PaymentsReportingIdDTO> getNotImportedFilteredByFileName(List<IngestionFlowFile> ingestionFlowFiles, List<PaymentsReportingIdDTO> paymentsReportingIds) {
		Set<String> importedFileNames = ingestionFlowFiles.stream()
			.map(IngestionFlowFile::getFileName)
			.collect(Collectors.toSet());

		return paymentsReportingIds.stream()
			.filter(item -> !importedFileNames.contains(item.getPaymentsReportingFileName()))
			.toList();
	}

	/**
	 * Finds the oldest date in the list of PaymentsReportingIdDTOs.
	 *
	 * @param paymentsReportingIds the list of payments reporting IDs
	 * @return the oldest date found in the list, or null if the list is empty
	 */
	private OffsetDateTime findOldestDate(List<PaymentsReportingIdDTO> paymentsReportingIds) {
		return paymentsReportingIds.stream()
			.map(PaymentsReportingIdDTO::getFlowDateTime)
			.min(Comparator.naturalOrder())
			.orElse(null);
	}
}
