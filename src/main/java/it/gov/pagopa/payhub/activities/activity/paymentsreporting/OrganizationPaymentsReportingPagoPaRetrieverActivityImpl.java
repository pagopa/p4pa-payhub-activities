package it.gov.pagopa.payhub.activities.activity.paymentsreporting;

import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.connector.pagopapayments.PaymentsReportingPagoPaService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.pagopapayments.dto.generated.PaymentsReportingIdDTO;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile.IngestionFlowFileTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Lazy
@Slf4j
@Service
public class OrganizationPaymentsReportingPagoPaRetrieverActivityImpl implements OrganizationPaymentsReportingPagoPaRetrieverActivity {

	private final OrganizationService organizationService;
	private final PaymentsReportingPagoPaService paymentsReportingPagoPaService;
	private final IngestionFlowFileService ingestionFlowFileService;

	public OrganizationPaymentsReportingPagoPaRetrieverActivityImpl(OrganizationService organizationService, PaymentsReportingPagoPaService paymentsReportingPagoPaService,
                                                                    IngestionFlowFileService ingestionFlowFileService) {
        this.organizationService = organizationService;
        this.paymentsReportingPagoPaService = paymentsReportingPagoPaService;
		this.ingestionFlowFileService = ingestionFlowFileService;
	}

	@Override
	public List<Long> fetchPagoPaPaymentsReportingFiles(Long organizationId, List<PaymentsReportingIdDTO> paymentsReportingIds) {
		log.info("fetch payments reporting from PagoPA for the organization ID: {}", organizationId);
		if (paymentsReportingIds.isEmpty()) {
			return Collections.emptyList();
		}
		Set<String> alreadyProcessedFileNames = getFilenamesFilteredByStatus(organizationId, paymentsReportingIds);

		return paymentsReportingIds.stream()
			.filter(idDTO -> !alreadyProcessedFileNames.contains(idDTO.getPaymentsReportingFileName()))
			.map(idDTO -> {
				Organization organization = organizationService.getOrganizationById(organizationId).orElseThrow(() -> new IllegalArgumentException("Organization doesn't exists: " + organizationId));
				return paymentsReportingPagoPaService.fetchPaymentReporting(organization, idDTO.getPagopaPaymentsReportingId(), idDTO.getPaymentsReportingFileName(), Optional.ofNullable(idDTO.getRevision()).orElse(0).longValue(), null); //TODO null will be fixed in https://pagopa.atlassian.net/browse/P4ADEV-4297
            })
			.toList();
	}

	/**
	 * Filters the Set of PaymentsReportingIdDTOs to find those that have not been processed yet based on file names.
	 * @return a Set of file names that have not been processed
	 */
	private Set<String> getFilenamesFilteredByStatus(Long organizationId, List<PaymentsReportingIdDTO> paymentsReportingIds) {
		return paymentsReportingIds.stream().map(PaymentsReportingIdDTO::getPaymentsReportingFileName)
			.flatMap(fileName -> ingestionFlowFileService
				.findByOrganizationIdFlowTypeFilename(organizationId, IngestionFlowFileTypeEnum.PAYMENTS_REPORTING_PAGOPA, fileName)
				.stream())
			.map(IngestionFlowFile::getFileName)
			.collect(Collectors.toSet());
	}
}
