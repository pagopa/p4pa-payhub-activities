package it.gov.pagopa.payhub.activities.enums.debtposition;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DebtPositionSyncWfName {
    FINE_WF("fineWf");

    private final String value;

    DebtPositionSyncWfName(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static DebtPositionSyncWfName fromValue(String value) {
        for (DebtPositionSyncWfName b : DebtPositionSyncWfName.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}
