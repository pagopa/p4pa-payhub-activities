package it.gov.pagopa.payhub.activities.mapper.exportflow.debtposition;

import it.gov.pagopa.payhub.activities.dto.exportflow.debtposition.ReceiptsArchivingExportFlowFileDTO;
import it.gov.pagopa.payhub.activities.service.receipt.RtFileHandlerService;
import it.gov.pagopa.pu.debtposition.dto.generated.PersonDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptArchivingView;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Lazy
public class ReceiptsArchivingExportFlowFileDTOMapper {
    private static final String PAYMENT_OUTCOME= "OK";

    private final RtFileHandlerService rtFileHandlerService;

    public ReceiptsArchivingExportFlowFileDTOMapper(RtFileHandlerService rtFileHandlerService) {
        this.rtFileHandlerService = rtFileHandlerService;
    }

    public ReceiptsArchivingExportFlowFileDTO map(ReceiptArchivingView receiptArchivingView){
        PersonDTO debtor = receiptArchivingView.getDebtor();
        PersonDTO payer = receiptArchivingView.getPayer();

        ReceiptsArchivingExportFlowFileDTO.ReceiptsArchivingExportFlowFileDTOBuilder receiptsArchivingExportFlowFileDTOBuilder = ReceiptsArchivingExportFlowFileDTO.builder()
                .receiptXml(rtFileHandlerService.read(receiptArchivingView.getOrganizationId(), receiptArchivingView.getRtFilePath()))
                .paymentDateTime(receiptArchivingView.getPaymentDateTime() != null
                        ? receiptArchivingView.getPaymentDateTime().toLocalDateTime()
                        : null)
                .paymentReceiptId(receiptArchivingView.getPaymentReceiptId())
                .remittanceInformation(receiptArchivingView.getRemittanceInformation())
                .orgFiscalCode(receiptArchivingView.getOrgFiscalCode())
                .iuv(receiptArchivingView.getIuv())
                .paymentOutcome(PAYMENT_OUTCOME)
                .creditorReferenceId(receiptArchivingView.getCreditorReferenceId());

        if (debtor != null){
            receiptsArchivingExportFlowFileDTOBuilder
                    .debtorEntityType(debtor.getEntityType())
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
