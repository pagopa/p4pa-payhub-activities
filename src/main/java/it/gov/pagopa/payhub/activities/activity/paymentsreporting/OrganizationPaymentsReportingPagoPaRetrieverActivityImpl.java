package it.gov.pagopa.payhub.activities.activity.paymentsreporting;

import it.gov.pagopa.payhub.activities.connector.pagopapayments.PaymentsReportingPagoPaService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.pu.pagopapayments.dto.generated.PaymentsReportingIdDTO;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile.FlowFileTypeEnum;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Lazy
@Slf4j
@Service
public class OrganizationPaymentsReportingPagoPaRetrieverActivityImpl implements OrganizationPaymentsReportingPagoPaRetrieverActivity {
	private final PaymentsReportingPagoPaService paymentsReportingPagoPaService;
	private final IngestionFlowFileService ingestionFlowFileService;

	public OrganizationPaymentsReportingPagoPaRetrieverActivityImpl(PaymentsReportingPagoPaService paymentsReportingPagoPaService,
	                                                                IngestionFlowFileService ingestionFlowFileService) {
		this.paymentsReportingPagoPaService = paymentsReportingPagoPaService;
		this.ingestionFlowFileService = ingestionFlowFileService;
	}

	@Override
	public List<Long> fetch(Long organizationId, List<PaymentsReportingIdDTO> paymentsReportingIds) {
		log.info("fetch payments reporting from PagoPA for the organization ID: {}", organizationId);
		if (paymentsReportingIds.isEmpty()) {
			return Collections.emptyList();
		}
		List<String> ingestionFlowFileNames = getFilenamesFilteredByStatus(organizationId, paymentsReportingIds);

		return paymentsReportingIds.stream()
			.filter(item -> ingestionFlowFileNames.contains(item.getPaymentsReportingFileName()))
			.map(item -> paymentsReportingPagoPaService.fetchPaymentReporting(organizationId, item.getPagopaPaymentsReportingId()))
			.toList();
	}

	/**
	 * Filters the list of PaymentsReportingIdDTOs to find those that have not been processed yet based on file names.
	 * @param organizationId
	 * @param paymentsReportingIds
	 * @return a list of file names that have not been processed
	 */
	private List<String> getFilenamesFilteredByStatus(Long organizationId, List<PaymentsReportingIdDTO> paymentsReportingIds) {
		return paymentsReportingIds.stream().map(PaymentsReportingIdDTO::getPaymentsReportingFileName)
			.map(item -> ingestionFlowFileService
				.findByOrganizationIdFlowTypeFilename(organizationId, FlowFileTypeEnum.PAYMENTS_REPORTING_PAGOPA, item))
			.flatMap(List::stream)
			.filter(item -> item.getStatus().equals(StatusEnum.UPLOADED))
			.map(IngestionFlowFile::getFileName)
			.toList();
	}
}
