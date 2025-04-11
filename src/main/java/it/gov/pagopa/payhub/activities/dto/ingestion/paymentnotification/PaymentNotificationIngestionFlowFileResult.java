package it.gov.pagopa.payhub.activities.dto.ingestion.paymentnotification;

import it.gov.pagopa.payhub.activities.dto.ingestion.IngestionFlowFileResult;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;


@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class PaymentNotificationIngestionFlowFileResult extends IngestionFlowFileResult {
    private List<String> iudList;
    private Long organizationId;
}
