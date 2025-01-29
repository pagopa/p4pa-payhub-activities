package it.gov.pagopa.payhub.activities.exception;


public class OperatorNotAuthorizedException extends NotRetryableActivityException {

    public OperatorNotAuthorizedException(String message) {
        super(message);
    }
}
