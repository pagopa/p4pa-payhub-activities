package it.gov.pagopa.payhub.activities.connector.ionotification.mapper;

import it.gov.pagopa.payhub.activities.service.debtposition.ionotification.IoNotificationPlaceholderResolverService;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
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

    private final IoNotificationPlaceholderResolverService ioNotificationPlaceholderResolverService;

    public NotificationRequestMapper(IoNotificationPlaceholderResolverService ioNotificationPlaceholderResolverService) {
        this.ioNotificationPlaceholderResolverService = ioNotificationPlaceholderResolverService;
    }

    public List<NotificationRequestDTO> map(DebtPositionDTO debtPositionDTO, IONotificationDTO ioNotificationDTO) {
        // If only one PaymentOption exists, map with nav field
        List<PaymentOptionDTO> paymentOptions = debtPositionDTO.getPaymentOptions();
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
                                debtPositionDTO, ioNotificationDTO, installment);
                        notificationRequestDTO.setNav(installment.getNav());
                        notificationRequestDTO.setOrgFiscalCode(installment.getTransfers().getFirst().getOrgFiscalCode());
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
                .map(installmentDTO -> mapNotificationRequestDTO(debtPositionDTO, ioNotificationDTO, installmentDTO))
                .toList();
    }

    private NotificationRequestDTO mapNotificationRequestDTO(DebtPositionDTO debtPositionDTO, IONotificationDTO ioNotificationDTO, InstallmentDTO installmentDTO) {

        NotificationRequestDTO notificationRequestDTO = new NotificationRequestDTO();
        notificationRequestDTO.setFiscalCode(installmentDTO.getDebtor().getFiscalCode());
        notificationRequestDTO.setOrgId(debtPositionDTO.getOrganizationId());
        notificationRequestDTO.setDebtPositionTypeOrgId(debtPositionDTO.getDebtPositionTypeOrgId());
        if (ioNotificationDTO.getIoTemplateSubject() != null && ioNotificationDTO.getIoTemplateMessage() != null && ioNotificationDTO.getServiceId() != null) {
            notificationRequestDTO.setSubject(ioNotificationPlaceholderResolverService.applyDefaultPlaceholder(ioNotificationDTO.getIoTemplateSubject(), debtPositionDTO, installmentDTO));
            notificationRequestDTO.setMarkdown(ioNotificationPlaceholderResolverService.applyDefaultPlaceholder(ioNotificationDTO.getIoTemplateMessage(), debtPositionDTO, installmentDTO));
            notificationRequestDTO.setServiceId(ioNotificationDTO.getServiceId());
        }
        notificationRequestDTO.setAmount(installmentDTO.getAmountCents());

        return notificationRequestDTO;
    }
}


