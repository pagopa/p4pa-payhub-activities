package it.gov.pagopa.payhub.activities.activity.ingestionflow.paymentsreporting;

import it.gov.pagopa.payhub.activities.connector.classification.PaymentsReportingService;
import it.gov.pagopa.payhub.activities.dto.classifications.PaymentsReportingTransferDTO;
import it.gov.pagopa.payhub.activities.service.ingestionflow.paymentsreporting.PaymentsReportingMapperService;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Lazy
@Component
public class HandlePaymentsReportingDeletionActivityImpl implements HandlePaymentsReportingDeletionActivity {

    private final PaymentsReportingService paymentsReportingService;
    private final PaymentsReportingMapperService paymentsReportingMapperService;

    public HandlePaymentsReportingDeletionActivityImpl(PaymentsReportingService paymentsReportingService, PaymentsReportingMapperService paymentsReportingMapperService) {
        this.paymentsReportingService = paymentsReportingService;
        this.paymentsReportingMapperService = paymentsReportingMapperService;
    }

    @Override
    public List<PaymentsReportingTransferDTO> handlePaymentsReportingDeletion(Long organizationId, String iuf, Long ingestionFlowFileId) {
        List<PaymentsReporting> paymentsReportingDeleted = paymentsReportingService.findAndDeleteByOrgIdAndIufAndIngestionFlowFileIdNot(organizationId, iuf, ingestionFlowFileId);

        return paymentsReportingDeleted.stream().map(paymentsReportingMapperService::map).toList();
    }
}
