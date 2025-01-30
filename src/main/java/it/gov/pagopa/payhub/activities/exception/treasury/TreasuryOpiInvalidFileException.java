package it.gov.pagopa.payhub.activities.exception.treasury;

import it.gov.pagopa.payhub.activities.exception.NotRetryableActivityException;

public class TreasuryOpiInvalidFileException extends NotRetryableActivityException {
    public TreasuryOpiInvalidFileException(String message) {
        super(message);
    }
}
