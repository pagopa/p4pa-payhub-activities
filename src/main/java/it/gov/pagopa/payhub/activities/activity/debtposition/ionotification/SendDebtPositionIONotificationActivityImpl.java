package it.gov.pagopa.payhub.activities.activity.debtposition.ionotification;

import it.gov.pagopa.payhub.activities.connector.ionotification.IONotificationFacadeService;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.IupdSyncStatusUpdateDTO;
import it.gov.pagopa.pu.ionotification.dto.generated.NotificationRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Map;

@Lazy
@Service
@Slf4j
public class SendDebtPositionIONotificationActivityImpl implements SendDebtPositionIONotificationActivity {

    private final IONotificationFacadeService ioNotificationFacadeService;

    public SendDebtPositionIONotificationActivityImpl(IONotificationFacadeService ioNotificationFacadeService) {
        this.ioNotificationFacadeService = ioNotificationFacadeService;
    }

    @Override
    public void sendMessage(DebtPositionDTO debtPosition, Map<String, IupdSyncStatusUpdateDTO> iupdSyncStatusUpdateDTOMap) {
        log.info("Sending message to IONotification for debt position type org id {}", debtPosition.getDebtPositionTypeOrgId());
        // TODO to be fix https://pagopa.atlassian.net/browse/P4ADEV-2089
        ioNotificationFacadeService.sendMessage(new NotificationRequestDTO());
    }
}
