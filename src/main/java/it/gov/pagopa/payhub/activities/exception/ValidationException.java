package it.gov.pagopa.payhub.activities.exception;

public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
            super(message);
        }
}
