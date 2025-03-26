package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionOrigin;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Lazy
@Service
public class InstallmentClient {
    private final DebtPositionApisHolder debtPositionApisHolder;

    public InstallmentClient(DebtPositionApisHolder debtPositionApisHolder) {
        this.debtPositionApisHolder = debtPositionApisHolder;
    }

    public List<InstallmentDTO> getInstallmentsByOrganizationIdAndNav(String accessToken, Long organizationId, String nav, List<DebtPositionOrigin> debtPositionOrigins) {
        return debtPositionApisHolder.getInstallmentApi(accessToken).getInstallmentsByOrganizationIdAndNav(organizationId, nav, debtPositionOrigins);
    }
}
