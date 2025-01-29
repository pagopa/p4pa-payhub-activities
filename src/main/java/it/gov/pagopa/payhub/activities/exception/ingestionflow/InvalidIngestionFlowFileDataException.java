package it.gov.pagopa.payhub.activities.exception.ingestionflow;

import it.gov.pagopa.payhub.activities.exception.NotRetryableActivityException;

public class InvalidIngestionFlowFileDataException extends NotRetryableActivityException {

	public InvalidIngestionFlowFileDataException(String message) {
		super(message);
	}
}
