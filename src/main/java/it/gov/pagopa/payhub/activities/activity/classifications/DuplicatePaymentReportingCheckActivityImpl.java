package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.connector.classification.ClassificationService;
import it.gov.pagopa.payhub.activities.connector.classification.PaymentsReportingService;
import it.gov.pagopa.payhub.activities.connector.debtposition.ReceiptService;
import it.gov.pagopa.payhub.activities.dto.classifications.Transfer2ClassifyDTO;
import it.gov.pagopa.payhub.activities.dto.classifications.TransferSemanticKeyDTO;
import it.gov.pagopa.payhub.activities.service.classifications.TransferClassificationStoreService;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.debtpositions.dto.generated.ReceiptNoPII;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Lazy
@Slf4j
@Component
public class DuplicatePaymentReportingCheckActivityImpl implements DuplicatePaymentReportingCheckActivity {

    private final ClassificationService classificationService;
    private final PaymentsReportingService paymentsReportingService;
    private final ReceiptService receiptService;
    private final TransferClassificationStoreService transferClassificationStoreService;

    public DuplicatePaymentReportingCheckActivityImpl(ClassificationService classificationService,
                                                      PaymentsReportingService paymentsReportingService,
                                                      ReceiptService receiptService, TransferClassificationStoreService transferClassificationStoreService) {
        this.classificationService = classificationService;
        this.paymentsReportingService = paymentsReportingService;
        this.receiptService = receiptService;
        this.transferClassificationStoreService = transferClassificationStoreService;
    }

    @Override
    public void duplicatePaymentsCheck(Long organizationId, Transfer2ClassifyDTO transfer2ClassifyDTO) {
        ReceiptNoPII receipt = receiptService.getByPaymentReceiptId(transfer2ClassifyDTO.getIur());

        // Delete Classifications
        classificationService.deleteDuplicates(organizationId, transfer2ClassifyDTO.getIuv(), transfer2ClassifyDTO.getTransferIndex());

        // Find possible duplicates Payments Reporting
        List<PaymentsReporting> paymentsReportingList = paymentsReportingService.findDuplicates(organizationId, transfer2ClassifyDTO.getIuv(), transfer2ClassifyDTO.getTransferIndex(),
                receipt.getOrgFiscalCode());

        // If multiple Payments Reporting (different IURs) are found, create a Classification with label DOPPI for each
        List<String> iurs = paymentsReportingList.stream().map(PaymentsReporting::getIur).distinct().toList();
        if (iurs.size() > 1) {
            paymentsReportingList
                    .forEach(pr -> {
                                TransferSemanticKeyDTO transferSemanticKeyDTO = TransferSemanticKeyDTO.builder()
                                        .orgId(organizationId)
                                        .iuv(pr.getIuv())
                                        .iur(pr.getIur())
                                        .transferIndex(pr.getTransferIndex())
                                        .build();
                                transferClassificationStoreService.saveClassifications(transferSemanticKeyDTO,
                                        null,
                                        null,
                                        pr,
                                        null,
                                        null,
                                        List.of(ClassificationsEnum.DOPPI));
                            }
                    );

        }
    }
}
