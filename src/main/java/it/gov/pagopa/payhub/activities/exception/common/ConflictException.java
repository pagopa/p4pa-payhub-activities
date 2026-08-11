package it.gov.pagopa.payhub.activities.exception.common;

import it.gov.pagopa.payhub.activities.config.rest.PuErrorDTO;
import it.gov.pagopa.payhub.activities.exception.NotRetryableActivityException;

import java.util.List;

@SuppressWarnings("java:S110") // Suppress "Inheritance tree of classes should not be too deep": allowed for exception hierarchy
public class ConflictException extends NotRetryableActivityException {

    public ConflictException(String code, String message) {
        super(code, message);
    }

    public ConflictException(String code, String message, List<PuErrorDTO.ErrorFieldDTO> fieldErrors) {
        super(code, message, fieldErrors, null);
    }
}
