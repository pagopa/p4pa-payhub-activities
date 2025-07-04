package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.connector.classification.PaymentNotificationService;
import it.gov.pagopa.payhub.activities.connector.debtposition.InstallmentService;
import it.gov.pagopa.payhub.activities.connector.debtposition.TransferService;
import it.gov.pagopa.payhub.activities.dto.classifications.IudClassificationActivityResult;
import it.gov.pagopa.payhub.activities.service.classifications.TransferClassificationStoreService;
import it.gov.pagopa.payhub.activities.util.faker.InstallmentFaker;
import it.gov.pagopa.payhub.activities.util.faker.PaymentNotificationFaker;
import it.gov.pagopa.payhub.activities.util.faker.TransferFaker;
import it.gov.pagopa.pu.classification.dto.generated.Classification;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum;
import it.gov.pagopa.pu.classification.dto.generated.PaymentNotificationNoPII;
import it.gov.pagopa.pu.debtposition.dto.generated.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IudClassificationActivityTest {

    @Mock
    private InstallmentService installmentServiceMock;
    @Mock
    private TransferService transferServiceMock;
    @Mock
    private TransferClassificationStoreService transferClassificationStoreServiceMock;
    @Mock
    private PaymentNotificationService paymentNotificationServiceMock;

    private IudClassificationActivity iudClassificationActivity;

    private static final Long ORGANIZATIONID = 1L;
    private static final String IUD = "IUD";
    private static final List<InstallmentStatus> INSTALLMENT_PAYED_STATUSES_LIST =
            List.of(InstallmentStatus.PAID, InstallmentStatus.REPORTED);


    @BeforeEach
    void init() {
        iudClassificationActivity = new IudClassificationActivityImpl(
                installmentServiceMock,
                transferServiceMock,
                transferClassificationStoreServiceMock,
                paymentNotificationServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                installmentServiceMock,
                transferServiceMock,
                transferClassificationStoreServiceMock,
                paymentNotificationServiceMock);
    }

    @Test
    void givenNotifiedTransferWhenClassifyIudThenOk() {
        CollectionModelInstallmentNoPII expectedCollectionModelInstallmentNoPII = InstallmentFaker.buildCollectionModelInstallmentNoPII();

        List<InstallmentNoPII> expectedInstallmentNoPIIs = expectedCollectionModelInstallmentNoPII.getEmbedded()
                .getInstallmentNoPIIs();

        when(installmentServiceMock.getInstallmentsByOrgIdAndIudAndStatus(
                Mockito.eq(ORGANIZATIONID),
                Mockito.eq(IUD),
                argThat(list -> list.containsAll(
                        INSTALLMENT_PAYED_STATUSES_LIST) && INSTALLMENT_PAYED_STATUSES_LIST.containsAll(list))))
                .thenReturn(expectedCollectionModelInstallmentNoPII);
        when(transferServiceMock.findByInstallmentId(Mockito.anyLong()))
                .thenReturn(TransferFaker.buildCollectionModelTransfer());

        IudClassificationActivityResult expectedIudClassificationActivityResult =
                IudClassificationActivityResult
                        .builder()
                        .organizationId(1L)
                        .iud("IUD")
                        .iur("iur")
                        .iuv("iuv")
                        .transferIndexes(List.of(1))
                        .build();

        IudClassificationActivityResult iudClassificationActivityResult =
                iudClassificationActivity.classifyIud(ORGANIZATIONID, IUD);

        assertEquals(iudClassificationActivityResult, expectedIudClassificationActivityResult);

        Mockito.verify(transferServiceMock, Mockito.times(expectedInstallmentNoPIIs.size()))
                .findByInstallmentId(Mockito.anyLong());
    }

    @Test
    void givenInstallmentsWithTransfersWhenClassifyIudThenReturnTransferIndexes() {
        CollectionModelInstallmentNoPII installmentNoPII = InstallmentFaker.buildCollectionModelInstallmentNoPII();
        installmentNoPII.getEmbedded().getInstallmentNoPIIs().forEach(installment -> {
            installment.setInstallmentId(1L);
        });

        CollectionModelTransfer transferModel = TransferFaker.buildCollectionModelTransfer();
        transferModel.getEmbedded().getTransfers().forEach(transfer -> {
            transfer.setTransferIndex(1);
        });

        when(installmentServiceMock.getInstallmentsByOrgIdAndIudAndStatus(
                Mockito.eq(1L),
                Mockito.eq("IUD"),
                argThat(list -> list.containsAll(INSTALLMENT_PAYED_STATUSES_LIST) && INSTALLMENT_PAYED_STATUSES_LIST.containsAll(list))))
                .thenReturn(installmentNoPII);
        when(transferServiceMock.findByInstallmentId(Mockito.anyLong()))
                .thenReturn(transferModel);

        IudClassificationActivityResult result = iudClassificationActivity.classifyIud(1L, "IUD");

        assertEquals(1L, result.getOrganizationId());
        assertEquals("IUD", result.getIud());
        assertEquals(List.of(1), result.getTransferIndexes());

        Mockito.verify(transferServiceMock, Mockito.times(installmentNoPII.getEmbedded().getInstallmentNoPIIs().size()))
                .findByInstallmentId(Mockito.anyLong());
    }


    @Test
    void givenNoReportedTransferWhenClassifyIudThenNoInteractionWithPaymentNotificationService() {
        CollectionModelInstallmentNoPII installmentNoPII = new CollectionModelInstallmentNoPII();
        installmentNoPII.setEmbedded(new CollectionModelInstallmentNoPIIEmbedded(Collections.emptyList()));

        when(installmentServiceMock.getInstallmentsByOrgIdAndIudAndStatus(
                Mockito.eq(ORGANIZATIONID),
                Mockito.eq(IUD),
                argThat(list -> list.containsAll(INSTALLMENT_PAYED_STATUSES_LIST) && INSTALLMENT_PAYED_STATUSES_LIST.containsAll(list))))
                .thenReturn(installmentNoPII);

        IudClassificationActivityResult iudClassificationActivityResult =
                iudClassificationActivity.classifyIud(ORGANIZATIONID, IUD);

        IudClassificationActivityResult expectedIudClassificationActivityResult =
                IudClassificationActivityResult
                        .builder()
                        .organizationId(ORGANIZATIONID)
                        .iud(IUD)
                        .transferIndexes(Collections.emptyList())
                        .build();

        assertEquals(expectedIudClassificationActivityResult, iudClassificationActivityResult);
    }


    @Test
    void givenEmptyTransferIndexListWhenClassifyIudThenSaveClassification() {
        // Mocking installments with no transfers
        CollectionModelInstallmentNoPII installmentNoPII = InstallmentFaker.buildCollectionModelInstallmentNoPII();
        installmentNoPII.getEmbedded().getInstallmentNoPIIs().forEach(installment -> {
            installment.setInstallmentId(1L);
        });
        CollectionModelTransfer expectedTransferModel = TransferFaker.buildCollectionModelTransfer();
        expectedTransferModel.setEmbedded(new CollectionModelTransferEmbedded(Collections.emptyList()));


        when(installmentServiceMock.getInstallmentsByOrgIdAndIudAndStatus(
                Mockito.eq(ORGANIZATIONID),
                Mockito.eq(IUD),
                argThat(list -> list.containsAll(INSTALLMENT_PAYED_STATUSES_LIST) && INSTALLMENT_PAYED_STATUSES_LIST.containsAll(list))))
                .thenReturn(installmentNoPII);

        when(transferServiceMock.findByInstallmentId(installmentNoPII.getEmbedded().getInstallmentNoPIIs().getFirst().getInstallmentId()))
                .thenReturn(expectedTransferModel);

        PaymentNotificationNoPII paymentNotificationNoPII = PaymentNotificationFaker.buildPaymentNotificationNoPII();
        when(paymentNotificationServiceMock.getByOrgIdAndIud(ORGANIZATIONID, IUD)).thenReturn(paymentNotificationNoPII);

        Classification expectedClassification = Classification.builder()
                .organizationId(ORGANIZATIONID)
                .iud(IUD)
                .label(ClassificationsEnum.IUD_NO_RT)
                .lastClassificationDate(LocalDate.now())
                .debtPositionTypeOrgCode(paymentNotificationNoPII.getDebtPositionTypeOrgCode())
                .iuv(paymentNotificationNoPII.getIuv())
                .payDate(paymentNotificationNoPII.getPaymentExecutionDate())
                .build();

        IudClassificationActivityResult expectedIudClassificationActivityResult =
                IudClassificationActivityResult
                        .builder()
                        .organizationId(ORGANIZATIONID)
                        .iud(IUD)
                        .iuv("iuv")
                        .iur("iur")
                        .transferIndexes(Collections.emptyList())
                        .build();

        IudClassificationActivityResult iudClassificationActivityResult =
                iudClassificationActivity.classifyIud(ORGANIZATIONID, IUD);

        assertEquals(expectedIudClassificationActivityResult, iudClassificationActivityResult);

        Mockito.verify(transferServiceMock, Mockito.times(installmentNoPII.getEmbedded().getInstallmentNoPIIs().size()))
                .findByInstallmentId(Mockito.anyLong());
        Mockito.verify(transferClassificationStoreServiceMock, Mockito.times(1)).saveIudClassifications(
                paymentNotificationNoPII,
                List.of(ClassificationsEnum.IUD_NO_RT)
        );
    }


}

