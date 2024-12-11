package it.gov.pagopa.payhub.activities.dao;

import it.gov.pagopa.payhub.activities.dto.classifications.PaymentsClassificationDTO;
import it.gov.pagopa.payhub.activities.exception.PaymentsClassificatioSaveException;

/**
 * Data Access Object interface  for saving payments classification
 */
public interface PaymentsClassificationDao {
	boolean save(PaymentsClassificationDTO paymentsClassificationDTO) throws PaymentsClassificatioSaveException;
}

