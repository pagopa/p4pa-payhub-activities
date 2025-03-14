package it.gov.pagopa.payhub.activities.service.debtposition.ionotification;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionTypeOrgService;
import it.gov.pagopa.payhub.activities.connector.ionotification.IONotificationFacadeService;
import it.gov.pagopa.payhub.activities.connector.ionotification.mapper.NotificationRequestMapper;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.IONotificationDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.IupdSyncStatusUpdateDTO;
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

        DebtPositionDTO filteredDebtPosition = filterInstallmentsByIUD(debtPositionDTO, iupdSyncStatusUpdateDTOMap);

        if (!filteredDebtPosition.getPaymentOptions().isEmpty()) {
            DebtPositionDTO createDpDebtPosition = calculateOperationType(filteredDebtPosition);

            if (!createDpDebtPosition.getPaymentOptions().isEmpty()) {
                IONotificationDTO ioNotificationDTO = debtPositionTypeOrgService
                        .getIONotificationDetails(debtPositionDTO.getDebtPositionTypeOrgId(), CREATE_DP);

                if (ioNotificationDTO != null) {
                    response = processNotifications(createDpDebtPosition, ioNotificationDTO);
                }
            }
        }

        return response;
    }

    /**
     * Maps and Sends notifications for the given debt position.
     */
    private List<MessageResponseDTO> processNotifications(DebtPositionDTO debtPositionDTO, IONotificationDTO ioNotificationDTO) {
        List<NotificationRequestDTO> notifications = notificationRequestMapper.map(
                debtPositionDTO, ioNotificationDTO, CREATE_DP
        );

        return notifications.stream()
                .map(ioNotificationFacadeService::sendMessage)
                .toList();
    }

    /**
     * Filters payment options to keep only installments with the CREATE_DP operation type.
     */
    private DebtPositionDTO calculateOperationType(DebtPositionDTO debtPositionDTO) {
        debtPositionDTO.getPaymentOptions().forEach(po ->
                po.setInstallments(po.getInstallments().stream()
                        .filter(i -> NotificationRequestDTO.OperationTypeEnum.CREATE_DP
                                .equals(installmentOperationTypeResolver.calculateOperationType(i)))
                        .toList())
        );

        debtPositionDTO.setPaymentOptions(
                debtPositionDTO.getPaymentOptions().stream()
                        .filter(po -> !po.getInstallments().isEmpty())
                        .toList()
        );

        return debtPositionDTO;
    }

    /**
     * Filters the installments by keeping only those whose IUD exists in the provided map.
     * Removes PaymentOptionDTO entries that have no valid installments left.
     */
    private DebtPositionDTO filterInstallmentsByIUD(DebtPositionDTO debtPositionDTO, Map<String, IupdSyncStatusUpdateDTO> iupdSyncStatusUpdateDTOMap) {
        debtPositionDTO.getPaymentOptions().forEach(po ->
                po.setInstallments(po.getInstallments().stream()
                        .filter(i -> iupdSyncStatusUpdateDTOMap.containsKey(i.getIud()))
                        .toList())
        );

        debtPositionDTO.setPaymentOptions(
                debtPositionDTO.getPaymentOptions().stream()
                        .filter(po -> !po.getInstallments().isEmpty())
                        .toList()
        );

        return debtPositionDTO;
    }
}

