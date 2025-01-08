package it.gov.pagopa.payhub.activities.connector.ionotification.mapper;

import it.gov.pagopa.payhub.activities.dto.debtposition.DebtPositionDTO;
import it.gov.pagopa.pu.ionotification.dto.generated.NotificationQueueDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Lazy
public class NotificationQueueMapper {

    public List<NotificationQueueDTO> mapDebtPositionDTO2NotificationQueueDTO(DebtPositionDTO debtPosition) {
        return debtPosition.getPaymentOptions().stream()
                .flatMap(p -> p.getInstallments().stream())
                .map(i -> i.getPayer().getUniqueIdentifierCode())
                .distinct()
                .map(cf -> NotificationQueueDTO.builder()
                        .fiscalCode(cf)
                        .enteId(debtPosition.getOrg().getOrgId())
                        .tipoDovutoId(debtPosition.getDebtPositionTypeOrg().getDebtPositionTypeOrgId())
                        .build())
                .toList();
    }
}

