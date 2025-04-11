package it.gov.pagopa.payhub.activities.dto.ingestion.paymentnotification;

import it.gov.pagopa.payhub.activities.dto.ingestion.IngestionFlowFileResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;


@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PaymentNotificationIngestionFlowFileResult extends IngestionFlowFileResult {
    private List<String> iudList;
    private Long organizationId;
}
