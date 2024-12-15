package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.dao.ClassifyDao;
import it.gov.pagopa.payhub.activities.dto.classifications.ClassifyDTO;
import it.gov.pagopa.payhub.activities.exception.ClearClassifyIufException;
import it.gov.pagopa.payhub.activities.utility.Utilities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@Lazy
@Component
public class ClearClassifyIufActivityImpl implements ClearClassifyIufActivity {
    private final ClassifyDao classifyDao;

    public ClearClassifyIufActivityImpl(ClassifyDao classifyDao) {
        this.classifyDao = classifyDao;
    }

    /**
     *
     * @param classifyDTO dto containing classification to delete
     * @return boolean true for a successful deletion otherwise false
     * @throws ClearClassifyIufException specific exception thrown
     */
    public boolean deleteClassificationByIuf(ClassifyDTO classifyDTO) throws ClearClassifyIufException {
        boolean deletedSuccessfully = true;
        String classification = classifyDTO.getClassificationCode();
        Long paymentReportingId = classifyDTO.getPaymentReportingId();
        verifyParameters(paymentReportingId, classification);
        try {
            classifyDao.deleteClassificationByIuf(paymentReportingId, classification);
        }
        catch (ClearClassifyIufException ex) {
            log.error("Error deleting classification: {} for reporting id {}", classification, paymentReportingId);
            deletedSuccessfully = false;
        }
        return deletedSuccessfully;
    }

    /**
     *
     * @param paymentReportingId unique identifier of the payment reporting flow (IUF)
     * @param classification classification
     */
    private static void verifyParameters(Long paymentReportingId, String classification) {
        if (Utilities.isInvalidIdentifier(paymentReportingId))
            throw new ClearClassifyIufException ("payment reporting id may be not null or zero");
        if (Utilities.isNullOrEmptyString(classification))
            throw new ClearClassifyIufException("classification may be not null or blank");
    }
}
