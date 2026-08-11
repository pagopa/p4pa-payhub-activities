package it.gov.pagopa.payhub.activities.exception.common;

import it.gov.pagopa.payhub.activities.config.rest.PuErrorDTO;
import it.gov.pagopa.payhub.activities.exception.NotRetryableActivityException;

import java.util.List;

@SuppressWarnings("java:S110") // Suppress "Inheritance tree of classes should not be too deep": allowed for exception hierarchy
public class InvalidValueException extends NotRetryableActivityException {

    public InvalidValueException(String code, String message, List<PuErrorDTO.ErrorFieldDTO> fieldErrors) {
        super(code, message, fieldErrors, null);
    }
    public InvalidValueException(String code, String message) {
        this(code, message, null);
    }

    public InvalidValueException(String message) {
        this("INVALID_VALUE", message);
    }
}
