package it.gov.pagopa.payhub.activities.connector.ionotification.mapper;

import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
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

    public List<NotificationRequestDTO> map(DebtPositionDTO debtPosition, String apiKey, IONotificationDTO ioNotificationDTO) {
        List<PaymentOptionDTO> paymentOptions = debtPosition.getPaymentOptions();

        // If only one PaymentOption exists, map with nav field only the first installment
        if (paymentOptions.size() == 1) {
            InstallmentDTO firstInstallment = paymentOptions.getFirst().getInstallments().getFirst();

            return List.of(
                    mapNotificationRequestDTO(debtPosition, apiKey, ioNotificationDTO, firstInstallment)
                            .nav(firstInstallment.getNav())
                            .build()
            );
        }

        // If more than one PO, iterate on every installment and map where fiscal code not distinct
        return paymentOptions.stream()
                .flatMap(p -> p.getInstallments().stream())
                .collect(Collectors.toMap(
                        i -> i.getDebtor().getFiscalCode(),
                        i -> i,
                        (i1, i2) -> i1
                ))
                .values().stream()
                .map(i -> mapNotificationRequestDTO(debtPosition, apiKey, ioNotificationDTO, i).build())
                .collect(Collectors.toList());
    }

    private static NotificationRequestDTO.NotificationRequestDTOBuilder<?, ?> mapNotificationRequestDTO(
            DebtPositionDTO debtPosition, String apiKey, IONotificationDTO ioNotificationDTO, InstallmentDTO installmentDTO) {

        return NotificationRequestDTO.builder()
                .fiscalCode(installmentDTO.getDebtor().getFiscalCode())
                .orgId(debtPosition.getOrganizationId())
                .debtPositionTypeOrgId(debtPosition.getDebtPositionTypeOrgId())
                .apiKey(apiKey)
                .subject(ioNotificationDTO.getIoTemplateSubject())
                .markdown(ioNotificationDTO.getIoTemplateMessage())
                .serviceId(ioNotificationDTO.getServiceId())
                .amount(installmentDTO.getAmountCents())
                .dueDate(String.valueOf(installmentDTO.getDueDate()));
    }
}


