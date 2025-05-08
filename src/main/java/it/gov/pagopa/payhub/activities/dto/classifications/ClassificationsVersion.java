package it.gov.pagopa.payhub.activities.dto.classifications;

import lombok.Getter;

@Getter
public enum ClassificationsVersion {

    V1_3("v1.3"),
    V1_4("v1.4");

    private final String value;

    ClassificationsVersion(String value) {
        this.value = value;
    }

    public static ClassificationsVersion fromValue(String value) {
        for (ClassificationsVersion b : ClassificationsVersion.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

}
