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
    }
}
