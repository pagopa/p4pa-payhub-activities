package it.gov.pagopa.payhub.activities.mapper.ingestionflow.receipt;

import it.gov.pagopa.payhub.activities.util.DebtPositionUtilities;
import it.gov.pagopa.payhub.activities.util.Utilities;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.debtposition.dto.generated.*;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service responsible for Receipt mapping based on Payment Reporting in case of its paymentOutcomeCode is equal to 8 or 9
 */
@Service
public class PaymentsReporting2ReceiptMapper {
    public static final String ANONYMOUS_PERSON = "ANONIMO";
    public static final String ALIAS_TEMPLATE = "CODE_%s_" + ReceiptOriginType.PAYMENTS_REPORTING.getValue();
    public static final String CHANNEL = "BATCH";
    /**
     * Taxonomy for general incomes
     */
    public static final String DEFAULT_TRANSFER_CATEGORY = "9/0801100AP/";

    public ReceiptWithAdditionalNodeDataDTO map2Receipt(PaymentsReporting paymentsReporting, Organization organization,
                                                        List<InstallmentDebtorDTO> installmentDebtorDTOS) {

        PersonDTO personDTO = !installmentDebtorDTOS.isEmpty() ? installmentDebtorDTOS.getFirst().getDebtor() : buildAnonymousPerson();

        return new ReceiptWithAdditionalNodeDataDTO()
                .ingestionFlowFileId(paymentsReporting.getIngestionFlowFileId())
                .receiptOrigin(ReceiptOriginType.PAYMENTS_REPORTING)
                .paymentReceiptId(paymentsReporting.getIur())
                .creditorReferenceId(paymentsReporting.getIuv())
                .orgFiscalCode(paymentsReporting.getReceiverOrganizationCode())
                .outcome(paymentsReporting.getPaymentOutcomeCode())
                .noticeNumber(DebtPositionUtilities.iuv2nav(paymentsReporting.getIuv()))
                .paymentAmountCents(paymentsReporting.getAmountPaidCents())
                .description(paymentsReporting.getIuf())
                .companyName(organization.getOrgName())
                .idPsp(paymentsReporting.getPspIdentifier())
                .pspFiscalCode(paymentsReporting.getSenderPspCode())
                .pspCompanyName(paymentsReporting.getSenderPspName())
                .idChannel(paymentsReporting.getIuv())
                .channelDescription(CHANNEL)
                .paymentDateTime(Utilities.toOffsetDateTimeStartOfTheDay(paymentsReporting.getPayDate()))
                .sourceFlowName(organization.getIpaCode() + "_IMPORT-DOVUTO")
                .applicationDate(Utilities.toOffsetDateTimeStartOfTheDay(paymentsReporting.getAcquiringDate()))
                .transferDate(Utilities.toOffsetDateTimeStartOfTheDay(paymentsReporting.getRegulationDate()))
                .standin(false)
                .debtor(personDTO)
                .payer(personDTO)
                .transfers(List.of(buildDummyTransfer(paymentsReporting, organization.getOrgFiscalCode(), organization.getOrgName())));
    }

    private PersonDTO buildAnonymousPerson() {
        return new PersonDTO()
                .entityType(PersonEntityType.F)
                .fiscalCode(ANONYMOUS_PERSON)
                .fullName(ANONYMOUS_PERSON);
    }

    private ReceiptTransferDTO buildDummyTransfer(PaymentsReporting paymentsReporting, String fiscalCodePA, String orgName) {
        return new ReceiptTransferDTO()
                .idTransfer(1)
                .transferAmountCents(paymentsReporting.getAmountPaidCents())
                .fiscalCodePA(fiscalCodePA)
                .remittanceInformation(ALIAS_TEMPLATE.formatted(paymentsReporting.getPaymentOutcomeCode()))
                .transferCategory(DEFAULT_TRANSFER_CATEGORY)
                .companyName(orgName);
    }
}
