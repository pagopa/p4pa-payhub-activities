package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.debtposition.client.DebtPositionClient;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.IupdSyncStatusUpdateDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Lazy
@Service
public class DebtPositionServiceImpl implements DebtPositionService {

    private final AuthnService authnService;
    private final DebtPositionClient debtPositionClient;

    public DebtPositionServiceImpl(AuthnService authnService, DebtPositionClient debtPositionClient) {
        this.authnService = authnService;
        this.debtPositionClient = debtPositionClient;
    }

    @Override
    public DebtPositionDTO finalizeSyncStatus(Long debtPositionId, Map<String, IupdSyncStatusUpdateDTO> syncStatusUpdateDTO) {
        String accessToken = authnService.getAccessToken();
        return debtPositionClient.finalizeSyncStatus(accessToken, debtPositionId, syncStatusUpdateDTO);
    }

    @Override
    public OffsetDateTime checkAndUpdateInstallmentExpiration(Long debtPositionId) {
        String accessToken = authnService.getAccessToken();
        DebtPositionDTO debtPositionDTO = debtPositionClient.checkAndUpdateInstallmentExpiration(accessToken, debtPositionId);

        Optional<OffsetDateTime> dueDate = debtPositionDTO.getPaymentOptions().stream()
                .flatMap(paymentOption -> paymentOption.getInstallments().stream())
                .filter(installment -> InstallmentDTO.StatusEnum.UNPAID.equals(installment.getStatus()))
                .map(InstallmentDTO::getDueDate)
                .filter(Objects::nonNull)
                .min(OffsetDateTime::compareTo);

        return dueDate.orElse(null);
    }
}
