package it.gov.pagopa.payhub.activities.exception;

import it.gov.pagopa.payhub.activities.exception.common.BaseBusinessException;

/** If thrown by an Activity, it could be retried */
public class RetryableActivityException extends BaseBusinessException {

    public RetryableActivityException(String code, String message, Throwable throwable){
        super(code, message, throwable);
    }

    public RetryableActivityException(String code, String message){
        this(code, message, null);
    }
}
