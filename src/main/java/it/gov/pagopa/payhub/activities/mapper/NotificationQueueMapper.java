package it.gov.pagopa.payhub.activities.mapper;

import it.gov.pagopa.payhub.activities.dto.debtposition.DebtPositionDTO;
import it.gov.pagopa.payhub.activities.dto.debtposition.InstallmentDTO;
import it.gov.pagopa.payhub.activities.dto.debtposition.PaymentOptionDTO;
import it.gov.pagopa.pu.p4paionotification.model.generated.NotificationQueueDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Lazy
public class NotificationQueueMapper {

    public List<NotificationQueueDTO> mapDebtPositionDTO2NotificationQueueDTO(DebtPositionDTO debtPosition) {
        List<NotificationQueueDTO> notificationQueueList = new ArrayList<>();

        for (PaymentOptionDTO paymentOption : debtPosition.getPaymentOptions()) {
            if (paymentOption.isMultiDebtor()) {
                Set<String> uniqueFiscalCode = paymentOption.getInstallments().stream()
                        .map(installment -> installment.getPayer().getUniqueIdentifierCode())
                        .collect(Collectors.toSet());

                for (String fiscalCode : uniqueFiscalCode) {
                    notificationQueueList.add(
                            NotificationQueueDTO.builder()
                                    .fiscalCode(fiscalCode)
                                    .enteId(debtPosition.getOrg().getOrgId())
                                    .tipoDovutoId(debtPosition.getDebtPositionTypeOrg().getDebtPositionTypeOrgId())
                                    .build()
                    );
                }
            } else {
                InstallmentDTO firstInstallment = paymentOption.getInstallments().get(0);
                notificationQueueList.add(
                        NotificationQueueDTO.builder()
                                .fiscalCode(firstInstallment.getPayer().getUniqueIdentifierCode())
                                .enteId(debtPosition.getOrg().getOrgId())
                                .tipoDovutoId(debtPosition.getDebtPositionTypeOrg().getDebtPositionTypeOrgId())
                                .build()
                );
            }
        }

        return notificationQueueList;
    }
}

