package it.gov.pagopa.payhub.activities.activity.debtposition.aca;

import it.gov.pagopa.payhub.activities.connector.aca.AcaService;
import it.gov.pagopa.payhub.activities.connector.aca.mapper.DebtPositionDTOMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildDebtPositionDTO;
import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildPaymentsDebtPositionDTO;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AcaStandInCreateDebtPositionActivityTest {

    @Mock
    private AcaService acaServiceMock;

    @Mock
    private DebtPositionDTOMapper debtPositionDTOMapperMock;

    private AcaStandInCreateDebtPositionActivity activity;

    @BeforeEach
    void init() {
        activity = new AcaStandInCreateDebtPositionActivityImpl(acaServiceMock, debtPositionDTOMapperMock);
    }


    @Test
    void testCreateAcaDebtPosition(){
        when(debtPositionDTOMapperMock.map(buildDebtPositionDTO())).thenReturn(buildPaymentsDebtPositionDTO());

        activity.createAcaDebtPosition(buildDebtPositionDTO());

        Mockito.verify(acaServiceMock, Mockito.times(1))
                .createAcaDebtPosition(buildPaymentsDebtPositionDTO());
    }
}
