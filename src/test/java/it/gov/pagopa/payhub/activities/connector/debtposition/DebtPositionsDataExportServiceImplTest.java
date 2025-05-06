package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.debtposition.client.DataExportClient;
import it.gov.pagopa.pu.debtposition.dto.generated.PagedInstallmentsPaidView;
import it.gov.pagopa.pu.debtposition.dto.generated.PagedReceiptsArchivingView;
import it.gov.pagopa.pu.processexecutions.dto.generated.PaidExportFileFilter;
import it.gov.pagopa.pu.processexecutions.dto.generated.ReceiptsArchivingExportFileFilter;
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
class DebtPositionsDataExportServiceImplTest {

    @Mock
    private DataExportClient dataExportClientMock;
    @Mock
    private AuthnService authnServiceMock;

    private PodamFactory podamFactory;
    DebtPositionsDataExportService dataExportService;

    @BeforeEach
    void setUp() {
        dataExportService = new DebtPositionsDataExportServiceImpl(dataExportClientMock, authnServiceMock);
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
        Long organizationId = 1L;
        String operatorExternalUserId = "operatorExternalUserId";

        PaidExportFileFilter paidExportFileFilter = podamFactory.manufacturePojo(PaidExportFileFilter.class);
        PagedInstallmentsPaidView expected = podamFactory.manufacturePojo(PagedInstallmentsPaidView.class);

        Mockito.when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
        Mockito.when(dataExportClientMock.getExportPaidInstallments(accessToken, organizationId, operatorExternalUserId ,paidExportFileFilter, 0, 10, null)).thenReturn(expected);
        //when
        PagedInstallmentsPaidView result = dataExportService.exportPaidInstallments(organizationId, operatorExternalUserId,paidExportFileFilter, 0, 10, null);
        //then
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void givenParameters_WhenGetExportReceiptsArchiving_ThenReturnPagedReceiptsArchivingView() {
        //given
        String accessToken = "accessToken";
        Long organizationId = 1L;
        String operatorExternalUserId = "operatorExternalUserId";

        ReceiptsArchivingExportFileFilter receiptsArchivingExportFileFilter = podamFactory.manufacturePojo(ReceiptsArchivingExportFileFilter.class);
        PagedReceiptsArchivingView expected = podamFactory.manufacturePojo(PagedReceiptsArchivingView.class);

        Mockito.when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
        Mockito.when(dataExportClientMock.getExportReceiptsArchivingView(accessToken, organizationId, operatorExternalUserId, receiptsArchivingExportFileFilter, 0, 10, null)).thenReturn(expected);
        //when
        PagedReceiptsArchivingView result = dataExportService.exportReceiptsArchivingView(organizationId, operatorExternalUserId, receiptsArchivingExportFileFilter, 0, 10, null);
        //then
        assertNotNull(result);
        assertEquals(expected, result);
    }
}