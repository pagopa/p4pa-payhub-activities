package it.gov.pagopa.payhub.activities.enums;

import lombok.Getter;

@Getter
public enum EntityIdentifierType {
    F("F"),
    G("G");

    private final String value;

    EntityIdentifierType(String value) {

        this.value = value;
    }

    public static EntityIdentifierType fromValue(String value) {
        for (EntityIdentifierType b : EntityIdentifierType.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }}
