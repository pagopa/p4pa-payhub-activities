package it.gov.pagopa.payhub.activities.service.debtposition.ionotification;

import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionTypeOrgService;
import it.gov.pagopa.payhub.activities.dto.IONotificationMessage;
import it.gov.pagopa.payhub.activities.dto.debtposition.syncwfconfig.GenericWfExecutionConfig;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionTypeOrg;
import it.gov.pagopa.pu.debtposition.dto.generated.IONotificationDTO;
import it.gov.pagopa.pu.workflowhub.dto.generated.PaymentEventType;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Lazy
public class IoNotificationBaseOpsMessagesResolverService {

    private final DebtPositionTypeOrgService debtPositionTypeOrgService;

    public IoNotificationBaseOpsMessagesResolverService(DebtPositionTypeOrgService debtPositionTypeOrgService) {
        this.debtPositionTypeOrgService = debtPositionTypeOrgService;
    }

    public IONotificationDTO resolveIoMessages(DebtPositionDTO debtPositionDTO, PaymentEventType paymentEventType, GenericWfExecutionConfig.IONotificationBaseOpsMessages ioMessages) {
        if (ioMessages == null) {
            return null;
        }

        IONotificationMessage template = switch (paymentEventType) {
            case DP_CREATED -> ioMessages.getCreated();
            case DP_UPDATED, DPI_ADDED, DPI_UPDATED -> ioMessages.getUpdated();
            case DPI_CANCELLED, DP_CANCELLED -> ioMessages.getDeleted();
            default -> null;
        };

        return transcodeTemplate(debtPositionDTO, template);
    }

    private IONotificationDTO transcodeTemplate(DebtPositionDTO debtPositionDTO, IONotificationMessage template) {
        if (template == null) {
            return null;
        }

        DebtPositionTypeOrg debtPositionTypeOrg = debtPositionTypeOrgService.getById(debtPositionDTO.getDebtPositionTypeOrgId());
        if (debtPositionTypeOrg != null) {
            return new IONotificationDTO(debtPositionTypeOrg.getServiceId(), template.getSubject(), template.getMessage());
        } else {
            return null;
        }
    }
}
