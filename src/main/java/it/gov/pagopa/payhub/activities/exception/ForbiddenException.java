package it.gov.pagopa.payhub.activities.exception;

public class ForbiddenException extends NotRetryableActivityException {

    public ForbiddenException(String code, String message) {
        super(code, message);
    }

}
