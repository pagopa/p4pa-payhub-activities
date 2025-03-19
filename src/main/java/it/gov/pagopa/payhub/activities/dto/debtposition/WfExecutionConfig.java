package it.gov.pagopa.payhub.activities.dto.debtposition;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
        property = "workflowTypeName",
        defaultImpl = GenericWfExecutionConfig.class)
public interface WfExecutionConfig extends Serializable {
}
