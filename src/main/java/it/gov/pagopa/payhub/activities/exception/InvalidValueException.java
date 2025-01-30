package it.gov.pagopa.payhub.activities.exception;

public class InvalidValueException extends NotRetryableActivityException {

    public InvalidValueException(String message) {
            super(message);
        }
}
