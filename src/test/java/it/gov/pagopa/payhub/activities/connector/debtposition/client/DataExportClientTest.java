package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.payhub.activities.util.Utilities;
import it.gov.pagopa.pu.debtposition.client.generated.DataExportsApi;
import it.gov.pagopa.pu.debtposition.dto.generated.PagedInstallmentsPaidView;
import it.gov.pagopa.pu.processexecutions.dto.generated.LocalDateIntervalFilter;
import it.gov.pagopa.pu.processexecutions.dto.generated.PaidExportFileFilter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class DataExportClientTest {

    @Mock
    private DebtPositionApisHolder debtPositionApisHolderMock;
    @Mock
    private DataExportsApi dataExportsApiMock;

    DataExportClient dataExportClient;
    PodamFactory podamFactory;

    @BeforeEach
    void setUp() {
        dataExportClient = new DataExportClient(debtPositionApisHolderMock);
        podamFactory = new PodamFactoryImpl();
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                debtPositionApisHolderMock,
                dataExportsApiMock
        );
    }

    @Test
    void givenParameters_WhenGetExportPaidInstallments_ThenReturnPagedInstallmentsPaidView() {
        //given
        String accessToken = "accessToken";
        Long organizationId = 1L;
        String operatorExternalUserId = "operatorExternalUserId";
        LocalDate from = LocalDate.now();
        LocalDate to = LocalDate.now().plusMonths(1);
        Long debtPositionTypeOrgId = 1L;

        LocalDateIntervalFilter paymentDate = LocalDateIntervalFilter.builder().from(from).to(to).build();

        PaidExportFileFilter paidExportFileFilter = PaidExportFileFilter.builder()
                .paymentDate(paymentDate)
                .debtPositionTypeOrgId(debtPositionTypeOrgId)
                .build();

        PagedInstallmentsPaidView expected = podamFactory.manufacturePojo(PagedInstallmentsPaidView.class);


        Mockito.when(dataExportsApiMock.exportPaidInstallments(organizationId, operatorExternalUserId, Utilities.toOffsetDateTime(paymentDate.getFrom()), Utilities.toOffsetDateTime(paymentDate.getTo()), debtPositionTypeOrgId, 0, 10, null)). thenReturn(expected);
        Mockito.when(debtPositionApisHolderMock.getDataExportsApi(accessToken)).thenReturn(dataExportsApiMock);
        //when
        PagedInstallmentsPaidView result = dataExportClient.getExportPaidInstallments(accessToken, organizationId, operatorExternalUserId, paidExportFileFilter , 0, 10, null);
        //then
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void givenParametersWithNullPaymentDate_WhenGetExportPaidInstallments_ThenReturnPagedInstallmentsPaidView() {
        //given
        String accessToken = "accessToken";
        Long organizationId = 1L;
        String operatorExternalUserId = "operatorExternalUserId";
        Long debtPositionTypeOrgId = 1L;

        PaidExportFileFilter paidExportFileFilter = PaidExportFileFilter.builder()
                .paymentDate(null)
                .debtPositionTypeOrgId(debtPositionTypeOrgId)
                .build();

        PagedInstallmentsPaidView expected = podamFactory.manufacturePojo(PagedInstallmentsPaidView.class);

        Mockito.when(dataExportsApiMock.exportPaidInstallments(organizationId, operatorExternalUserId,null,null, debtPositionTypeOrgId, 0, 10, null)). thenReturn(expected);
        Mockito.when(debtPositionApisHolderMock.getDataExportsApi(accessToken)).thenReturn(dataExportsApiMock);
        //when
        PagedInstallmentsPaidView result = dataExportClient.getExportPaidInstallments(accessToken, organizationId, operatorExternalUserId, paidExportFileFilter , 0, 10, null);
        //then
        assertNotNull(result);
        assertEquals(expected, result);
    }
}