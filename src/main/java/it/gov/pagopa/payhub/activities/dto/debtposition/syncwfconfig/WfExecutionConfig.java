package it.gov.pagopa.payhub.activities.dto.debtposition.syncwfconfig;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.gov.pagopa.payhub.activities.enums.debtposition.DebtPositionSyncWfName;

import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
        property = "workflowTypeName",
        defaultImpl = GenericWfExecutionConfig.class)
@JsonSubTypes({
        @JsonSubTypes.Type(name = "fineWf", value = FineWfExecutionConfig.class),
})
public interface WfExecutionConfig extends Serializable {
    DebtPositionSyncWfName getWorkflowTypeName();
}
