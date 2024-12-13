package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.dao.ClassifyDao;
import it.gov.pagopa.payhub.activities.dao.TreasuryDao;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.exception.ClearClassifyIufException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@Lazy
@Component
public class ClearClassifyIufActivityImpl implements ClearClassifyIufActivity {
    private final TreasuryDao treasuryDao;
    private final ClassifyDao classifyDao;

    public ClearClassifyIufActivityImpl(TreasuryDao treasuryDao, ClassifyDao classifyDao) {
        this.treasuryDao = treasuryDao;
        this.classifyDao = classifyDao;
    }

    /**
     *  deletion of a classification based on the provided parameters
     *
     * @param iuf the unique identifier of the payment reporting flow (IUF)
     * @param classification classification to delete
     * @return boolean true for a successful deletion otherwise false
     */
    public boolean deleteClassificationByIuf(String iuf, String classification) {
        verifyParameters(iuf, classification);

        for (TreasuryDTO treasuryDTO : treasuryDao.searchByIuf(iuf)) {
            try {
                classifyDao.deleteClassificationByIuf(treasuryDTO.getCodIdUnivocoFlusso(), classification);
            }
            catch (ClearClassifyIufException clearClassifyIufException) {
                log.error("Error deleting classification");
                throw new ClearClassifyIufException("Error deleting classification");
            }
        }
        return true;
    }

    /**
     *
     * @param iuf unique identifier of the payment reporting flow (IUF)
     * @param classification classification
     */

    private static void verifyParameters(String iuf, String classification) {
        if (classification ==null || classification.isEmpty())
            throw new ClearClassifyIufException("classification may be not null or blank");
        if (iuf ==null || iuf.isEmpty())
            throw new ClearClassifyIufException ("iuf may be not null or blank");
    }

}
