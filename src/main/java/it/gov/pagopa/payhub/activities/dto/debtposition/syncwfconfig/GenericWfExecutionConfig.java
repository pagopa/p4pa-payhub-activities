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
public class GenericWfExecutionConfig implements WfExecutionConfig {
    private static final DebtPositionSyncWfName workflowTypeName = null;
    @Override
    public DebtPositionSyncWfName getWorkflowTypeName() {return workflowTypeName;}

    private IONotificationBaseOpsMessages ioMessages;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    public static class IONotificationBaseOpsMessages implements Serializable {
        private IONotificationMessage created;
        private IONotificationMessage updated;
        private IONotificationMessage deleted;
    }

}
