package it.gov.pagopa.payhub.activities.activity.debtposition.aca;

import it.gov.pagopa.payhub.activities.connector.aca.AcaService;
import it.gov.pagopa.payhub.activities.connector.aca.mapper.DebtPositionDTOMapper;
import it.gov.pagopa.pu.debtposition.dto.generated.IupdSyncStatusUpdateDTO;
import it.gov.pagopa.pu.pagopapayments.dto.generated.DebtPositionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildDebtPositionDTO;
import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildPaymentsDebtPositionDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AcaStandInDeleteDebtPositionActivityTest {

    @Mock
    private AcaService acaServiceMock;

    @Mock
    private DebtPositionDTOMapper debtPositionDTOMapperMock;

    private AcaStandInDeleteDebtPositionActivity activity;

    @BeforeEach
    void init() {
        activity = new AcaStandInDeleteDebtPositionActivityImpl(acaServiceMock, debtPositionDTOMapperMock);
    }


    @Test
    void testDeleteAcaDebtPosition(){
        DebtPositionDTO paymentsDebtPositionDTO = buildPaymentsDebtPositionDTO();
        List<String> iudList = List.of("IUD1", "IUD2");
        IupdSyncStatusUpdateDTO iupdSyncStatusUpdateDTO = IupdSyncStatusUpdateDTO.builder()
                .iupdPagopa(null)
                .newStatus(IupdSyncStatusUpdateDTO.NewStatusEnum.INVALID)
                .build();
        Map<String, IupdSyncStatusUpdateDTO> expectedMap = new HashMap<>();
        expectedMap.put("IUD1", iupdSyncStatusUpdateDTO);
        expectedMap.put("IUD2", iupdSyncStatusUpdateDTO);

        when(debtPositionDTOMapperMock.map(buildDebtPositionDTO())).thenReturn(paymentsDebtPositionDTO);
        when(acaServiceMock.deleteAcaDebtPosition(paymentsDebtPositionDTO)).thenReturn(iudList);

        Map<String, IupdSyncStatusUpdateDTO> result = activity.deleteAcaDebtPosition(buildDebtPositionDTO());

        Mockito.verify(acaServiceMock, Mockito.times(1))
                .deleteAcaDebtPosition(buildPaymentsDebtPositionDTO());
        assertEquals(result, expectedMap);
    }
}
