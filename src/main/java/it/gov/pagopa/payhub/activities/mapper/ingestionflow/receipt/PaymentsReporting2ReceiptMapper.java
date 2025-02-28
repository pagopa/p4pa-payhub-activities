package it.gov.pagopa.payhub.activities.mapper.ingestionflow.receipt;

import it.gov.pagopa.payhub.activities.util.Utilities;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.debtposition.dto.generated.PersonDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptTransferDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptWithAdditionalNodeDataDTO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service responsible for Receipt mapping based on Payment Reporting in case of its paymentOutcomeCode is equal to 8 or 9
 */
@Service
public class PaymentsReporting2ReceiptMapper {
	public static final String CREDITOR_REFERENCE_ID_PREFIX = "3";
	public static final String ANONYMOUS_PERSON = "ANONIMO";
	public static final String FAKE_COMPANY_NAME = "PaymentsReportingCode9";
	public static final String CHANNEL = "BATCH";

	public ReceiptWithAdditionalNodeDataDTO map2DummyReceipt(PaymentsReporting paymentsReporting, String fiscalCodePA) {
		return new ReceiptWithAdditionalNodeDataDTO()
			.ingestionFlowFileId(paymentsReporting.getIngestionFlowFileId())
			.receiptOrigin(ReceiptWithAdditionalNodeDataDTO.ReceiptOriginEnum.PAYMENTS_REPORTING)
			.paymentReceiptId(paymentsReporting.getIur())
			.noticeNumber(paymentsReporting.getIuv())
			.orgFiscalCode(paymentsReporting.getReceiverOrganizationCode())
			.outcome(paymentsReporting.getPaymentOutcomeCode())
			.creditorReferenceId(CREDITOR_REFERENCE_ID_PREFIX + paymentsReporting.getIuv())
			.paymentAmountCents(paymentsReporting.getAmountPaidCents())
			.description(paymentsReporting.getIuf())
			.companyName(FAKE_COMPANY_NAME)
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
			.entityType(PersonDTO.EntityTypeEnum.F)
			.fiscalCode(ANONYMOUS_PERSON)
			.fullName(ANONYMOUS_PERSON)
			//.email() it's mandatory but it shouldn't
			;
	}

	private ReceiptTransferDTO buildDummyTransfer(PaymentsReporting paymentsReporting, String fiscalCodePA) {
		return new ReceiptTransferDTO()
			.idTransfer(1)
			.transferAmountCents(paymentsReporting.getAmountPaidCents())
			.fiscalCodePA(fiscalCodePA)
			.remittanceInformation("code_" + paymentsReporting.getPaymentOutcomeCode())
			.transferCategory("9/---");
	}
}
