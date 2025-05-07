package it.gov.pagopa.payhub.activities.exception;

public class OrganizationNotFoundException extends NotRetryableActivityException{

    public OrganizationNotFoundException(String message) {
        super(message);
    }
}
