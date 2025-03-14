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
    private final DebtOperationOperationTypeResolver debtOperationOperationTypeResolver;

    public IONotificationService(DebtPositionTypeOrgService debtPositionTypeOrgService, IONotificationFacadeService ioNotificationFacadeService, NotificationRequestMapper notificationRequestMapper, DebtOperationOperationTypeResolver debtOperationOperationTypeResolver) {
        this.debtPositionTypeOrgService = debtPositionTypeOrgService;
        this.ioNotificationFacadeService = ioNotificationFacadeService;
        this.notificationRequestMapper = notificationRequestMapper;
        this.debtOperationOperationTypeResolver = debtOperationOperationTypeResolver;
    }

    public List<MessageResponseDTO> sendMessage(DebtPositionDTO debtPositionDTO, Map<String, IupdSyncStatusUpdateDTO> iupdSyncStatusUpdateDTOMap) {
        List<MessageResponseDTO> response = new ArrayList<>();

        NotificationRequestDTO.OperationTypeEnum operationType = debtOperationOperationTypeResolver.calculateDebtPositionOperationType(debtPositionDTO, iupdSyncStatusUpdateDTOMap);

        if (operationType != null) {
            IONotificationDTO ioNotificationDTO = debtPositionTypeOrgService
                    .getIONotificationDetails(debtPositionDTO.getDebtPositionTypeOrgId(), operationType);

            if (ioNotificationDTO != null && operationType.equals(CREATE_DP)) {
                response = processNotifications(debtPositionDTO, ioNotificationDTO, operationType);
            }
        }

        return response;
    }

    /**
     * Maps and Sends notifications for the given debt position.
     */
    private List<MessageResponseDTO> processNotifications(DebtPositionDTO debtPositionDTO,
                                                          IONotificationDTO ioNotificationDTO,
                                                          NotificationRequestDTO.OperationTypeEnum operationType) {
        List<NotificationRequestDTO> notifications = notificationRequestMapper.map(
                debtPositionDTO, ioNotificationDTO, operationType
        );

        return notifications.stream()
                .map(ioNotificationFacadeService::sendMessage)
                .toList();
    }
}

