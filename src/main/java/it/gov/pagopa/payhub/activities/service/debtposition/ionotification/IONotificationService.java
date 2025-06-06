package it.gov.pagopa.payhub.activities.service.debtposition.ionotification;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionTypeOrgService;
import it.gov.pagopa.payhub.activities.connector.ionotification.IONotificationFacadeService;
import it.gov.pagopa.payhub.activities.connector.ionotification.mapper.NotificationRequestMapper;
import it.gov.pagopa.payhub.activities.dto.debtposition.DebtPositionIoNotificationDTO;
import it.gov.pagopa.payhub.activities.dto.debtposition.syncwfconfig.GenericWfExecutionConfig;
import it.gov.pagopa.payhub.activities.service.debtposition.DebtPositionOperationTypeResolver;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.IONotificationDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.SyncCompleteDTO;
import it.gov.pagopa.pu.ionotification.dto.generated.MessageResponseDTO;
import it.gov.pagopa.pu.ionotification.dto.generated.NotificationRequestDTO;
import it.gov.pagopa.pu.workflowhub.dto.generated.PaymentEventType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Lazy
@Service
@Slf4j
public class IONotificationService {

    private final DebtPositionTypeOrgService debtPositionTypeOrgService;
    private final IoNotificationBaseOpsMessagesResolverService baseOpsMessagesResolverService;
    private final IONotificationFacadeService ioNotificationFacadeService;
    private final NotificationRequestMapper notificationRequestMapper;
    private final DebtPositionOperationTypeResolver debtPositionOperationTypeResolver;

    public IONotificationService(DebtPositionTypeOrgService debtPositionTypeOrgService, IoNotificationBaseOpsMessagesResolverService baseOpsMessagesResolverService, IONotificationFacadeService ioNotificationFacadeService, NotificationRequestMapper notificationRequestMapper, DebtPositionOperationTypeResolver debtPositionOperationTypeResolver) {
        this.debtPositionTypeOrgService = debtPositionTypeOrgService;
        this.baseOpsMessagesResolverService = baseOpsMessagesResolverService;
        this.ioNotificationFacadeService = ioNotificationFacadeService;
        this.notificationRequestMapper = notificationRequestMapper;
        this.debtPositionOperationTypeResolver = debtPositionOperationTypeResolver;
    }

    public DebtPositionIoNotificationDTO sendMessage(DebtPositionDTO debtPositionDTO, Map<String, SyncCompleteDTO> iupdSyncStatusUpdateDTOMap, GenericWfExecutionConfig.IONotificationBaseOpsMessages ioMessages) {
        PaymentEventType paymentEventType = debtPositionOperationTypeResolver.calculateDebtPositionOperationType(debtPositionDTO, iupdSyncStatusUpdateDTOMap);

        if (paymentEventType != null) {
            IONotificationDTO ioNotificationDTO = baseOpsMessagesResolverService.resolveIoMessages(debtPositionDTO, paymentEventType, ioMessages);

            if (ioNotificationDTO == null) {
                ioNotificationDTO = debtPositionTypeOrgService
                        .getDefaultIONotificationDetails(debtPositionDTO.getDebtPositionTypeOrgId(), paymentEventType);
            }

            if (ioNotificationDTO != null) {
                return processNotifications(debtPositionDTO, ioNotificationDTO);
            }
        }

        return null;
    }

    /**
     * Maps and Sends notifications for the given debt position.
     */
    private DebtPositionIoNotificationDTO processNotifications(DebtPositionDTO debtPositionDTO, IONotificationDTO ioNotificationDTO) {
        List<NotificationRequestDTO> notifications = notificationRequestMapper.map(
                debtPositionDTO, ioNotificationDTO
        );

        if (notifications.isEmpty()) {
            return null;
        }

        DebtPositionIoNotificationDTO out = DebtPositionIoNotificationDTO.builder()
                .debtPositionId(debtPositionDTO.getDebtPositionId())
                .organizationId(debtPositionDTO.getOrganizationId())
                .debtPositionTypeOrgId(debtPositionDTO.getDebtPositionTypeOrgId())
                .build();

        out.setMessages(notifications.stream()
                .map(r -> {
                    MessageResponseDTO messageResponseDTO = ioNotificationFacadeService.sendMessage(r);
                    if (messageResponseDTO != null) {
                        return (DebtPositionIoNotificationDTO.IoMessage) DebtPositionIoNotificationDTO.IoMessage.builder()
                                .notificationId(messageResponseDTO.getNotificationId())
                                .nav(r.getNav())
                                .serviceId(r.getServiceId())
                                .build();
                    } else {
                        log.info("No notification has been send from io-notification application for NAV {} (serviceId: {}) of organization {} and debtPositionTypeOrgId {}",
                                r.getNav(), r.getServiceId(), r.getOrgId(), r.getDebtPositionTypeOrgId());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList()
        );

        if(out.getMessages().isEmpty()){
            return null;
        }

        return out;
    }
}

