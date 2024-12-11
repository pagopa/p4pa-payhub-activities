package it.gov.pagopa.payhub.activities.dao;

import it.gov.pagopa.payhub.activities.dto.classifications.PaymentsClassificationDTO;
import it.gov.pagopa.payhub.activities.exception.PaymentsClassificationSaveException;

/**
 * Data Access Object interface  for saving payments classification
 */
public interface PaymentsClassificationDao {
	/**
	 *
	 * @param paymentsClassificationDTO dto containing data to save
	 * @return true if the payments classification save is successful, Exception otherwise
	 * @throws PaymentsClassificationSaveException in case of errors
	 */
	boolean save(PaymentsClassificationDTO paymentsClassificationDTO) throws PaymentsClassificationSaveException;
}

