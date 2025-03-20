package it.gov.pagopa.payhub.activities.service.debtposition.ionotification;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionTypeOrgService;
import it.gov.pagopa.payhub.activities.connector.ionotification.IONotificationFacadeService;
import it.gov.pagopa.payhub.activities.connector.ionotification.mapper.NotificationRequestMapper;
import it.gov.pagopa.payhub.activities.dto.IONotificationMessage;
import it.gov.pagopa.payhub.activities.dto.debtposition.GenericWfExecutionConfig;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionTypeOrg;
import it.gov.pagopa.pu.debtposition.dto.generated.IONotificationDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.IupdSyncStatusUpdateDTO;
import it.gov.pagopa.pu.ionotification.dto.generated.MessageResponseDTO;
import it.gov.pagopa.pu.ionotification.dto.generated.NotificationRequestDTO;
import it.gov.pagopa.pu.workflowhub.dto.generated.PaymentEventType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public List<MessageResponseDTO> sendMessage(DebtPositionDTO debtPositionDTO, Map<String, IupdSyncStatusUpdateDTO> iupdSyncStatusUpdateDTOMap, GenericWfExecutionConfig.IONotificationBaseOpsMessages ioMessages) {
        List<MessageResponseDTO> response = new ArrayList<>();

        PaymentEventType paymentEventType = debtOperationOperationTypeResolver.calculateDebtPositionOperationType(debtPositionDTO, iupdSyncStatusUpdateDTOMap);

        if (paymentEventType != null) {
            IONotificationDTO ioNotificationDTO = resolveProvidedIoMessages(debtPositionDTO, paymentEventType, ioMessages);

            if(ioNotificationDTO==null) {
                ioNotificationDTO = debtPositionTypeOrgService
                        .getDefaultIONotificationDetails(debtPositionDTO.getDebtPositionTypeOrgId(), paymentEventType);
            }

            if (ioNotificationDTO != null) {
                response = processNotifications(debtPositionDTO, ioNotificationDTO);
            }
        }

        return response;
    }

    private IONotificationDTO resolveProvidedIoMessages(DebtPositionDTO debtPositionDTO, PaymentEventType paymentEventType, GenericWfExecutionConfig.IONotificationBaseOpsMessages ioMessages) {
        if(ioMessages == null){
            return null;
        }

        IONotificationMessage template = switch (paymentEventType){
            case DP_CREATED -> ioMessages.getCreated();
            case DP_UPDATED, DPI_ADDED, DPI_UPDATED -> ioMessages.getUpdated();
            case DPI_CANCELLED, DP_CANCELLED -> ioMessages.getDeleted();
            default -> null;
        };

        return transcodeTemplate(debtPositionDTO, template);
    }

    private IONotificationDTO transcodeTemplate(DebtPositionDTO debtPositionDTO, IONotificationMessage template) {
        if(template == null){
            return null;
        }

        DebtPositionTypeOrg debtPositionTypeOrg = debtPositionTypeOrgService.getById(debtPositionDTO.getDebtPositionTypeOrgId());
        if(debtPositionTypeOrg!=null){
            return new IONotificationDTO(debtPositionTypeOrg.getServiceId(), template.getSubject(), template.getMessage());
        } else {
            return null;
        }
    }

    /**
     * Maps and Sends notifications for the given debt position.
     */
    private List<MessageResponseDTO> processNotifications(DebtPositionDTO debtPositionDTO, IONotificationDTO ioNotificationDTO) {
        List<NotificationRequestDTO> notifications = notificationRequestMapper.map(
                debtPositionDTO, ioNotificationDTO
        );

        return notifications.stream()
                .map(ioNotificationFacadeService::sendMessage)
                .toList();
    }
}

