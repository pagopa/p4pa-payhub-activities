package it.gov.pagopa.payhub.activities.exception;

/** If thrown by an Activity, it could be retried */
public class RetryableActivityException extends RuntimeException {

    public RetryableActivityException(String message, Throwable throwable){
        super(message, throwable);
    }

    public RetryableActivityException(String message){
        super(message);
    }
}
