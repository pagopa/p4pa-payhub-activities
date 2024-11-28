package it.gov.pagopa.payhub.activities.dao;

import it.gov.pagopa.payhub.activities.dto.reportingflow.PaymentsReportingDTO;

public interface PaymentsReportingDao {

	/**
	 *  * It will insert a new record of the object
	 * */
	int insertPaymentsReporting(PaymentsReportingDTO paymentsReportingDTO);
}
