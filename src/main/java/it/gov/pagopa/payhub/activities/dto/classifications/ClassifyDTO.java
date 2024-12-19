package it.gov.pagopa.payhub.activities.dto.classifications;

import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;
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
public class ClassifyDTO implements Serializable {
    private Long organizationId;
    private Long transferId;
    private Long paymentNotifyId;
    private Long paymentReportingId;
    private Long treasuryId;
    private String iuf;
    private String iud;
    private String iuv;
    private String iur;
    private int transferIndex;
    private ClassificationsEnum classificationsEnum;
    private LocalDate creationDate;
}
