package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.payhub.activities.dto.OffsetDateTimeIntervalFilter;
import it.gov.pagopa.payhub.activities.dto.export.debtposition.PaidInstallmentsRequestFilterDTO;
import it.gov.pagopa.pu.debtposition.client.generated.DataExportsApi;
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

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

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
        OffsetDateTime paymentDateFrom = OffsetDateTime.now().withOffsetSameInstant(ZoneOffset.UTC);
        OffsetDateTime paymentDateTo = OffsetDateTime.now().plusMonths(1).withOffsetSameInstant(ZoneOffset.UTC);
        Long debtPositionTypeOrgId = 1L;

        OffsetDateTimeIntervalFilter offsetDateTimeIntervalFilter = OffsetDateTimeIntervalFilter.builder().from(paymentDateFrom).to(paymentDateTo).build();
        PaidInstallmentsRequestFilterDTO paidInstallmentsRequestFilterDTO = PaidInstallmentsRequestFilterDTO
                .builder()
                .organizationId(organizationId)
                .operatorExternalUserId(operatorExternalUserId)
                .paymentDate(offsetDateTimeIntervalFilter)
                .debtPositionTypeOrgId(debtPositionTypeOrgId).build();

        PagedInstallmentsPaidView expected = podamFactory.manufacturePojo(PagedInstallmentsPaidView.class);

        Mockito.when(dataExportsApiMock.exportPaidInstallments(organizationId, operatorExternalUserId, paymentDateFrom, paymentDateTo, debtPositionTypeOrgId, 0, 10, null)). thenReturn(expected);
        Mockito.when(debtPositionApisHolderMock.getDataExportsApi(accessToken)).thenReturn(dataExportsApiMock);
        //when
        PagedInstallmentsPaidView result = dataExportClient.getExportPaidInstallments(accessToken, paidInstallmentsRequestFilterDTO, 0, 10, null);
        //then
        assertNotNull(result);
        assertEquals(expected, result);
    }
}