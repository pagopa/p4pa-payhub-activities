package it.gov.pagopa.payhub.activities.exception.debtposition.custom.fine;

import it.gov.pagopa.payhub.activities.exception.NotRetryableActivityException;

public class InvalidDebtPositionException extends NotRetryableActivityException {

    public InvalidDebtPositionException(String message) {
        super(message);
    }
}
