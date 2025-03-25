package it.gov.pagopa.payhub.activities.exception.exportFlow;

import it.gov.pagopa.payhub.activities.exception.NotRetryableActivityException;

/**
 * An exception indicating that a CSV row is invalid due to data issues, such as missing required fields or data type mismatches.
 * This exception extends {@link NotRetryableActivityException}, signifying that the operation should not be retried.
 */
public class InvalidCsvRowException extends NotRetryableActivityException {

  /**
   * Constructs a new InvalidCsvRowException with the specified detail message.
   *
   * @param message The detail message (which is saved for later retrieval by the {@link #getMessage()} method).
   */
  public InvalidCsvRowException(String message) {
    super(message);
  }
}