package it.gov.pagopa.payhub.activities.activity.paymentsreporting;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.pu.pagopapayments.dto.generated.PaymentsReportingIdDTO;

import java.util.List;

/**
 * Interface for retrieving the payments reporting files from PagoPA.
 */
@ActivityInterface
public interface OrganizationPaymentsReportingPagoPaFileRetrieverActivity {
	/**
	 * Retrieves the payments reporting files from PagoPA.
	 *
	 * @param paymentsReportingIds the list of payments reporting IDs
	 * @return a list of ingestion flow file IDs
	 */
	@ActivityMethod
	List<Long> getPaymentsReportingFile(List<PaymentsReportingIdDTO> paymentsReportingIds);
}
