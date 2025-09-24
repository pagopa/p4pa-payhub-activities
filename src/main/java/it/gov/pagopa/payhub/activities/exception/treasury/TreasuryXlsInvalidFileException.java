package it.gov.pagopa.payhub.activities.exception.treasury;

import it.gov.pagopa.payhub.activities.exception.NotRetryableActivityException;

public class TreasuryXlsInvalidFileException extends NotRetryableActivityException {
	public TreasuryXlsInvalidFileException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
