package it.gov.pagopa.payhub.activities.dto.ingestion.paymentsreporting;

import it.gov.pagopa.payhub.activities.dto.classifications.PaymentsReportingTransferDTO;
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
public class PaymentsReportingIngestionFlowFileActivityResult extends IngestionFlowFileResult {
    private String iuf;
    private List<PaymentsReportingTransferDTO> transfers;
}
