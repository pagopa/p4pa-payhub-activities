package it.gov.pagopa.payhub.activities.exception.organization;

import it.gov.pagopa.payhub.activities.exception.NotRetryableActivityException;

/**
 * A custom exception that indicates a mismatch between the IPA code and the organization
 * in the application's processing logic.
 *
 */
public class OrganizationIpaCodeNotMatchException extends NotRetryableActivityException {

    /**
     * Constructs a new {@code OrganizationIpaCodeNotMatchException} with the specified detail message.
     *
     * @param message the detail message explaining the cause of the exception.
     */
    public OrganizationIpaCodeNotMatchException(String message) {
        super(message);
    }
}
