package it.gov.pagopa.payhub.activities.service.debtposition.ionotification;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionTypeOrgService;
import it.gov.pagopa.payhub.activities.connector.ionotification.IONotificationFacadeService;
import it.gov.pagopa.payhub.activities.connector.ionotification.mapper.NotificationRequestMapper;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.IONotificationDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.IupdSyncStatusUpdateDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.PaymentOptionDTO;
import it.gov.pagopa.pu.ionotification.dto.generated.MessageResponseDTO;
import it.gov.pagopa.pu.ionotification.dto.generated.NotificationRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static it.gov.pagopa.pu.ionotification.dto.generated.NotificationRequestDTO.OperationTypeEnum.CREATE_DP;

@Lazy
@Service
@Slf4j
public class IONotificationService {

    private final DebtPositionTypeOrgService debtPositionTypeOrgService;
    private final IONotificationFacadeService ioNotificationFacadeService;
    private final NotificationRequestMapper notificationRequestMapper;
    private final InstallmentOperationTypeResolver installmentOperationTypeResolver;

    public IONotificationService(DebtPositionTypeOrgService debtPositionTypeOrgService, IONotificationFacadeService ioNotificationFacadeService, NotificationRequestMapper notificationRequestMapper, InstallmentOperationTypeResolver installmentOperationTypeResolver) {
        this.debtPositionTypeOrgService = debtPositionTypeOrgService;
        this.ioNotificationFacadeService = ioNotificationFacadeService;
        this.notificationRequestMapper = notificationRequestMapper;
        this.installmentOperationTypeResolver = installmentOperationTypeResolver;
    }

    public List<MessageResponseDTO> sendMessage(DebtPositionDTO debtPositionDTO, Map<String, IupdSyncStatusUpdateDTO> iupdSyncStatusUpdateDTOMap) {
        List<MessageResponseDTO> response = new ArrayList<>();

        // Filter installments based on the provided IUD map
        List<PaymentOptionDTO> paymentOptionDTOList = filterInstallmentsByIUD(debtPositionDTO.getPaymentOptions(), iupdSyncStatusUpdateDTOMap);

        if (!paymentOptionDTOList.isEmpty()) {
            // Get only installments that require CREATE_DP notification
            List<PaymentOptionDTO> createDpPaymentOptions = calculateOperationType(paymentOptionDTOList);

            if (!createDpPaymentOptions.isEmpty()) {
                // Retrieve notification details based on the operation type
                IONotificationDTO ioNotificationDTO = debtPositionTypeOrgService
                        .getIONotificationDetails(debtPositionDTO.getDebtPositionTypeOrgId(), CREATE_DP);

                if (ioNotificationDTO != null) {
                    // Map and send IO Notifications with custom markdown
                    response = processNotifications(debtPositionDTO.getOrganizationId(), debtPositionDTO.getDebtPositionTypeOrgId(), createDpPaymentOptions, ioNotificationDTO);
                }
            }
        }

        return response;
    }

    private List<MessageResponseDTO> processNotifications(Long orgId, Long debtPositionTypeOrgId, List<PaymentOptionDTO> createDpPaymentOptions, IONotificationDTO ioNotificationDTO) {
        List<NotificationRequestDTO> notifications = notificationRequestMapper.map(
                createDpPaymentOptions, orgId, debtPositionTypeOrgId, ioNotificationDTO
        );

        return notifications.stream()
                .map(ioNotificationFacadeService::sendMessage)
                .toList();
    }


    /**
     * Determines the operation type based on the filtered payment options.
     */
    private List<PaymentOptionDTO> calculateOperationType(List<PaymentOptionDTO> filteredPaymentOptions) {
        filteredPaymentOptions.forEach(po ->
                po.setInstallments(po.getInstallments().stream()
                        .filter(i -> NotificationRequestDTO.OperationTypeEnum.CREATE_DP
                                .equals(installmentOperationTypeResolver.calculateOperationType(i)))
                        .toList())
        );

        return filteredPaymentOptions.stream()
                .filter(po -> !po.getInstallments().isEmpty())
                .toList();
    }

    /**
     * Filters the installments by keeping only those whose IUD exists in the provided map.
     * Removes PaymentOptionDTO entries that have no valid installments left.
     */
    private List<PaymentOptionDTO> filterInstallmentsByIUD(List<PaymentOptionDTO> paymentOptions, Map<String, IupdSyncStatusUpdateDTO> iupdSyncStatusUpdateDTOMap) {
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

