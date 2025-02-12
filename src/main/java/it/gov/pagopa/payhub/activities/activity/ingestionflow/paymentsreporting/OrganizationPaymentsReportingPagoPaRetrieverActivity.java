package it.gov.pagopa.payhub.activities.activity.ingestionflow.paymentsreporting;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.pu.pagopapayments.dto.generated.PaymentsReportingIdDTO;

import java.util.List;

/**
 * Interface for retrieving the payments reporting files from PagoPA.
 */
@ActivityInterface
public interface OrganizationPaymentsReportingPagoPaRetrieverActivity {
	/**
	 * Fetch the ingestion flow files data of the relative payments reporting files from PagoPA.
	 *
	 * @param paymentsReportingIds the list of payments reporting IDs
	 * @return a list of ingestion flow file IDs
	 */
	@ActivityMethod
	List<Long> fetch(Long organizationId, List<PaymentsReportingIdDTO> paymentsReportingIds);
}
