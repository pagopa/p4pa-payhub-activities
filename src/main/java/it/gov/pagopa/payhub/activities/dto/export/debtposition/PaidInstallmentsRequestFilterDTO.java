package it.gov.pagopa.payhub.activities.dto.export.debtposition;


import it.gov.pagopa.payhub.activities.dto.OffsetDateTimeIntervalFilter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaidInstallmentsRequestFilterDTO {

    private Long organizationId;
    private String operatorExternalUserId;
    private Long debtPositionTypeOrgId;
    private OffsetDateTimeIntervalFilter paymentDate;
}