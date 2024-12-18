package it.gov.pagopa.payhub.activities.mapper;

import it.gov.pagopa.payhub.activities.dto.debtposition.DebtPositionDTO;
import it.gov.pagopa.payhub.activities.dto.debtposition.InstallmentDTO;
import it.gov.pagopa.pu.p4paionotification.model.generated.NotificationQueueDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Lazy
public class NotificationQueueMapper {

    public NotificationQueueDTO mapDebtPositionDTO2NotificationQueueDTO(DebtPositionDTO debtPosition){
        InstallmentDTO installment = debtPosition.getPaymentOptions().get(0).getInstallments().get(0);

        return NotificationQueueDTO.builder()
                .fiscalCode(installment.getPayer().getUniqueIdentifierCode())
                .enteId(debtPosition.getOrg().getOrgId())
                .tipoDovutoId(debtPosition.getDebtPositionTypeOrg().getDebtPositionTypeOrgId())
                .paymentDate(String.valueOf(installment.getDueDate()))
                .amount(String.valueOf(installment.getAmount()))
                .iuv(installment.getIuv())
                .paymentReason(installment.getRemittanceInformation())
                .build();
    }
}
