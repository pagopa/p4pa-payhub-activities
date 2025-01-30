package it.gov.pagopa.payhub.activities.dto.paymentsreporting;

import it.gov.pagopa.payhub.activities.dto.classifications.PaymentsReportingTransferDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentsReportingIngestionFlowFileActivityResult {
    private String iuf;
    private Long organizationId;
    private List<PaymentsReportingTransferDTO> transfers;
}
