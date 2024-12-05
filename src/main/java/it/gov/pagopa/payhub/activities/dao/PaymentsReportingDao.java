package it.gov.pagopa.payhub.activities.dao;

import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;

public interface PaymentsReportingDao {

	/**
	 *  * It will insert a new record of the object
	 * */
	PaymentsReportingDTO save(PaymentsReportingDTO paymentsReportingDTO);
}
