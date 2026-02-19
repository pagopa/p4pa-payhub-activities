package it.gov.pagopa.payhub.activities.exception.sendnotification;

import it.gov.pagopa.payhub.activities.exception.NotRetryableActivityException;

public class SendNotificationNotFoundException extends NotRetryableActivityException {

	public SendNotificationNotFoundException(String message, Throwable t) {
		super(message, t);
	}

}