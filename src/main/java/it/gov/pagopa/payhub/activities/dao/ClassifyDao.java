package it.gov.pagopa.payhub.activities.dao;

/**
 * Data Access Object interface for saving payments classification
 */
public interface ClassifyDao {
	/**
	 *
	 * @param organizationId organization id
	 * @param iuf fow identifier
	 * @param classification classification to delete
	 * @return true for success deletion
	 */
	boolean deleteClassificationByIuf(Long organizationId, String iuf, String classification);
}

