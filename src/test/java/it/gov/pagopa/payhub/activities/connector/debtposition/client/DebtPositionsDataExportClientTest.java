package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.pu.debtposition.client.generated.DataExportsApi;
import it.gov.pagopa.pu.debtposition.dto.generated.PagedInstallmentsPaidView;
import it.gov.pagopa.pu.debtposition.dto.generated.PagedReceiptsArchivingView;
import it.gov.pagopa.pu.processexecutions.dto.generated.OffsetDateTimeIntervalFilter;
import it.gov.pagopa.pu.processexecutions.dto.generated.PaidExportFileFilter;
import it.gov.pagopa.pu.processexecutions.dto.generated.ReceiptsArchivingExportFileFilter;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DebtPositionsDataExportClientTest {

    @Mock
    private DebtPositionApisHolder debtPositionApisHolderMock;

    @Mock
    private DataExportsApi dataExportsApiMock;

    DebtPositionsDataExportClient debtPositionsDataExportClient;

    @BeforeEach
    void setUp() {
        debtPositionsDataExportClient = new DebtPositionsDataExportClient(debtPositionApisHolderMock);
    }

    @Test
    void givenNoDateFilters_whenGetExportPaidInstallments_thenClientCalledWithNullDates() {
        String accessToken = "token";
        Long orgId = 1L;
        String userId = "user";
        PaidExportFileFilter filter = new PaidExportFileFilter();   // no dates
        filter.setDebtPositionOrigins(null);
        PagedInstallmentsPaidView expected = new PagedInstallmentsPaidView();

        Mockito.when(debtPositionApisHolderMock.getDataExportsApi(accessToken))
                .thenReturn(dataExportsApiMock);
        Mockito.when(dataExportsApiMock.exportPaidInstallments(
                        orgId, userId, null, null, null, null, null, null, 0, 10, null))
                .thenReturn(expected);

        PagedInstallmentsPaidView result = debtPositionsDataExportClient.getExportPaidInstallments(
                accessToken, orgId, userId, filter, 0, 10, null);

        assertEquals(expected, result);
    }

    @Test
    void givenOnlyPaymentDateTime_whenGetExportPaidInstallments_thenClientCalledCorrectly() {
        OffsetDateTime from = OffsetDateTime.now().minusDays(5);
        OffsetDateTime to = OffsetDateTime.now();
        OffsetDateTimeIntervalFilter paymentDate = new OffsetDateTimeIntervalFilter();
        paymentDate.setFrom(from);
        paymentDate.setTo(to);

        PaidExportFileFilter filter = new PaidExportFileFilter();
        filter.setDebtPositionOrigins(null);
        filter.setPaymentDateTime(paymentDate);

        PagedInstallmentsPaidView expected = new PagedInstallmentsPaidView();

        Mockito.when(debtPositionApisHolderMock.getDataExportsApi("token"))
                .thenReturn(dataExportsApiMock);
        Mockito.when(dataExportsApiMock.exportPaidInstallments(
                        1L, "user", from, to, null, null, null, null, 0, 10, null))
                .thenReturn(expected);

        PagedInstallmentsPaidView result = debtPositionsDataExportClient.getExportPaidInstallments(
                "token", 1L, "user", filter, 0, 10, null);

        assertEquals(expected, result);
    }

    @Test
    void givenOnlyInstallmentUpdateDateTime_whenGetExportPaidInstallments_thenClientCalledCorrectly() {
        OffsetDateTime from = OffsetDateTime.now().minusDays(10);
        OffsetDateTime to = OffsetDateTime.now();
        OffsetDateTimeIntervalFilter installmentDate = new OffsetDateTimeIntervalFilter();
        installmentDate.setFrom(from);
        installmentDate.setTo(to);

        PaidExportFileFilter filter = new PaidExportFileFilter();
        filter.setDebtPositionOrigins(null);
        filter.setInstallmentUpdateDateTime(installmentDate);

        PagedInstallmentsPaidView expected = new PagedInstallmentsPaidView();

        Mockito.when(debtPositionApisHolderMock.getDataExportsApi("token"))
                .thenReturn(dataExportsApiMock);
        Mockito.when(dataExportsApiMock.exportPaidInstallments(
                        1L, "user", null, null, from, to, null, null, 0, 10, null))
                .thenReturn(expected);

        PagedInstallmentsPaidView result = debtPositionsDataExportClient.getExportPaidInstallments(
                "token", 1L, "user", filter, 0, 10, null);

        assertEquals(expected, result);
    }

    @Test
    void givenBothDateFilters_whenGetExportPaidInstallments_thenClientCalledWithAllDates() {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTimeIntervalFilter paymentDate = new OffsetDateTimeIntervalFilter();
        paymentDate.setFrom(now.minusDays(5));
        paymentDate.setTo(now);

        OffsetDateTimeIntervalFilter installmentDate = new OffsetDateTimeIntervalFilter();
        installmentDate.setFrom(now.minusDays(10));
        installmentDate.setTo(now.minusDays(1));

        PaidExportFileFilter filter = new PaidExportFileFilter();
        filter.setDebtPositionOrigins(null);
        filter.setPaymentDateTime(paymentDate);
        filter.setInstallmentUpdateDateTime(installmentDate);

        PagedInstallmentsPaidView expected = new PagedInstallmentsPaidView();

        Mockito.when(debtPositionApisHolderMock.getDataExportsApi("token"))
                .thenReturn(dataExportsApiMock);
        Mockito.when(dataExportsApiMock.exportPaidInstallments(
                        1L, "user",
                        paymentDate.getFrom(), paymentDate.getTo(),
                        installmentDate.getFrom(), installmentDate.getTo(),
                        null, null, 0, 10, null))
                .thenReturn(expected);

        PagedInstallmentsPaidView result = debtPositionsDataExportClient.getExportPaidInstallments(
                "token", 1L, "user", filter, 0, 10, null);

        assertEquals(expected, result);
    }

    @Test
    void givenNoPaymentDateTime_whenGetExportReceiptsArchivingView_thenClientCalledWithNullDates() {
        ReceiptsArchivingExportFileFilter filter = new ReceiptsArchivingExportFileFilter();
        PagedReceiptsArchivingView expected = new PagedReceiptsArchivingView();

        Mockito.when(debtPositionApisHolderMock.getDataExportsApi("token"))
                .thenReturn(dataExportsApiMock);
        Mockito.when(dataExportsApiMock.exportArchivingReceipts(
                        1L, "user", null, null, 0, 10, null))
                .thenReturn(expected);

        PagedReceiptsArchivingView result = debtPositionsDataExportClient.getExportReceiptsArchivingView(
                "token", 1L, "user", filter, 0, 10, null);

        assertEquals(expected, result);
    }

    @Test
    void givenPaymentDateTime_whenGetExportReceiptsArchivingView_thenClientCalledWithDates() {
        OffsetDateTime from = OffsetDateTime.now().minusDays(7);
        OffsetDateTime to = OffsetDateTime.now();

        OffsetDateTimeIntervalFilter paymentDateTime = new OffsetDateTimeIntervalFilter();
        paymentDateTime.setFrom(from);
        paymentDateTime.setTo(to);

        ReceiptsArchivingExportFileFilter filter = new ReceiptsArchivingExportFileFilter();
        filter.setPaymentDateTime(paymentDateTime);

        PagedReceiptsArchivingView expected = new PagedReceiptsArchivingView();

        Mockito.when(debtPositionApisHolderMock.getDataExportsApi("token"))
                .thenReturn(dataExportsApiMock);
        Mockito.when(dataExportsApiMock.exportArchivingReceipts(
                        1L, "user", from, to, 0, 10, null))
                .thenReturn(expected);

        PagedReceiptsArchivingView result = debtPositionsDataExportClient.getExportReceiptsArchivingView(
                "token", 1L, "user", filter, 0, 10, null);

        assertEquals(expected, result);
    }


}