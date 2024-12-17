package it.gov.pagopa.payhub.activities.exception;

public class RetryableActivityException extends RuntimeException {

    public RetryableActivityException(String message) { super(message); }
}
