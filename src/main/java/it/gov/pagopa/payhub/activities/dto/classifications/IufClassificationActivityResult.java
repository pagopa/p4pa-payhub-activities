package it.gov.pagopa.payhub.activities.dto.classifications;

import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class IufClassificationActivityResult {
    private List<PaymentsReportingDTO> paymentsReportingDTOS;
    private boolean success;
}
