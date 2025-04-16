package it.gov.pagopa.payhub.activities.mapper.exportflow.debtposition;

import it.gov.pagopa.payhub.activities.dto.exportflow.debtposition.ReceiptsArchivingExportFlowFileDTO;
import it.gov.pagopa.payhub.activities.enums.EntityIdentifierType;
import it.gov.pagopa.pu.debtposition.dto.generated.PersonDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptArchivingView;
import org.springframework.stereotype.Component;

@Component
public class ReceiptsArchivingExportFlowFileDTOMapper {
    private static final String PAYMENT_OUTCOME= "OK";

    public ReceiptsArchivingExportFlowFileDTO map(ReceiptArchivingView receiptArchivingView){
        PersonDTO debtor = receiptArchivingView.getDebtor();
        PersonDTO payer = receiptArchivingView.getPayer();

        ReceiptsArchivingExportFlowFileDTO.ReceiptsArchivingExportFlowFileDTOBuilder receiptsArchivingExportFlowFileDTOBuilder = ReceiptsArchivingExportFlowFileDTO.builder()
                .receiptXml(null) // TODO: field receiptXml depends on task https://pagopa.atlassian.net/browse/P4ADEV-2306
                .paymentDateTime(receiptArchivingView.getPaymentDateTime())
                .paymentReceiptId(receiptArchivingView.getPaymentReceiptId())
                .remittanceInformation(receiptArchivingView.getRemittanceInformation())
                .orgFiscalCode(receiptArchivingView.getOrgFiscalCode())
                .iuv(receiptArchivingView.getIuv())
                .paymentOutcome(PAYMENT_OUTCOME)
                .creditorReferenceId(receiptArchivingView.getCreditorReferenceId());

        if (debtor != null){
            receiptsArchivingExportFlowFileDTOBuilder
                    .debtorEntityType(debtor.getEntityType() != null ? EntityIdentifierType.valueOf(debtor.getEntityType().getValue()) : null)
                    .debtorFullName(debtor.getFullName())
                    .debtorUniqueIdentifierCode(debtor.getFiscalCode())
                    .debtorEmail(debtor.getEmail());
        }

        if (payer != null) {
            receiptsArchivingExportFlowFileDTOBuilder
                .payerUniqueIdentifierCode(payer.getFiscalCode())
                .payerFullName(payer.getFullName());
        }

        return receiptsArchivingExportFlowFileDTOBuilder.build();
    }
}
