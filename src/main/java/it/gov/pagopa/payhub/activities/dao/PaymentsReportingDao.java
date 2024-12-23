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

	/**
	 * find payment reporting by semantic key
	 *
	 * @param orgId  organization id
	 * @param iuv    payment identifier
	 * @param iur    reporting identifier
	 * @param transferIndex transfer index
	 * @return PaymentsReportingDTO object returned
	 */
	PaymentsReportingDTO findBySemanticKey(Long orgId, String iuv, String iur, int transferIndex);
}
