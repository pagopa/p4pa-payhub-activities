package it.gov.pagopa.payhub.activities.enums;

import java.util.EnumSet;
import java.util.Set;

public enum WorkflowStatus {
    COMPLETED,
    FAILED,
    CANCELLED,
    TERMINATED,
    TIMED_OUT;

    private static final Set<WorkflowStatus> TERMINAL_STATUSES = EnumSet.of(
            COMPLETED, FAILED, CANCELLED, TERMINATED, TIMED_OUT
    );

    public boolean isTerminal() {
        return TERMINAL_STATUSES.contains(this);
    }

    public static WorkflowStatus fromString(String status) {
        try {
            return WorkflowStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }
}

