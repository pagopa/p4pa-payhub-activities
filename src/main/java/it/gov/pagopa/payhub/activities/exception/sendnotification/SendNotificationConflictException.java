package it.gov.pagopa.payhub.activities.exception.sendnotification;

import it.gov.pagopa.payhub.activities.exception.NotRetryableActivityException;

public class SendNotificationConflictException extends NotRetryableActivityException {

    public SendNotificationConflictException(String message) {
            super(message);
        }
}
