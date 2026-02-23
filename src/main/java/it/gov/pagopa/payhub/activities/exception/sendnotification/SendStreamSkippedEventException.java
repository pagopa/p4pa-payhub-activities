package it.gov.pagopa.payhub.activities.exception.sendnotification;

import it.gov.pagopa.payhub.activities.exception.NotRetryableActivityException;

public class SendStreamSkippedEventException extends NotRetryableActivityException {

    public SendStreamSkippedEventException(String message){
        super(message);
    }

}
