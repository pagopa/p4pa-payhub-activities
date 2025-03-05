package it.gov.pagopa.payhub.activities.service.debtposition.ionotification;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionTypeOrgService;
import it.gov.pagopa.payhub.activities.connector.ionotification.IONotificationService;
import it.gov.pagopa.payhub.activities.connector.ionotification.mapper.NotificationRequestMapper;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.IONotificationDTO;
import it.gov.pagopa.pu.ionotification.dto.generated.MessageResponseDTO;
import it.gov.pagopa.pu.ionotification.dto.generated.NotificationRequestDTO;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationApiKeys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
@Slf4j
public class IONotificationActivityService {

    private final OrganizationService organizationService;
    private final DebtPositionTypeOrgService debtPositionTypeOrgService;
    private final IONotificationService ioNotificationService;
    private final NotificationRequestMapper notificationRequestMapper;

    public IONotificationActivityService(OrganizationService organizationService, DebtPositionTypeOrgService debtPositionTypeOrgService, IONotificationService ioNotificationService, NotificationRequestMapper notificationRequestMapper) {
        this.organizationService = organizationService;
        this.debtPositionTypeOrgService = debtPositionTypeOrgService;
        this.ioNotificationService = ioNotificationService;
        this.notificationRequestMapper = notificationRequestMapper;
    }

    public MessageResponseDTO sendMessage(DebtPositionDTO debtPositionDTO) {
        String apiKey = organizationService.getOrganizationApiKey(debtPositionDTO.getOrganizationId(), OrganizationApiKeys.KeyTypeEnum.IO);

        IONotificationDTO ioNotificationDTO = debtPositionTypeOrgService
                .getIONotificationDetails(debtPositionDTO.getDebtPositionTypeOrgId(), NotificationRequestDTO.OperationTypeEnum.CREATE_DP);

        MessageResponseDTO messageResponseDTO = null;
        for (NotificationRequestDTO notificationRequestDTO : notificationRequestMapper.map(debtPositionDTO, apiKey, ioNotificationDTO)) {
            messageResponseDTO = ioNotificationService.sendMessage(notificationRequestDTO);
        }
        return messageResponseDTO;
    }
}

