package it.gov.pagopa.payhub.activities.enums;

import lombok.Getter;

@Getter
public enum FlowFileType {
    REPORTING_FLOW_TYPE("R");

    private final String flowFileType;
    FlowFileType(String flowFileType) {
        this.flowFileType = flowFileType;
    }
}
