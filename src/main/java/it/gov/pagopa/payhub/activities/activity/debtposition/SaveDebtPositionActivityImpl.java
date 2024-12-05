package it.gov.pagopa.payhub.activities.activity.debtposition;

import it.gov.pagopa.payhub.activities.dao.DebtPositionDao;
import it.gov.pagopa.payhub.activities.dto.debtposition.DebtPositionDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class SaveDebtPositionActivityImpl implements SaveDebtPositionActivity{

    private final DebtPositionDao debtPositionDao;

    public SaveDebtPositionActivityImpl(DebtPositionDao debtPositionDao) {
        this.debtPositionDao = debtPositionDao;
    }

    public void saveDebtPosition(DebtPositionDTO debtPosition){
        debtPositionDao.save(debtPosition);
    }
}
