package it.gov.pagopa.payhub.activities.dao;

import it.gov.pagopa.payhub.activities.exception.ClearClassifyIufException;

/**
 * Data Access Object interface for saving payments classification
 */
public interface ClassifyDao {
	/**
	/**
	 * delete classification
	 *
	 * @param organizationId organization id
	 * @param iuf fow identifier
	 * @param classification classification to delete
	 * @throws ClearClassifyIufException possible exception in deletion
	 */
	void deleteClassificationByIuf(Long organizationId, String iuf, String classification) throws Exception;
}

