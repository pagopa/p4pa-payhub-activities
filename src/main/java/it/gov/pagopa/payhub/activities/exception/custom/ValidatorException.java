package it.gov.pagopa.payhub.activities.exception.custom;

import it.gov.pagopa.payhub.activities.exception.ActivitiesException;


public class ValidatorException extends ActivitiesException {

    public ValidatorException(String message) {
        super(message);
    }
}
