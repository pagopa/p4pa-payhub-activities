package it.gov.pagopa.payhub.activities.activity.ingestionflow.receipt;

import it.gov.pagopa.payhub.activities.connector.classification.PaymentsReportingService;
import it.gov.pagopa.payhub.activities.connector.debtposition.ReceiptService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.classifications.PaymentsReportingTransferDTO;
import it.gov.pagopa.payhub.activities.exception.InvalidValueException;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.receipt.PaymentsReporting2ReceiptMapper;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptWithAdditionalNodeDataDTO;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Lazy
@Slf4j
@Component
public class PaymentsReportingImplicitReceiptHandlerActivityImpl implements PaymentsReportingImplicitReceiptHandlerActivity {
	private static final List<String> PAYMENT_OUTCOME_CODES = List.of("8", "9");

	private final PaymentsReportingService paymentsReportingService;
	private final OrganizationService organizationService;
	private final PaymentsReporting2ReceiptMapper paymentsReporting2ReceiptMapper;
	private final ReceiptService receiptService;

	public PaymentsReportingImplicitReceiptHandlerActivityImpl(PaymentsReportingService paymentsReportingService,
	                                                           OrganizationService organizationService,
	                                                           PaymentsReporting2ReceiptMapper paymentsReporting2ReceiptMapper,
	                                                           ReceiptService receiptService) {
		this.paymentsReportingService = paymentsReportingService;
		this.organizationService = organizationService;
		this.paymentsReporting2ReceiptMapper = paymentsReporting2ReceiptMapper;
		this.receiptService = receiptService;
	}

	@Override
	public void handleImplicitReceipt(PaymentsReportingTransferDTO paymentsReportingTransferDTO) {
		if (PAYMENT_OUTCOME_CODES.contains(paymentsReportingTransferDTO.getPaymentOutcomeCode())) {
			log.info("Retrieve payment reporting with payment outcome code {} for organization id: {} and iuv: {} and iur {} and transfer index: {}", paymentsReportingTransferDTO.getPaymentOutcomeCode(),
				paymentsReportingTransferDTO.getOrgId(), paymentsReportingTransferDTO.getIuv(), paymentsReportingTransferDTO.getIur(), paymentsReportingTransferDTO.getTransferIndex());
			PaymentsReporting paymentsReporting = paymentsReportingService.getBySemanticKey(paymentsReportingTransferDTO);

			String organizationFiscalCode = organizationService.getOrganizationById(paymentsReporting.getOrganizationId()).map(Organization::getOrgFiscalCode)
				.orElseThrow(() -> new InvalidValueException("Organization not found: " + paymentsReporting.getOrganizationId()));

			ReceiptWithAdditionalNodeDataDTO dummyReceipt = paymentsReporting2ReceiptMapper.map2DummyReceipt(paymentsReporting, organizationFiscalCode);

			ReceiptDTO dummyReceiptCreated = receiptService.createReceipt(dummyReceipt);
			log.info("Dummy Receipt has been created with id: {}", dummyReceiptCreated.getReceiptId());
		} else {
			log.info("Dummy Receipt generation is not needed with payment outcome code: {} for organization id: {} and iuv: {} and iur {} and transfer index: {}", paymentsReportingTransferDTO.getPaymentOutcomeCode(),
				paymentsReportingTransferDTO.getOrgId(), paymentsReportingTransferDTO.getIuv(), paymentsReportingTransferDTO.getIur(), paymentsReportingTransferDTO.getTransferIndex());
		}
	}
}
