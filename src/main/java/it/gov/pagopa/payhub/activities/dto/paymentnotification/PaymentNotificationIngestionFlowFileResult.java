package it.gov.pagopa.payhub.activities.dto.paymentnotification;

import it.gov.pagopa.pu.classification.dto.generated.PaymentNotificationDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentNotificationIngestionFlowFileResult {

    private Long totalRows;

    private Long processedRows;

    private String errorDescription;

    private String discardedFileName;

    private List<PaymentNotificationDTO> paymentNotificationList;
}
