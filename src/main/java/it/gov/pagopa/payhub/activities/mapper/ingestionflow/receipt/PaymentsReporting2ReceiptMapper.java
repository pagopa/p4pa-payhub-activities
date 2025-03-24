package it.gov.pagopa.payhub.activities.mapper.ingestionflow.receipt;

import it.gov.pagopa.payhub.activities.util.Utilities;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.debtposition.dto.generated.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service responsible for Receipt mapping based on Payment Reporting in case of its paymentOutcomeCode is equal to 8 or 9
 */
@Service
public class PaymentsReporting2ReceiptMapper {
	public static final String CREDITOR_REFERENCE_ID_PREFIX = "3";
	public static final String ANONYMOUS_PERSON = "ANONIMO";
	public static final String ALIAS_TEMPLATE = "CODE_%s_" + ReceiptOriginType.PAYMENTS_REPORTING.getValue();
	public static final String CHANNEL = "BATCH";
	/** Taxonomy for general incomes */
	public static final String DEFAULT_TRANSFER_CATEGORY = "9/0801100AP/";

	public ReceiptWithAdditionalNodeDataDTO map2DummyReceipt(PaymentsReporting paymentsReporting, String fiscalCodePA) {
		return new ReceiptWithAdditionalNodeDataDTO()
			.ingestionFlowFileId(paymentsReporting.getIngestionFlowFileId())
			.receiptOrigin(ReceiptOriginType.PAYMENTS_REPORTING)
			.paymentReceiptId(paymentsReporting.getIur())
			.noticeNumber(paymentsReporting.getIuv())
			.orgFiscalCode(paymentsReporting.getReceiverOrganizationCode())
			.outcome(paymentsReporting.getPaymentOutcomeCode())
			.creditorReferenceId(CREDITOR_REFERENCE_ID_PREFIX + paymentsReporting.getIuv())
			.paymentAmountCents(paymentsReporting.getAmountPaidCents())
			.description(paymentsReporting.getIuf())
			.companyName(String.format(ALIAS_TEMPLATE, paymentsReporting.getPaymentOutcomeCode()))
			.idPsp(paymentsReporting.getPspIdentifier())
			.pspFiscalCode(paymentsReporting.getSenderPspCode())
			.pspCompanyName(paymentsReporting.getSenderPspName())
			.idChannel(paymentsReporting.getIuv())
			.channelDescription(CHANNEL)
			.paymentDateTime(Utilities.toOffsetDateTime(paymentsReporting.getPayDate()))
			.applicationDate(Utilities.toOffsetDateTime(paymentsReporting.getAcquiringDate()))
			.transferDate(Utilities.toOffsetDateTime(paymentsReporting.getRegulationDate()))
			.standin(false)
			.debtor(buildAnonymousPerson())
			.payer(buildAnonymousPerson())
			.transfers(List.of(buildDummyTransfer(paymentsReporting, fiscalCodePA)));
	}

	private PersonDTO buildAnonymousPerson() {
		return new PersonDTO()
			.entityType(EntityTypeEnum.F)
			.fiscalCode(ANONYMOUS_PERSON)
			.fullName(ANONYMOUS_PERSON);
	}

	private ReceiptTransferDTO buildDummyTransfer(PaymentsReporting paymentsReporting, String fiscalCodePA) {
		return new ReceiptTransferDTO()
			.idTransfer(1)
			.transferAmountCents(paymentsReporting.getAmountPaidCents())
			.fiscalCodePA(fiscalCodePA)
			.remittanceInformation(String.format(ALIAS_TEMPLATE, paymentsReporting.getPaymentOutcomeCode()))
			.transferCategory(DEFAULT_TRANSFER_CATEGORY);
	}
}
