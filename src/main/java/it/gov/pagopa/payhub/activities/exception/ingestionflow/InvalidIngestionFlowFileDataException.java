package it.gov.pagopa.payhub.activities.exception.ingestionflow;

import it.gov.pagopa.payhub.activities.exception.ActivitiesException;

public class InvalidIngestionFlowFileDataException extends ActivitiesException {

	public InvalidIngestionFlowFileDataException(String message) {
		super(message);
	}
}
