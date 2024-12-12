package it.gov.pagopa.payhub.activities.activity.treasury;

import io.micrometer.common.util.StringUtils;
import it.gov.pagopa.payhub.activities.dao.TreasuryDao;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryRetrieveIufActivityResult;
import it.gov.pagopa.payhub.activities.exception.RetrieveIufFromTesException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Interface for defining an activity to process Treasury
 */
@Slf4j
@Lazy
@Component
public class RetrieveIufFromTesActivityImpl implements RetrieveIufFromTesActivity{
    private final TreasuryDao treasuryDao;

    public RetrieveIufFromTesActivityImpl(TreasuryDao treasuryDao) {
        this.treasuryDao = treasuryDao;
    }

    /**
     * retrieve treasury having the same iuf
     *
     * @param iuf flow unique identifier
     * @return list of treasury dto associated to the iuf and flag true if a list is returned
     */
    @Override
    public TreasuryRetrieveIufActivityResult searchByIuf(String iuf) {
        TreasuryRetrieveIufActivityResult treasuryRetrieveIufActivityResult = new TreasuryRetrieveIufActivityResult();
        if (StringUtils.isEmpty(iuf)) {
            log.error("IUF is null or blank");
            throw new RetrieveIufFromTesException("IUF is null or blank");
        }
        List<TreasuryDTO> treasuryDTOS = treasuryDao.searchByIuf(iuf);
        if (CollectionUtils.isEmpty(treasuryDTOS)) {
            log.error("List of treasury null or empty");
            throw new RetrieveIufFromTesException("List of treasury null or empty");
        }
        treasuryRetrieveIufActivityResult.setReportingDTOList(treasuryDTOS);
        treasuryRetrieveIufActivityResult.setSuccess(true);
        return treasuryRetrieveIufActivityResult;
    }

}