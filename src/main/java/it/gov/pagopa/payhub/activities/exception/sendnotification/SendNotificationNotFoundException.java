package it.gov.pagopa.payhub.activities.exception.sendnotification;

import it.gov.pagopa.payhub.activities.exception.NotRetryableActivityException;

public class SendNotificationNotFoundException extends NotRetryableActivityException {

	public SendNotificationNotFoundException(String message) {
		super("SEND_NOTIFICATION_NOT_FOUND", message);
	}

}