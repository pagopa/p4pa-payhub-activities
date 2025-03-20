package it.gov.pagopa.payhub.activities.dto.debtposition.syncwfconfig;

import it.gov.pagopa.payhub.activities.dto.IONotificationMessage;
import it.gov.pagopa.payhub.activities.enums.debtposition.DebtPositionSyncWfName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class FineWfExecutionConfig implements WfExecutionConfig {
    private static final DebtPositionSyncWfName workflowTypeName = DebtPositionSyncWfName.FINE_WF;
    @Override
    public DebtPositionSyncWfName getWorkflowTypeName() {return workflowTypeName;}

    private long discountDays;
    private long expirationDays;
    private IONotificationFineWfMessages ioMessages;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    public static class IONotificationFineWfMessages implements Serializable {
        private IONotificationMessage notified;
        private IONotificationMessage reductionExpired;
    }
}
