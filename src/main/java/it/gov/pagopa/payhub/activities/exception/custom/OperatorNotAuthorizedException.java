package it.gov.pagopa.payhub.activities.exception.custom;

import it.gov.pagopa.payhub.activities.exception.ActivitiesException;


public class OperatorNotAuthorizedException extends ActivitiesException {

    public OperatorNotAuthorizedException(String message) {
        super(message);
    }
}
