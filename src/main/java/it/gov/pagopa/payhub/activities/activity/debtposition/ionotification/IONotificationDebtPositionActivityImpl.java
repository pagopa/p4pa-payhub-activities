package it.gov.pagopa.payhub.activities.activity.debtposition.ionotification;

import it.gov.pagopa.payhub.activities.dto.debtposition.GenericWfExecutionConfig;
import it.gov.pagopa.payhub.activities.service.debtposition.ionotification.IONotificationService;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.IupdSyncStatusUpdateDTO;
import it.gov.pagopa.pu.ionotification.dto.generated.MessageResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Lazy
@Service
@Slf4j
public class IONotificationDebtPositionActivityImpl implements IONotificationDebtPositionActivity {

    private final IONotificationService ioNotificationService;

    public IONotificationDebtPositionActivityImpl(IONotificationService ioNotificationService) {
        this.ioNotificationService = ioNotificationService;
    }

    @Override
    public List<MessageResponseDTO> sendIoNotification(DebtPositionDTO debtPosition, Map<String, IupdSyncStatusUpdateDTO> iupdSyncStatusUpdateDTOMap, GenericWfExecutionConfig.IONotificationBaseOpsMessages ioMessages) {
        log.info("Sending message to IONotification for debt position type org id {}", debtPosition.getDebtPositionTypeOrgId());
        return ioNotificationService.sendMessage(debtPosition, iupdSyncStatusUpdateDTOMap, ioMessages);
    }
}
