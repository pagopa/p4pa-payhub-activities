package it.gov.pagopa.payhub.activities.activity.debtposition.acastandin;

import it.gov.pagopa.payhub.activities.mapper.DebtPositionDTOMapper;
import it.gov.pagopa.pu.pagopapayments.client.generated.AcaApi;
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
class AcaStandInActivityTest {

    @Mock
    private AcaApi acaApiMock;

    @Mock
    private DebtPositionDTOMapper debtPositionDTOMapperMock;

    private AcaStandInActivity activity;

    @BeforeEach
    void init() {
        activity = new AcaStandInActivityImpl(acaApiMock, debtPositionDTOMapperMock);
    }


    @Test
    void testCreateAcaDebtPosition(){
        when(debtPositionDTOMapperMock.map(buildDebtPositionDTO())).thenReturn(buildPaymentsDebtPositionDTO());

        activity.createAcaDebtPosition(buildDebtPositionDTO());

        Mockito.verify(acaApiMock, Mockito.times(1))
                .createAca(buildPaymentsDebtPositionDTO());
    }
}
