package it.gov.pagopa.payhub.activities.dao;

import it.gov.pagopa.payhub.activities.dto.classifications.TransferSemanticKeyDTO;
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
	 *
	 * @param organizationId organization id
	 * @param iuf identifies a specific reporting code
	 * @return List of PaymentsReportingDTO objects that may be an empty list
	 */
	List<PaymentsReportingDTO> findByOrganizationIdAndIuf(Long organizationId, String iuf);

	/**
	 * find payment reporting by semantic key
	 *
	 * @param transferSemanticKey the DTO containing semantic keys such as organization ID, IUV, IUR, and transfer index.
	 * @return PaymentsReportingDTO object returned
	 */
	PaymentsReportingDTO findBySemanticKey(TransferSemanticKeyDTO transferSemanticKey);
}
