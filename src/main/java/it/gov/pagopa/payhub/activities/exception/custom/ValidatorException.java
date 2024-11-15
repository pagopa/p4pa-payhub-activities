package it.gov.pagopa.payhub.activities.exception.custom;

public class ValidatorException extends RuntimeException {

    public ValidatorException(String message) {
        super(message);
    }
}