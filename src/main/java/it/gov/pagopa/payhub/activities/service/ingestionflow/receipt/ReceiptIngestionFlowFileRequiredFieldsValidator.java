package it.gov.pagopa.payhub.activities.service.ingestionflow.receipt;

import it.gov.pagopa.payhub.activities.dto.ingestion.receipt.ReceiptIngestionFlowFileDTO;
import org.apache.commons.lang3.StringUtils;

public class ReceiptIngestionFlowFileRequiredFieldsValidator {

    private ReceiptIngestionFlowFileRequiredFieldsValidator() {
    }

    public static void setDefaultValues(ReceiptIngestionFlowFileDTO dto) {
        if (StringUtils.isBlank(dto.getRemittanceInformation())) {
            dto.setRemittanceInformation("Causale Default iuv: " + dto.getCreditorReferenceId());
        }

        if (StringUtils.isBlank(dto.getFiscalCodePA())) {
            dto.setFiscalCodePA(dto.getOrgFiscalCode());
        }

        if (dto.getIdTransfer() == null) {
            dto.setIdTransfer(1);
        }

        if (dto.getSinglePaymentAmount() == null) {
            dto.setSinglePaymentAmount(dto.getPaymentAmountCents());
        }

        if (StringUtils.isBlank(dto.getTransferCategory())) {
            dto.setTransferCategory("UNKNOWN");
        }
    }
}
