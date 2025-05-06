package it.gov.pagopa.payhub.activities.activity.debtposition.synchronize.aca;

import it.gov.pagopa.payhub.activities.connector.pagopapayments.AcaService;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildDebtPositionDTO;

@ExtendWith(MockitoExtension.class)
class SynchronizeInstallmentAcaActivityTest {

    @Mock
    private AcaService acaServiceMock;

    private SynchronizeInstallmentAcaActivity activity;

    @BeforeEach
    void init() {
        activity = new SynchronizeInstallmentAcaActivityImpl(acaServiceMock);
    }

    @Test
    void testSynchronizeInstallmentAcaActivity(){
        String iud = "IUD";
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();

        activity.synchronizeInstallmentAca(debtPositionDTO, iud);

        Mockito.verify(acaServiceMock).syncInstallmentAca(iud, debtPositionDTO);
    }
}
