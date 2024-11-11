package it.gov.pagopa.payhub.activities.exception;

public class ActivitiesException extends RuntimeException {

    public ActivitiesException(String message) {
        super(message);
    }

    public ActivitiesException(String message, Throwable cause) {
        super(message, cause);
    }

    public ActivitiesException(Throwable cause) {
        super(cause);
    }

    public ActivitiesException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
