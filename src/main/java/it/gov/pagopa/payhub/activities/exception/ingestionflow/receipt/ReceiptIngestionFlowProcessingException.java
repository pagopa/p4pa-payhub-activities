package it.gov.pagopa.payhub.activities.exception.ingestionflow.receipt;

import it.gov.pagopa.payhub.activities.exception.RetryableActivityException;

public class ReceiptIngestionFlowProcessingException extends RetryableActivityException {
    public ReceiptIngestionFlowProcessingException(String message) {
        super(message);
    }

  public ReceiptIngestionFlowProcessingException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
