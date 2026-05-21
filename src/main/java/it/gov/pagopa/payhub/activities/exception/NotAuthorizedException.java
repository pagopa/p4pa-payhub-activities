package it.gov.pagopa.payhub.activities.exception;

public class NotAuthorizedException extends NotRetryableActivityException {

    public NotAuthorizedException(String code, String message) {
        super(code, message);
    }

}
