package it.gov.pagopa.payhub.activities.exception.exportFlow;

public class InvalidExportFileException extends RuntimeException {
    public InvalidExportFileException(String message) {
        super(message);
    }
}
