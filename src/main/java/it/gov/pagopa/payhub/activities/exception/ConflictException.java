package it.gov.pagopa.payhub.activities.exception;

public class ConflictException extends NotRetryableActivityException {

    public ConflictException(String code, String message) {
        super(code, message);
    }

}
