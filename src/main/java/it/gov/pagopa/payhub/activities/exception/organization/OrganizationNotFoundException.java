package it.gov.pagopa.payhub.activities.exception.organization;

import it.gov.pagopa.payhub.activities.exception.NotRetryableActivityException;

/**
 * A custom exception that indicates the absence of an expected organization
 * in the application's processing logic.
 *
 */
public class OrganizationNotFoundException extends NotRetryableActivityException {

    /**
     * Constructs a new {@code OrganizationNotFoundException} with the specified detail message.
     *
     * @param message the detail message explaining the cause of the exception.
     */
    public OrganizationNotFoundException(String message) {
        super(message);
    }
}
