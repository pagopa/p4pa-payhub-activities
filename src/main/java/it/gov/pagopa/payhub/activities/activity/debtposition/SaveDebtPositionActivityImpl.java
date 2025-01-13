package it.gov.pagopa.payhub.activities.activity.debtposition;

import it.gov.pagopa.payhub.activities.dao.DebtPositionDao;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class SaveDebtPositionActivityImpl implements SaveDebtPositionActivity{

    private final DebtPositionDao debtPositionDao;

    public SaveDebtPositionActivityImpl(DebtPositionDao debtPositionDao) {
        this.debtPositionDao = debtPositionDao;
    }

    public DebtPositionDTO saveDebtPosition(DebtPositionDTO debtPosition){
        return debtPositionDao.save(debtPosition);
    }
}
