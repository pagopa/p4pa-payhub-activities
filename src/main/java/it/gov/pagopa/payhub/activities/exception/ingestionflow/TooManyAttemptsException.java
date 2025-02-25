package it.gov.pagopa.payhub.activities.exception.ingestionflow;

import it.gov.pagopa.payhub.activities.exception.NotRetryableActivityException;

public class TooManyAttemptsException extends NotRetryableActivityException {

    public TooManyAttemptsException(String message) {
        super(message);
    }
}
