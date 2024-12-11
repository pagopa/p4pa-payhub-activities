package it.gov.pagopa.payhub.activities.dao;

import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;

import java.util.List;

/**
 * Data Access Object (DAO) interface for handling operations related to
 * `PaymentsReportingDTO` objects.
 */
public interface PaymentsReportingDao {

	/**
	 * Persists a list of `PaymentsReportingDTO` objects into the database.
	 *
	 * @param dtos the list of `PaymentsReportingDTO` objects to be saved.
	 * @return a list of the saved `PaymentsReportingDTO` objects, potentially with updated
	 *         fields (e.g., generated IDs or timestamps).
	 */
	List<PaymentsReportingDTO> saveAll(List<PaymentsReportingDTO> dtos);
}