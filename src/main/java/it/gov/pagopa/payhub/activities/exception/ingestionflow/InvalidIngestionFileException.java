package it.gov.pagopa.payhub.activities.exception.ingestionflow;

import it.gov.pagopa.payhub.activities.exception.NotRetryableActivityException;

/**
 * A custom exception that indicates an invalid ingestion file encountered
 * during the application's processing operations.
 *
 */
public class InvalidIngestionFileException extends NotRetryableActivityException {

    public InvalidIngestionFileException(String code, String message, Throwable e) {
        super(code, message, e);
    }

    public InvalidIngestionFileException(String message, Throwable e) {
        this(null, message, e);
    }

    public InvalidIngestionFileException(String code, String message) {
        this(code, message, null);
    }

    public InvalidIngestionFileException(String message) {
        this(null, message, null);
    }
}

