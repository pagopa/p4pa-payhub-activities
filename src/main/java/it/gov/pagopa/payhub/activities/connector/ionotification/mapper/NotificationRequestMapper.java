package it.gov.pagopa.payhub.activities.connector.ionotification.mapper;

import it.gov.pagopa.pu.debtposition.dto.generated.IONotificationDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.PaymentOptionDTO;
import it.gov.pagopa.pu.ionotification.dto.generated.NotificationRequestDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@Lazy
public class NotificationRequestMapper {

    public List<NotificationRequestDTO> map(List<PaymentOptionDTO> paymentOptions, Long orgId, Long debtPositionTypeOrgId, IONotificationDTO ioNotificationDTO, NotificationRequestDTO.OperationTypeEnum operationType) {
        // If only one PaymentOption exists, map with nav field
        if (paymentOptions.size() == 1) {
            List<InstallmentDTO> installmentDTOList = paymentOptions.getFirst().getInstallments();

            // If at least one installment has a dueDate, select those with the earliest dueDate, otherwise take the first installment
            List<InstallmentDTO> installments = installmentDTOList.stream()
                    .filter(i -> i.getDueDate() != null)
                    .collect(groupingBy(InstallmentDTO::getDueDate))
                    .entrySet().stream()
                    .min(Map.Entry.comparingByKey())
                    .map(Map.Entry::getValue)
                    .orElse(List.of(installmentDTOList.getFirst()));

            return installments.stream()
                    .map(installment -> {
                        NotificationRequestDTO notificationRequestDTO = mapNotificationRequestDTO(
                                orgId, debtPositionTypeOrgId, ioNotificationDTO, installment, operationType);
                        notificationRequestDTO.setNav(installment.getNav());
                        return notificationRequestDTO;
                    })
                    .toList();
        }

        // If more than one PO, iterate on every installment and map only distinct fiscal codes
        return paymentOptions.stream()
                .flatMap(p -> p.getInstallments().stream())
                .collect(Collectors.toMap(
                        i -> i.getDebtor().getFiscalCode(),
                        i -> i,
                        (i1, i2) -> i1
                ))
                .values().stream()
                .map(installmentDTO -> mapNotificationRequestDTO(orgId, debtPositionTypeOrgId, ioNotificationDTO, installmentDTO, operationType))
                .toList();
    }

    private static NotificationRequestDTO mapNotificationRequestDTO(
            Long orgId, Long debtPositionTypeOrgId, IONotificationDTO ioNotificationDTO,
            InstallmentDTO installmentDTO, NotificationRequestDTO.OperationTypeEnum operationType) {

        NotificationRequestDTO notificationRequestDTO = new NotificationRequestDTO();
        notificationRequestDTO.setFiscalCode(installmentDTO.getDebtor().getFiscalCode());
        notificationRequestDTO.setOrgId(orgId);
        notificationRequestDTO.setDebtPositionTypeOrgId(debtPositionTypeOrgId);
        if (ioNotificationDTO.getIoTemplateSubject() != null && ioNotificationDTO.getIoTemplateMessage() != null && ioNotificationDTO.getServiceId() != null) {
            notificationRequestDTO.setSubject(ioNotificationDTO.getIoTemplateSubject());
            notificationRequestDTO.setMarkdown(ioNotificationDTO.getIoTemplateMessage());
            notificationRequestDTO.setServiceId(ioNotificationDTO.getServiceId());
        }
        notificationRequestDTO.setAmount(installmentDTO.getAmountCents());
        notificationRequestDTO.setOperationType(operationType);

        return notificationRequestDTO;
    }
}


