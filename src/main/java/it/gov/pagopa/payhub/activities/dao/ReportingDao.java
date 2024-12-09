package it.gov.pagopa.payhub.activities.dao;

import it.gov.pagopa.payhub.activities.dto.classifications.ReportingDTO;

import java.util.List;

/**
 * Data Access Object (DAO) interface for managing reporting.
 *
 * <p>
 * The {@code ReportingDao} provides an abstraction for accessing and manipulating reporting
 * </p>
 */
public interface ReportingDao {
	/**
	 *
	 * @param organizationId organization id
	 * @param iuf identifies a specific reporting code
	 * @return List<ClassificationDTO> of iuv data that may be an empty list
	 */
	List<ReportingDTO> findById(String organizationId, String iuf);
}

