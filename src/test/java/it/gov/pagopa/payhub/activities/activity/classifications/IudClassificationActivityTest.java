package it.gov.pagopa.payhub.activities.activity.classifications;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

import it.gov.pagopa.payhub.activities.connector.classification.ClassificationService;
import it.gov.pagopa.payhub.activities.connector.classification.PaymentNotificationService;
import it.gov.pagopa.payhub.activities.connector.debtposition.InstallmentService;
import it.gov.pagopa.payhub.activities.connector.debtposition.TransferService;
import it.gov.pagopa.payhub.activities.dto.classifications.IudClassificationActivityResult;
import it.gov.pagopa.payhub.activities.util.faker.InstallmentFaker;
import it.gov.pagopa.payhub.activities.util.faker.TransferFaker;
import it.gov.pagopa.pu.debtposition.dto.generated.CollectionModelInstallmentNoPII;
import it.gov.pagopa.pu.debtposition.dto.generated.CollectionModelTransfer;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentNoPIIResponse;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentStatus;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IudClassificationActivityTest {

  @Mock
  private InstallmentService installmentServiceMock;
  @Mock
  private TransferService transferServiceMock;
  @Mock
  private ClassificationService classificationServiceMock;
  @Mock
  private PaymentNotificationService paymentNotificationServiceMock;


  private IudClassificationActivity iudClassificationActivity;

  private static final Long ORGANIZATIONID = 1L;
  private static final String IUD = "IUD";
  private static final List<InstallmentStatus> INSTALLMENT_PAYED_STATUSES_LIST =
      List.of(InstallmentStatus.PAID, InstallmentStatus.REPORTED);


  @BeforeEach
  void init() {
    iudClassificationActivity = new IudClassificationActivityImpl(installmentServiceMock,
        transferServiceMock, classificationServiceMock, paymentNotificationServiceMock);
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(installmentServiceMock, transferServiceMock);
  }


  @Test
  void givenReportedTransferWhenClassifyThenOk() {
    CollectionModelInstallmentNoPII expectedCollectionModelInstallmentNoPII = InstallmentFaker.buildCollectionModelInstallmentNoPII();

    List<InstallmentNoPIIResponse> expectedInstallmentNoPIIs = expectedCollectionModelInstallmentNoPII.getEmbedded()
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
        iudClassificationActivity.classify(ORGANIZATIONID, IUD);

    assertEquals(iudClassificationActivityResult, expectedIudClassificationActivityResult);

    Mockito.verify(installmentServiceMock, Mockito.times(1))
        .getInstallmentsByOrgIdAndIudAndStatus(
                Mockito.eq(ORGANIZATIONID),
            Mockito.eq(IUD),
            argThat(list -> list.containsAll(
                INSTALLMENT_PAYED_STATUSES_LIST) && INSTALLMENT_PAYED_STATUSES_LIST.containsAll(list)));
    Mockito.verify(transferServiceMock, Mockito.times(expectedInstallmentNoPIIs.size()))
        .findByInstallmentId(Mockito.anyLong());
  }

  @Test
  void givenInstallmentsWithTransfersWhenClassifyThenReturnTransferIndexes() {
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

    IudClassificationActivityResult result = iudClassificationActivity.classify(1L, "IUD");

    assertEquals(1L, result.getOrganizationId());
    assertEquals("IUD", result.getIud());
    assertEquals(List.of(1), result.getTransferIndexes());
    Mockito.verify(installmentServiceMock, Mockito.times(1))
        .getInstallmentsByOrgIdAndIudAndStatus(
            Mockito.eq(1L),
            Mockito.eq("IUD"),
            argThat(list -> list.containsAll(INSTALLMENT_PAYED_STATUSES_LIST) && INSTALLMENT_PAYED_STATUSES_LIST.containsAll(list)));
    Mockito.verify(transferServiceMock, Mockito.times(installmentNoPII.getEmbedded().getInstallmentNoPIIs().size()))
        .findByInstallmentId(Mockito.anyLong());
    Mockito.verifyNoInteractions(paymentNotificationServiceMock);
    Mockito.verifyNoInteractions(classificationServiceMock);
  }











}

