package it.gov.pagopa.payhub.activities.activity.paymentsreporting;

import it.gov.pagopa.payhub.activities.connector.pagopapayments.PaymentsReportingPagoPaService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.pu.pagopapayments.dto.generated.PaymentsReportingIdDTO;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile.FlowFileTypeEnum;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;

@Lazy
@Service
public class OrganizationPaymentsReportingPagoPaRetrieverActivityImpl implements OrganizationPaymentsReportingPagoPaRetrieverActivity {
	private static final FlowFileTypeEnum FLOW_FILE_TYPE = FlowFileTypeEnum.PAYMENTS_REPORTING_PAGOPA;

	private final PaymentsReportingPagoPaService paymentsReportingPagoPaService;
	private final IngestionFlowFileService ingestionFlowFileService;

	public OrganizationPaymentsReportingPagoPaRetrieverActivityImpl(PaymentsReportingPagoPaService paymentsReportingPagoPaService,
	                                                                IngestionFlowFileService ingestionFlowFileService) {
		this.paymentsReportingPagoPaService = paymentsReportingPagoPaService;
		this.ingestionFlowFileService = ingestionFlowFileService;
	}

	@Override
	public List<Long> retrieve(Long organizationId) {
		List<PaymentsReportingIdDTO> paymentsReportingIds = paymentsReportingPagoPaService.getPaymentsReportingList(organizationId);
		OffsetDateTime oldestDate = findOldestDate(paymentsReportingIds);

		List<IngestionFlowFile> ingestionFlowFiles = ingestionFlowFileService.findByOrganizationIdFlowTypeCreateDate(organizationId, FLOW_FILE_TYPE, oldestDate);

		getFilterNotImportedByFileName(ingestionFlowFiles, paymentsReportingIds);

		// TODO in P4ADEV-2005: implement loop to retrieve ingestion flow file from PagoPA
		return List.of();
	}

	private List<PaymentsReportingIdDTO> getFilterNotImportedByFileName(List<IngestionFlowFile> ingestionFlowFiles, List<PaymentsReportingIdDTO> paymentsReportingIds) {
		List<String> importedFileNames = ingestionFlowFiles.stream()
			.map(IngestionFlowFile::getFileName)
			.toList();

		return paymentsReportingIds.stream()
			.filter(item -> !importedFileNames.contains(item.getPaymentsReportingFileName()))
			.toList();
	}

	private OffsetDateTime findOldestDate(List<PaymentsReportingIdDTO> paymentsReportingIds) {
		return paymentsReportingIds.stream()
			.map(PaymentsReportingIdDTO::getFlowDateTime)
			.min(Comparator.naturalOrder())
			.orElse(null);
	}
}
