package it.gov.pagopa.payhub.activities.connector.ionotification.mapper;

import it.gov.pagopa.pu.debtposition.dto.generated.IONotificationDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.PaymentOptionDTO;
import it.gov.pagopa.pu.ionotification.dto.generated.NotificationRequestDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static it.gov.pagopa.payhub.activities.util.Utilities.centsAmountToEuroString;
import static java.util.stream.Collectors.groupingBy;

@Service
@Lazy
public class NotificationRequestMapper {

    public static final String placeholderDefault = "Not Specified";

    public List<NotificationRequestDTO> map(List<PaymentOptionDTO> paymentOptions, Long orgId, Long debtPositionTypeOrgId, IONotificationDTO ioNotificationDTO) {
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
                                orgId, debtPositionTypeOrgId, ioNotificationDTO, installment);
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
                .map(installmentDTO -> mapNotificationRequestDTO(orgId, debtPositionTypeOrgId, ioNotificationDTO, installmentDTO))
                .toList();
    }

    private NotificationRequestDTO mapNotificationRequestDTO(Long orgId, Long debtPositionTypeOrgId, IONotificationDTO ioNotificationDTO,
                                                                    InstallmentDTO installmentDTO) {

        NotificationRequestDTO notificationRequestDTO = new NotificationRequestDTO();
        notificationRequestDTO.setFiscalCode(installmentDTO.getDebtor().getFiscalCode());
        notificationRequestDTO.setOrgId(orgId);
        notificationRequestDTO.setDebtPositionTypeOrgId(debtPositionTypeOrgId);
        if (ioNotificationDTO.getIoTemplateSubject() != null && ioNotificationDTO.getIoTemplateMessage() != null && ioNotificationDTO.getServiceId() != null) {
            notificationRequestDTO.setSubject(ioNotificationDTO.getIoTemplateSubject());
            notificationRequestDTO.setMarkdown(replacePlaceholders(ioNotificationDTO.getIoTemplateMessage(), installmentDTO));
            notificationRequestDTO.setServiceId(ioNotificationDTO.getServiceId());
        }
        notificationRequestDTO.setAmount(installmentDTO.getAmountCents());
        notificationRequestDTO.setOperationType(NotificationRequestDTO.OperationTypeEnum.CREATE_DP);

        return notificationRequestDTO;
    }

    private String replacePlaceholders(String markdown, InstallmentDTO installment) {
        Map<String, String> placeholders = Map.of(
                "%importoDovuto%", Objects.toString(centsAmountToEuroString(installment.getAmountCents()), placeholderDefault),
                "%dataEsecuzionePagamento%", Objects.toString(installment.getDueDate(), placeholderDefault),
                "%codIUV%", Objects.toString(installment.getIuv(), placeholderDefault),
                "%causaleVersamento%", Objects.toString(installment.getRemittanceInformation(), placeholderDefault)
        );
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            markdown = markdown.replace(entry.getKey(), entry.getValue() != null ? entry.getValue() : "");
        }
        return markdown.replaceAll("\\s{2,}", " ").trim();
    }
}


