package it.gov.pagopa.payhub.activities.exception;

public class InvalidValueException extends NotRetryableActivityException {

    public InvalidValueException(String code, String message) {
        super(code, message);
    }

    public InvalidValueException(String message) {
        this("INVALID_VALUE", message);
    }
}
