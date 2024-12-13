package it.gov.pagopa.payhub.activities.dao;

import it.gov.pagopa.payhub.activities.dto.classifications.ClassifyDTO;
import it.gov.pagopa.payhub.activities.exception.PaymentsClassificationSaveException;

/**
 * Data Access Object interface  for saving payments classification
 */
public interface ClassifyDao {
	/**
	 * save classification
	 *
	 * @param classifyDTO dto classification  to save
	 * @throws PaymentsClassificationSaveException in case of errors
	 */
	void save(ClassifyDTO classifyDTO) throws PaymentsClassificationSaveException;
}

