package it.gov.pagopa.payhub.activities.exception;

public class NotRetryableActivityException extends RuntimeException {

    public NotRetryableActivityException(String message) { super(message); }
}
