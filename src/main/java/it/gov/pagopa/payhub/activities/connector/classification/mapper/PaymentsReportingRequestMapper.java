package it.gov.pagopa.payhub.activities.connector.classification.mapper;

import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReportingRequestBody;

public class PaymentsReportingRequestMapper {

    private PaymentsReportingRequestMapper() {
    }

    public static PaymentsReportingRequestBody map(PaymentsReporting paymentsReporting) {
        if (paymentsReporting == null) {
            return null;
        }

        return PaymentsReportingRequestBody.builder()
                .paymentsReportingId(paymentsReporting.getPaymentsReportingId())
                .ingestionFlowFileId(paymentsReporting.getIngestionFlowFileId())
                .organizationId(paymentsReporting.getOrganizationId())
                .iuv(paymentsReporting.getIuv())
                .iur(paymentsReporting.getIur())
                .transferIndex(paymentsReporting.getTransferIndex())
                .pspIdentifier(paymentsReporting.getPspIdentifier())
                .iuf(paymentsReporting.getIuf())
                .flowDateTime(paymentsReporting.getFlowDateTime())
                .regulationUniqueIdentifier(paymentsReporting.getRegulationUniqueIdentifier())
                .regulationDate(paymentsReporting.getRegulationDate())
                .senderPspType(paymentsReporting.getSenderPspType())
                .senderPspCode(paymentsReporting.getSenderPspCode())
                .senderPspName(paymentsReporting.getSenderPspName())
                .receiverOrganizationType(paymentsReporting.getReceiverOrganizationType())
                .receiverOrganizationCode(paymentsReporting.getReceiverOrganizationCode())
                .receiverOrganizationName(paymentsReporting.getReceiverOrganizationName())
                .totalPayments(paymentsReporting.getTotalPayments())
                .totalAmountCents(paymentsReporting.getTotalAmountCents())
                .amountPaidCents(paymentsReporting.getAmountPaidCents())
                .paymentOutcomeCode(paymentsReporting.getPaymentOutcomeCode())
                .payDate(paymentsReporting.getPayDate())
                .acquiringDate(paymentsReporting.getAcquiringDate())
                .bicCodePouringBank(paymentsReporting.getBicCodePouringBank())
                .build();
    }


}
