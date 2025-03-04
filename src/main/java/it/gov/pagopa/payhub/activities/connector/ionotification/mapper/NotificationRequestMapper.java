package it.gov.pagopa.payhub.activities.connector.ionotification.mapper;

import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.ionotification.dto.generated.NotificationRequestDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Lazy
public class NotificationRequestMapper {

    public List<NotificationRequestDTO> mapDebtPositionDTO2NotificationRequestDTO(DebtPositionDTO debtPosition, String serviceId, String subject, String markdown) {
        return debtPosition.getPaymentOptions().stream()
                .flatMap(p -> p.getInstallments().stream())
                .map(i -> i.getDebtor().getFiscalCode())
                .distinct()
                .map(cf -> (NotificationRequestDTO)NotificationRequestDTO.builder()
                        .fiscalCode(cf)
                        .orgId(debtPosition.getOrganizationId())
                        .debtPositionTypeOrgId(debtPosition.getDebtPositionTypeOrgId())
                        .subject(subject)
                        .markdown(markdown)
                        .serviceId(serviceId)
                        .build())
                .toList();
    }
}

