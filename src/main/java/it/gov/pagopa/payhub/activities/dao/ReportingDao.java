package it.gov.pagopa.payhub.activities.dao;

import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;

import java.util.List;

/**
 * Data Access Object (DAO) interface for managing reporting.
 */
public interface ReportingDao {
	/**
	 *
	 * @param organizationId organization id
	 * @param flowIdentifierCode identifies a specific reporting code
	 * @return List<PaymentsReportingDTO> of iuv data that may be an empty list
	 */
	List<PaymentsReportingDTO> findByOrganizationIdFlowIdentifierCode(Long organizationId, String flowIdentifierCode);
}

