package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.dao.PaymentsClassificationDao;
import it.gov.pagopa.payhub.activities.dto.classifications.PaymentsClassificationDTO;
import it.gov.pagopa.payhub.activities.exception.PaymentsClassificationSaveException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Interface for defining an activity to process payment reporting classifications based on IUF.
 */
@Slf4j
@Lazy
@Component
public class IufClassificationActivityImpl implements IufClassificationActivity {
    private final PaymentsClassificationDao paymentsClassificationDao;

    public IufClassificationActivityImpl(PaymentsClassificationDao  paymentsClassificationDao) {
        this.paymentsClassificationDao = paymentsClassificationDao;
    }

    /**
     * save payment classification
     * @param paymentsClassificationDTO dto containing data to save
     * @return true if the payments classification save is successful, false otherwise
     */

    @Override
    public boolean save(PaymentsClassificationDTO paymentsClassificationDTO) throws PaymentsClassificationSaveException {
        boolean goodClassification = (paymentsClassificationDTO!=null);
        if (goodClassification) {
            try {
                return paymentsClassificationDao.save(paymentsClassificationDTO);
            }
            catch (PaymentsClassificationSaveException e){
                log.error("Error saving classification: "+e.getMessage());
                throw new PaymentsClassificationSaveException("Error saving classification");
            }
        }
        return false;
    }

}