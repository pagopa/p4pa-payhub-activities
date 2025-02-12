package it.gov.pagopa.payhub.activities.activity.debtposition.gpd;

import it.gov.pagopa.payhub.activities.connector.pagopapayments.GpdService;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildDebtPositionDTO;

@ExtendWith(MockitoExtension.class)
class SynchronizeInstallmentGpdActivityTest {

    @Mock
    private GpdService gpdServiceMock;

    private SynchronizeInstallmentGpdActivity activity;

    @BeforeEach
    void init() {
        activity = new SynchronizeInstallmentGpdActivityImpl(gpdServiceMock);
    }

    @Test
    void testSynchronizeInstallmentAcaActivity(){
        String iud = "IUD";
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();

        activity.synchronizeInstallmentGpd(debtPositionDTO, iud);

        Mockito.verify(gpdServiceMock).syncInstallmentGpd(iud, debtPositionDTO);
    }
}
