package it.gov.pagopa.payhub.activities.dto.debtposition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class DebtPositionIoNotificationDTO {
    private Long debtPositionId;
    private Long debtPositionTypeOrgId;
    private Long organizationId;

    private List<IoMessage> messages;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    public static class IoMessage{
        private String serviceId;
        private String nav;
        private String notificationId;
    }
}
