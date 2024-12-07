package it.gov.pagopa.payhub.activities.service.paymentsreporting;

import it.gov.pagopa.payhub.activities.dao.PaymentsReportingDao;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import it.gov.pagopa.payhub.activities.exception.PaymentsReportingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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
	 * Saves a list of PaymentsReportingDTO objects to the database.
	 *
	 * @param dtos the list of PaymentsReportingDTO objects to be saved.
	 * @return the list of saved PaymentsReportingDTO objects.
	 * @throws PaymentsReportingException if an error occurs while saving the data.
	 */
	public List<PaymentsReportingDTO> savePaymentsReporting(List<PaymentsReportingDTO> dtos) {
		try {
			return paymentsReportingDao.saveAll(dtos);
		} catch (Exception e) {
			throw new PaymentsReportingException("Error occurred while saving payment reports");
		}
	}

}
