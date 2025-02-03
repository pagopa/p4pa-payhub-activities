package it.gov.pagopa.payhub.activities.activity.paymentsreporting;

import java.util.List;

/**
 * Interface for retrieving the list of PaymentsReportingIdDTOs that have not been imported yet.
 */
public interface OrganizationPaymentsReportingPagoPaRetrieverActivity {
	/**
	 * Retrieves the list of PaymentsReportingIdDTOs that have not been imported yet.
	 *
	 * @param organizationId the ID of the organization
	 * @return a list of IDs of PaymentsReportingIdDTOs that have not been imported
	 */
	List<Long> retrieve(Long organizationId);
}
