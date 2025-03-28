package it.gov.pagopa.payhub.activities.dto.paymentnotification;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentNotificationIngestionFlowFileActivityResult {
    private List<String> iudList;
    private Long organizationId;
}
