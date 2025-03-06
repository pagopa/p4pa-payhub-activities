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

            return List.of(mapNotificationRequestDTO(orgId, debtPositionTypeOrgId, apiKey, ioNotificationDTO, firstInstallment, operationType)
                            .nav(firstInstallment.getNav())
                            .build()
            );
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
                .map(installmentDTO -> mapNotificationRequestDTO(orgId, debtPositionTypeOrgId,  apiKey, ioNotificationDTO, installmentDTO, operationType).build())
                .collect(Collectors.toList());
    }

    private static NotificationRequestDTO.NotificationRequestDTOBuilder<?, ?> mapNotificationRequestDTO(
            Long orgId, Long debtPositionTypeOrgId, String apiKey, IONotificationDTO ioNotificationDTO, InstallmentDTO installmentDTO, NotificationRequestDTO.OperationTypeEnum operationType) {

        return NotificationRequestDTO.builder()
                .fiscalCode(installmentDTO.getDebtor().getFiscalCode())
                .orgId(orgId)
                .debtPositionTypeOrgId(debtPositionTypeOrgId)
                .apiKey(apiKey)
                .iuv(installmentDTO.getIuv())
                .subject(ioNotificationDTO.getIoTemplateSubject())
                .markdown(ioNotificationDTO.getIoTemplateMessage())
                .serviceId(ioNotificationDTO.getServiceId())
                .amount(installmentDTO.getAmountCents())
                .operationType(operationType)
                .dueDate(String.valueOf(installmentDTO.getDueDate()))
                .paymentReason(installmentDTO.getRemittanceInformation());
    }
}


