package it.gov.pagopa.payhub.activities.connector.ionotification.mapper;

import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.IONotificationDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.PaymentOptionDTO;
import it.gov.pagopa.pu.ionotification.dto.generated.NotificationRequestDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static it.gov.pagopa.payhub.activities.util.Utilities.centsAmountToEuroString;
import static java.util.stream.Collectors.groupingBy;

@Service
@Lazy
public class NotificationRequestMapper {

    public List<NotificationRequestDTO> map(DebtPositionDTO debtPositionDTO, IONotificationDTO ioNotificationDTO, NotificationRequestDTO.OperationTypeEnum operationType) {
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
                                debtPositionDTO, ioNotificationDTO, installment, operationType);
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
                .map(installmentDTO -> mapNotificationRequestDTO(debtPositionDTO, ioNotificationDTO, installmentDTO, operationType))
                .toList();
    }

    private NotificationRequestDTO mapNotificationRequestDTO(DebtPositionDTO debtPositionDTO, IONotificationDTO ioNotificationDTO,
                                                                    InstallmentDTO installmentDTO, NotificationRequestDTO.OperationTypeEnum operationType) {

        NotificationRequestDTO notificationRequestDTO = new NotificationRequestDTO();
        notificationRequestDTO.setFiscalCode(installmentDTO.getDebtor().getFiscalCode());
        notificationRequestDTO.setOrgId(debtPositionDTO.getOrganizationId());
        notificationRequestDTO.setDebtPositionTypeOrgId(debtPositionDTO.getDebtPositionTypeOrgId());
        if (ioNotificationDTO.getIoTemplateSubject() != null && ioNotificationDTO.getIoTemplateMessage() != null && ioNotificationDTO.getServiceId() != null) {
            notificationRequestDTO.setSubject(ioNotificationDTO.getIoTemplateSubject());
            notificationRequestDTO.setMarkdown(replacePlaceholders(ioNotificationDTO.getIoTemplateMessage(), installmentDTO, debtPositionDTO.getDescription()));
            notificationRequestDTO.setServiceId(ioNotificationDTO.getServiceId());
        }
        notificationRequestDTO.setAmount(installmentDTO.getAmountCents());
        notificationRequestDTO.setOperationType(operationType);

        return notificationRequestDTO;
    }

    private String replacePlaceholders(String markdown, InstallmentDTO installment, String dpDescription) {
        Map<String, String> placeholders = new HashMap<>();

        placeholders.put("%posizioneDebitoria_descrizione%", Objects.toString(dpDescription, ""));
        placeholders.put("%debitore_nomeCompleto%", Objects.toString(installment.getDebtor().getFullName(), ""));
        placeholders.put("%debitore_codiceFiscale%", Objects.toString(installment.getDebtor().getFiscalCode(), ""));
        placeholders.put("%importoTotale%", Objects.toString(centsAmountToEuroString(installment.getAmountCents()), ""));
        placeholders.put("%IUV%", Objects.toString(installment.getIuv(), ""));
        placeholders.put("%NAV%", Objects.toString(installment.getNav(), ""));
        placeholders.put("%causale%", Objects.toString(installment.getRemittanceInformation(), ""));

        String formattedDueDate = installment.getDueDate() != null ? installment.getDueDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
        placeholders.put("%dataScadenza%", formattedDueDate);

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            markdown = markdown.replace(entry.getKey(), entry.getValue());
        }

        return markdown.replaceAll("\\s{2,}", " ").trim();
    }

}


