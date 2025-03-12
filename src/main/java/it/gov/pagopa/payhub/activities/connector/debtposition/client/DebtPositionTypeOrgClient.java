package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.pu.debtposition.dto.generated.IONotificationDTO;
import it.gov.pagopa.pu.ionotification.dto.generated.NotificationRequestDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class DebtPositionTypeOrgClient {

    private final DebtPositionApisHolder debtPositionApisHolder;

    public DebtPositionTypeOrgClient(DebtPositionApisHolder debtPositionApisHolder) {
        this.debtPositionApisHolder = debtPositionApisHolder;
    }

    public IONotificationDTO getIONotificationDetails(String accessToken, Long debtPositionTypeOrgId, NotificationRequestDTO.OperationTypeEnum context) {
        return debtPositionApisHolder.getDebtPositionTypeOrgApi(accessToken).getIONotificationDetails(debtPositionTypeOrgId, context.getValue());
    }
}
