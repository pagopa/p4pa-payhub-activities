package it.gov.pagopa.payhub.activities.activity.debtposition.synchronize.gpdpreload;

import it.gov.pagopa.payhub.activities.connector.pagopapayments.GpdPreLoadService;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildDebtPositionDTO;

@ExtendWith(MockitoExtension.class)
class SynchronizeInstallmentGpdPreLoadActivityTest {

    @Mock
    private GpdPreLoadService gpdPreLoadServiceMock;

    private SynchronizeInstallmentGpdPreLoadActivity activity;

    @BeforeEach
    void init() {
        activity = new SynchronizeInstallmentGpdPreLoadActivityImpl(gpdPreLoadServiceMock);
    }

    @Test
    void testSynchronizeInstallmentAcaActivity(){
        String iud = "IUD";
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();

        activity.synchronizeInstallmentGpdPreLoad(debtPositionDTO, iud);

        Mockito.verify(gpdPreLoadServiceMock).syncInstallmentGpdPreLoad(iud, debtPositionDTO);
    }
}
