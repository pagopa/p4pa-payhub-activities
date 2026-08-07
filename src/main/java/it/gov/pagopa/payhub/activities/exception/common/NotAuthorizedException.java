package it.gov.pagopa.payhub.activities.exception.common;

import it.gov.pagopa.payhub.activities.exception.NotRetryableActivityException;

@SuppressWarnings("java:S110") // Suppress "Inheritance tree of classes should not be too deep": allowed for exception hierarchy
public class NotAuthorizedException extends NotRetryableActivityException {

    public NotAuthorizedException(String code, String message) {
        super(code, message);
    }

}
