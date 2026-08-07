package it.gov.pagopa.payhub.activities.exception;

import it.gov.pagopa.payhub.activities.config.rest.PuErrorDTO;
import it.gov.pagopa.payhub.activities.exception.common.BaseBusinessException;

import java.util.List;

/** If thrown by an Activity, it cannot be retried */
public class NotRetryableActivityException extends BaseBusinessException {

    public NotRetryableActivityException(String code, String message, List<PuErrorDTO.ErrorFieldDTO> fields, Throwable cause){
        super(code, message, fields, cause);
    }
    public NotRetryableActivityException(String code, String message, Throwable throwable){
        this(code, message, null, throwable);
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
