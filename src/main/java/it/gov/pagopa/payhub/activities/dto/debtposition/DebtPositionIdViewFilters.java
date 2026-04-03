package it.gov.pagopa.payhub.activities.dto.debtposition;

import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DebtPositionIdViewFilters {
    private Long organizationId;
    private String iban;
    private Boolean syncError;
    private List<InstallmentStatus> installmentStatuses;
    private String postalIban;
    private Long dptoId;
}
