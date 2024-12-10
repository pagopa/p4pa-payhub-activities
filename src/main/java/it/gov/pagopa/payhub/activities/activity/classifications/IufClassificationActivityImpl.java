package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.dao.PaymentsClassificationDao;
import it.gov.pagopa.payhub.activities.dto.classifications.PaymentsClassificationDTO;
import it.gov.pagopa.payhub.activities.exception.PaymentsClassificatioSaveException;
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
     *
     * @param paymentsClassificationDTO dto containing data to save
     * @return true if the payments classification save is successful, false otherwise
     */
    public boolean save(PaymentsClassificationDTO paymentsClassificationDTO) throws Exception {
        if (paymentsClassificationDTO!=null) {
            try {
                boolean saveFlag = paymentsClassificationDao.save(paymentsClassificationDTO);
                return saveFlag;
            }
            catch (Exception e){
                throw new PaymentsClassificatioSaveException("Error saving classification code: "+paymentsClassificationDTO.getClassificationCode());
            }
        }
        return false;
    }

    @Override
    public boolean classify(String organizationId, String iuf) {
        return true;
    }

}