package it.gov.pagopa.payhub.activities.exception.exportFlow;

import it.gov.pagopa.payhub.activities.exception.NotRetryableActivityException;

/**
 * A custom exception that indicates the unsupported file type encountered during the export flow
 * in the application's processing logic.
 *
 */
public class ExportFlowFileTypeNotSupported extends NotRetryableActivityException {

    /**
     * Constructs a new {@code ExportFlowFileTypeNotSupported} with the specified detail message.
     *
     * @param message the detail message explaining the cause of the exception.
     */
    public ExportFlowFileTypeNotSupported(String message) {
        super(message);
    }
}
