package it.gov.pagopa.payhub.activities.dto.classifications;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PaymentsClassificationDTO implements Serializable {
    private Long organizationId;
    private Long transferId;
    private Long paymentNotifyId;
    private Long paymentReportingId;
    private Long treasuryId;
    private String classificationCode;
    private LocalDate creationDate;
}
