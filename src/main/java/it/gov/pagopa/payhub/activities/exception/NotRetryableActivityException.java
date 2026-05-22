package it.gov.pagopa.payhub.activities.exception;

/** If thrown by an Activity, it cannot be retried */
public class NotRetryableActivityException extends BaseBusinessException {

    public NotRetryableActivityException(String code, String message, Throwable throwable){
        super(code, message, throwable);
    }

    public NotRetryableActivityException(String message, Throwable throwable){
        this("NOT_RETRYABLE_ERROR", message, throwable);
    }

    public NotRetryableActivityException(String code, String message){
        this(code, message, null);
    }

    public NotRetryableActivityException(String message){
        this(message, (Throwable) null);
    }
}
