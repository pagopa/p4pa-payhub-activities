package it.gov.pagopa.payhub.activities.exception.exportFlow;

/**
 * A custom exception that indicates the absence of an expected export flow
 * in the application's processing logic.
 *
 */
public class ExportFlowFileNotFoundException extends RuntimeException {
    /**
     * Constructs a new {@code IngestionFlowFileNotFoundException} with the specified detail message.
     *
     * @param message the detail message explaining the cause of the exception.
     */
    public ExportFlowFileNotFoundException(String message) {
        super(message);
    }
}
