package it.gov.pagopa.payhub.activities.exception.ingestionflow;

public class TooManyAttemptsException extends Exception {

    public TooManyAttemptsException(String message) {
        super(message);
    }
}
