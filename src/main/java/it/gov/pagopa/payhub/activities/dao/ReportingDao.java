package it.gov.pagopa.payhub.activities.dao;

import it.gov.pagopa.payhub.activities.dto.classifications.ReportingDTO;

import java.util.List;

/**
 * Data Access Object (DAO) interface for managing reporting.
 */
public interface ReportingDao {
	/**
	 *
	 * @param organizationId organization id
	 * @param iuf identifies a specific reporting code
	 * @return  List<ReportingDTO> of iuv data that may be an empty list
	 */
	List<ReportingDTO> findById(String organizationId, String iuf);
}

