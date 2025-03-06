package it.gov.pagopa.payhub.activities.service.debtposition.ionotification;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionTypeOrgService;
import it.gov.pagopa.payhub.activities.connector.ionotification.IONotificationClientService;
import it.gov.pagopa.payhub.activities.connector.ionotification.mapper.NotificationRequestMapper;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.IONotificationDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.IupdSyncStatusUpdateDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.PaymentOptionDTO;
import it.gov.pagopa.pu.ionotification.dto.generated.MessageResponseDTO;
import it.gov.pagopa.pu.ionotification.dto.generated.NotificationRequestDTO;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationApiKeys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Lazy
@Service
@Slf4j
public class IONotificationService {

    private final OrganizationService organizationService;
    private final DebtPositionTypeOrgService debtPositionTypeOrgService;
    private final IONotificationClientService ioNotificationClientService;
    private final NotificationRequestMapper notificationRequestMapper;
    private final InstallmentOperationTypeResolver installmentOperationTypeResolver;

    public IONotificationService(OrganizationService organizationService, DebtPositionTypeOrgService debtPositionTypeOrgService, IONotificationClientService ioNotificationClientService, NotificationRequestMapper notificationRequestMapper, InstallmentOperationTypeResolver installmentOperationTypeResolver) {
        this.organizationService = organizationService;
        this.debtPositionTypeOrgService = debtPositionTypeOrgService;
        this.ioNotificationClientService = ioNotificationClientService;
        this.notificationRequestMapper = notificationRequestMapper;
        this.installmentOperationTypeResolver = installmentOperationTypeResolver;
    }

    public MessageResponseDTO sendMessage(DebtPositionDTO debtPositionDTO, Map<String, IupdSyncStatusUpdateDTO> iupdSyncStatusUpdateDTOMap) {
        // Retrieve API key for the organization
        String apiKey = organizationService.getOrganizationApiKey(debtPositionDTO.getOrganizationId(), OrganizationApiKeys.KeyTypeEnum.IO);
        MessageResponseDTO messageResponseDTO = null;

        if (apiKey != null) {
            // Filter installments based on the provided IUD map
            List<PaymentOptionDTO> filteredPaymentOptions = filterInstallmentsByIUD(debtPositionDTO.getPaymentOptions(), iupdSyncStatusUpdateDTOMap);

            if (!filteredPaymentOptions.isEmpty()) {
                // Determine the operation type based on the filtered installments
                NotificationRequestDTO.OperationTypeEnum operationType = calculateOperationType(filteredPaymentOptions);

                // Retrieve notification details based on the operation type
                IONotificationDTO ioNotificationDTO = debtPositionTypeOrgService
                        .getIONotificationDetails(debtPositionDTO.getDebtPositionTypeOrgId(), operationType);

                if (ioNotificationDTO != null) {
                    // Map and send IO Notifications
                    for (NotificationRequestDTO notificationRequestDTO : notificationRequestMapper.map(
                            filteredPaymentOptions, debtPositionDTO.getOrganizationId(),
                            debtPositionDTO.getDebtPositionTypeOrgId(), apiKey, ioNotificationDTO)) {
                        messageResponseDTO = ioNotificationClientService.sendMessage(notificationRequestDTO);
                    }
                }
            }
        }
        return messageResponseDTO;
    }

    /**
     * Determines the operation type based on the filtered installments.
     */
    private NotificationRequestDTO.OperationTypeEnum calculateOperationType(List<PaymentOptionDTO> filteredPaymentOptions) {
        return installmentOperationTypeResolver.calculateOperationType(
                filteredPaymentOptions.stream()
                        .flatMap(po -> po.getInstallments().stream())
                        .toList());
    }

    /**
     * Filters the installments by keeping only those whose IUD exists in the provided map.
     * Removes PaymentOptionDTO entries that have no valid installments left.
     */
    private static List<PaymentOptionDTO> filterInstallmentsByIUD(List<PaymentOptionDTO> paymentOptions, Map<String, IupdSyncStatusUpdateDTO> iupdSyncStatusUpdateDTOMap) {
        paymentOptions.forEach(po ->
                po.setInstallments(po.getInstallments().stream()
                        .filter(i -> iupdSyncStatusUpdateDTOMap.containsKey(i.getIud()))
                        .toList())
        );

        return paymentOptions.stream()
                .filter(po -> !po.getInstallments().isEmpty())
                .toList();
    }
}

