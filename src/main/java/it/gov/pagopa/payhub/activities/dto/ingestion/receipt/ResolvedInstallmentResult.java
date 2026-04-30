package it.gov.pagopa.payhub.activities.dto.ingestion.receipt;

import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionTypeOrg;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResolvedInstallmentResult {

    private InstallmentDTO citizenNotifiableInstallment;
    private List<NotifiableInstallment> silNotifiableInstallments;
    private Organization organization;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NotifiableInstallment {
        private DebtPositionTypeOrg debtPositionTypeOrg;
        private InstallmentDTO installment;
    }

    public static ResolvedInstallmentResult empty() {
        return new ResolvedInstallmentResult(null, Collections.emptyList(), null);
    }

    public boolean isEmpty() {
        return organization == null;
    }
}