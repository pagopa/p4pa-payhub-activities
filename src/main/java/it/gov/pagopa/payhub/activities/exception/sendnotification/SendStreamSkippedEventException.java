package it.gov.pagopa.payhub.activities.exception.sendnotification;

import it.gov.pagopa.payhub.activities.exception.NotRetryableActivityException;

public class SendStreamSkippedEventException extends NotRetryableActivityException {

    public SendStreamSkippedEventException(String message){
        super("SEND_STREAM_EVENT_SKIP", message);
    }

}
