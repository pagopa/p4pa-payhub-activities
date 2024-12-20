package it.gov.pagopa.payhub.activities.dao;

import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;

/**
 * Data Access Object interface for saving payments classification
 */
public interface ClassificationDao {
	/**
	 *
	 * @param organizationId organization id
	 * @param iuf fow identifier
	 * @param classification classification to delete
	 * @return true for success deletion
	 */
	boolean deleteClassificationByIuf(Long organizationId, String iuf, ClassificationsEnum classification);

	/**
	 *
	 * @param organizationId organization id
	 * @param iuv creditor reference identifier
	 * @return true for success deletion
	 */
	boolean deleteClassificationByTransferKeySet(Long orgId, String iuv, String iur, int transferIndex);
}
