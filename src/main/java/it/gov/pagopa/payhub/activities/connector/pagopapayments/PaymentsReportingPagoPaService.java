package it.gov.pagopa.payhub.activities.connector.pagopapayments;

import it.gov.pagopa.pu.pagopapayments.dto.generated.PaymentsReportingIdDTO;

import java.util.List;

/**
 * This interface provides a method for payments reporting on PagoPa service
 */
public interface PaymentsReportingPagoPaService {

	/**
	 * Retrieve the payments reporting for the organization
	 *
	 * @param organizationId the organization id
	 * @return the list of payments reporting info data to download
	 */
	List<PaymentsReportingIdDTO> getPaymentsReportingList(Long organizationId);
	/**
	 * Fetch the payment reporting for the organization
	 *
	 * @param organizationId the organization id
	 * @param flowId the flow id
	 * @return the payment reporting data to download
	 */
	Long fetchPaymentReporting(Long organizationId, String flowId);
}
