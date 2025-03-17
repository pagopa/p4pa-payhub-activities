package it.gov.pagopa.payhub.activities.activity.paymentsreporting;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.pu.pagopapayments.dto.generated.PaymentsReportingIdDTO;

import java.util.List;

/**
 * Interface for retrieving the list of PaymentsReportingIdDTOs that have not been imported yet.
 */
@ActivityInterface
public interface OrganizationPaymentsReportingPagoPaListRetrieverActivity {
	/**
	 * Retrieves the list of PaymentsReportingIdDTOs that have not been imported yet.
	 *
	 * @param organizationId the ID of the organization
	 * @return a list of PaymentsReportingIdDTOs that have not been imported
	 */
	@ActivityMethod
	List<PaymentsReportingIdDTO> retrieveNotImportedPagoPaPaymentsReportingIds(Long organizationId);
}
