package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.dto.classifications.PaymentsClassificationDTO;
import it.gov.pagopa.payhub.activities.exception.PaymentsClassificationSaveException;

/**
 * Interface for defining an activity to process payment reporting classifications based on IUF.
 */
public interface IufClassificationActivity {
    /**
     *
     * @param paymentsClassificationDTO dto containing data to save
     * @return true if the payments classification save is successful, false otherwise
     */
    boolean save(PaymentsClassificationDTO paymentsClassificationDTO) throws PaymentsClassificationSaveException;
}