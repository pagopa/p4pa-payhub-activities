package it.gov.pagopa.payhub.activities.dao;

import it.gov.pagopa.payhub.activities.dto.classifications.ClassifyDTO;

/**
 * Data Access Object interface  for saving payments classification
 */
public interface ClassificationDao {
	/**
	 * save classification
	 *
	 * @param classifyDTO dto classification  to save
	 */
	void save(ClassifyDTO classifyDTO);
}

