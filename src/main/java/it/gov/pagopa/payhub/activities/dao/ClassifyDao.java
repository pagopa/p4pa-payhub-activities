package it.gov.pagopa.payhub.activities.dao;

import it.gov.pagopa.payhub.activities.exception.ClearClassifyIufException;

/**
 * Data Access Object interface for saving payments classification
 */
public interface ClassifyDao {
	/**
	 * delete classification
	 *
	 * @param paymentReportingId reporting flow unique identifier
	 * @param classification classification to delete
	 */
	void deleteClassificationByIuf(Long paymentReportingId, String classification) throws ClearClassifyIufException;
}

