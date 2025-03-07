package it.gov.pagopa.payhub.activities.connector.ionotification.mapper;

import it.gov.pagopa.pu.debtposition.dto.generated.IONotificationDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.PaymentOptionDTO;
import it.gov.pagopa.pu.ionotification.dto.generated.NotificationRequestDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Lazy
public class NotificationRequestMapper {

    public List<NotificationRequestDTO> map(List<PaymentOptionDTO> paymentOptions, Long orgId, Long debtPositionTypeOrgId, String apiKey, IONotificationDTO ioNotificationDTO, NotificationRequestDTO.OperationTypeEnum operationType) {
        // If only one PaymentOption exists, map with nav field the first installment
        if (paymentOptions.size() == 1) {
            InstallmentDTO firstInstallment = paymentOptions.getFirst().getInstallments().getFirst();

            NotificationRequestDTO notificationRequestDTO = mapNotificationRequestDTO(
                    orgId, debtPositionTypeOrgId, apiKey, ioNotificationDTO, firstInstallment, operationType
            );

            notificationRequestDTO.setNav(firstInstallment.getNav());

            return List.of(notificationRequestDTO);
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
                .map(installmentDTO -> mapNotificationRequestDTO(orgId, debtPositionTypeOrgId, apiKey, ioNotificationDTO, installmentDTO, operationType))
                .toList();
    }

    private static NotificationRequestDTO mapNotificationRequestDTO(
            Long orgId, Long debtPositionTypeOrgId, String apiKey, IONotificationDTO ioNotificationDTO,
            InstallmentDTO installmentDTO, NotificationRequestDTO.OperationTypeEnum operationType) {

        NotificationRequestDTO notificationRequestDTO = new NotificationRequestDTO();
        notificationRequestDTO.setFiscalCode(installmentDTO.getDebtor().getFiscalCode());
        notificationRequestDTO.setOrgId(orgId);
        notificationRequestDTO.setDebtPositionTypeOrgId(debtPositionTypeOrgId);
        notificationRequestDTO.setApiKey(apiKey);
        notificationRequestDTO.setIuv(installmentDTO.getIuv());
        if (ioNotificationDTO.getIoTemplateSubject() != null && ioNotificationDTO.getIoTemplateMessage() != null && ioNotificationDTO.getServiceId() != null) {
            notificationRequestDTO.setSubject(ioNotificationDTO.getIoTemplateSubject());
            notificationRequestDTO.setMarkdown(ioNotificationDTO.getIoTemplateMessage());
            notificationRequestDTO.setServiceId(ioNotificationDTO.getServiceId());
        }
        notificationRequestDTO.setAmount(installmentDTO.getAmountCents());
        notificationRequestDTO.setOperationType(operationType);
        notificationRequestDTO.setDueDate(String.valueOf(installmentDTO.getDueDate()));
        notificationRequestDTO.setPaymentReason(installmentDTO.getRemittanceInformation());

        return notificationRequestDTO;
    }
}


