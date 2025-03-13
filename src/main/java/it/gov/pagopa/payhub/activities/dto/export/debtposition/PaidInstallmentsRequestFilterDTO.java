package it.gov.pagopa.payhub.activities.dto.export.debtposition;


import it.gov.pagopa.payhub.activities.dto.OffsetDateTimeIntervalFilter;
import lombok.*;
import lombok.experimental.SuperBuilder;


@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class PaidInstallmentsRequestFilterDTO extends OffsetDateTimeIntervalFilter {

    private Long organizationId;
    private String operatorExternalUserId;
    private Long debtPositionTypeOrgId;
}