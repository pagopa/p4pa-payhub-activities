package it.gov.pagopa.payhub.activities.service.paymentsreporting;

import it.gov.pagopa.payhub.activities.dao.PaymentsReportingDao;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for insert payments reporting data next to the data source.
 */
@Slf4j
@Service
public class PaymentsReportingInsertionService {

	/**
	 * Data Access Object for handling payment reporting data operations.
	 */
	private final PaymentsReportingDao paymentsReportingDao;

	/**
	 * Constructor for `PaymentsReportingInsertionService`.
	 *
	 * @param paymentsReportingDao the DAO used to perform operations on payment reporting data.
	 */
	public PaymentsReportingInsertionService(PaymentsReportingDao paymentsReportingDao) {
		this.paymentsReportingDao = paymentsReportingDao;
	}

	/**
	 * Inserts a payment reporting record into the data source.
	 *
	 * @param paymentsReportingDTO the DTO containing payment reporting data to be inserted.
	 * @return the number of records successfully inserted.
	 */
	public int savePaymentsReporting(PaymentsReportingDTO paymentsReportingDTO) {
		return paymentsReportingDao.save(paymentsReportingDTO);
	}

}
