package it.gov.pagopa.payhub.activities.dto.debtposition.constants;

import java.util.Set;

public class WorkflowStatus {
    public static final String COMPLETED = "COMPLETED";
    public static final String FAILED = "FAILED";
    public static final String CANCELLED = "CANCELLED";
    public static final String TERMINATED = "TERMINATED";
    public static final String TIMED_OUT = "TIMED_OUT";

    public static final Set<String> TERMINAL_STATUSES = Set.of(COMPLETED, FAILED, CANCELLED, TERMINATED, TIMED_OUT);

    private WorkflowStatus() {}
}

