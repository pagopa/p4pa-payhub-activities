package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.debtposition.client.DataExportClient;
import it.gov.pagopa.payhub.activities.dto.export.debtposition.PaidInstallmentsRequestFilterDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.PagedInstallmentsPaidView;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class DataExportServiceImplTest {

    @Mock
    private DataExportClient dataExportClientMock;
    @Mock
    private AuthnService authnServiceMock;

    private PodamFactory podamFactory;
    DataExportService dataExportService;

    @BeforeEach
    void setUp() {
        dataExportService = new DataExportServiceImpl(dataExportClientMock, authnServiceMock);
        podamFactory = new PodamFactoryImpl();
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                dataExportClientMock,
                authnServiceMock
        );
    }

    @Test
    void givenParameters_WhenGetExportPaidInstallments_ThenReturnPagedInstallmentsPaidView() {
        //given
        String accessToken = "accessToken";

        PaidInstallmentsRequestFilterDTO paidInstallmentsRequestFilterDTO = podamFactory.manufacturePojo(PaidInstallmentsRequestFilterDTO.class);
        PagedInstallmentsPaidView expected = podamFactory.manufacturePojo(PagedInstallmentsPaidView.class);

        Mockito.when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
        Mockito.when(dataExportClientMock.getExportPaidInstallments(accessToken, paidInstallmentsRequestFilterDTO, 0, 10, null)).thenReturn(expected);
        //when
        PagedInstallmentsPaidView result = dataExportService.exportPaidInstallments(paidInstallmentsRequestFilterDTO, 0, 10, null);
        //then
        assertNotNull(result);
        assertEquals(expected, result);
    }
}