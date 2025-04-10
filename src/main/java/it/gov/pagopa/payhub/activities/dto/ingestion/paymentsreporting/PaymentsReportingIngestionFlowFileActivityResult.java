package it.gov.pagopa.payhub.activities.dto.ingestion.paymentsreporting;

import it.gov.pagopa.payhub.activities.dto.classifications.PaymentsReportingTransferDTO;
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
public class PaymentsReportingIngestionFlowFileActivityResult extends IngestionFlowFileResult {
    private String iuf;
    private Long organizationId;
    private List<PaymentsReportingTransferDTO> transfers;
}
