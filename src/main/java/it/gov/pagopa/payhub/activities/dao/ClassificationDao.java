package it.gov.pagopa.payhub.activities.dao;

import it.gov.pagopa.payhub.activities.dto.classifications.ClassificationDTO;
import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;

/**
 * Data Access Object interface  for saving payments classification
 */
public interface ClassificationDao {
	/**
	 * save classification
	 *
     * @param classificationDTO dto classification  to save
     */
	void save(ClassificationDTO classificationDTO);

    /**
     * delete classification
     *
     * @param organizationId organization id
     * @param iuf fow identifier
     * @param classification classification to delete
     * @return true for success deletion
     */
	boolean deleteClassificationByIuf(Long organizationId, String iuf, ClassificationsEnum classification);
}

