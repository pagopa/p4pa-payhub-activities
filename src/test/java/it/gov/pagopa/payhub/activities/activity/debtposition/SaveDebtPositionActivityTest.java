package it.gov.pagopa.payhub.activities.activity.debtposition;

import it.gov.pagopa.payhub.activities.dao.DebtPositionDao;
import it.gov.pagopa.payhub.activities.dto.debtposition.DebtPositionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static it.gov.pagopa.payhub.activities.utility.faker.DebtPositionFaker.buildDebtPositionDTO;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SaveDebtPositionActivityTest {

    @Mock
    private DebtPositionDao debtPositionDao;

    private SaveDebtPositionActivity saveDebtPositionActivity;

    @BeforeEach
    void init(){
        saveDebtPositionActivity = new SaveDebtPositionActivityImpl(debtPositionDao);
    }

    @Test
    void givenSaveDebtPositionThenSuccess() {
        DebtPositionDTO debtPosition = buildDebtPositionDTO();
        saveDebtPositionActivity.saveDebtPosition(debtPosition);

        verify(debtPositionDao, times(1)).save(debtPosition);
    }
}
