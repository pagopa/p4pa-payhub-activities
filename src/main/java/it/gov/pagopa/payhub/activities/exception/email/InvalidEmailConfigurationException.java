package it.gov.pagopa.payhub.activities.exception.email;

import it.gov.pagopa.payhub.activities.exception.NotRetryableActivityException;

public class InvalidEmailConfigurationException extends NotRetryableActivityException {
    public InvalidEmailConfigurationException(String message) {
        super(message);
    }
}
