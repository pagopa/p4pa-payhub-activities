package it.gov.pagopa.payhub.activities.activity.classifications;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import it.gov.pagopa.payhub.activities.connector.debtposition.InstallmentService;
import it.gov.pagopa.payhub.activities.connector.debtposition.TransferService;
import it.gov.pagopa.payhub.activities.dto.classifications.IudClassificationActivityResult;
import it.gov.pagopa.payhub.activities.dto.classifications.Transfer2ClassifyDTO;
import it.gov.pagopa.payhub.activities.util.faker.InstallmentFaker;
import it.gov.pagopa.payhub.activities.util.faker.TransferFaker;
import it.gov.pagopa.pu.debtposition.dto.generated.CollectionModelInstallmentNoPII;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentNoPIIResponse;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentStatus;
import it.gov.pagopa.pu.debtposition.dto.generated.TransferResponse;
import java.util.List;
import java.util.Set;
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


  private IudClassificationActivity iudClassificationActivity;

  private static final Long ORGANIZATIONID = 1L;
  private static final String IUD = "IUD";
  private static final Set<InstallmentStatus> INSTALLMENT_STATUS_LIST =
      Set.of(InstallmentStatus.PAID, InstallmentStatus.REPORTED);


  @BeforeEach
  void init() {
    iudClassificationActivity = new IudClassificationActivityImpl(installmentServiceMock,
        transferServiceMock);
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

    when(installmentServiceMock.getInstallmentsByOrgIdAndIudAndStatus(ORGANIZATIONID, IUD,
        INSTALLMENT_STATUS_LIST))
        .thenReturn(expectedCollectionModelInstallmentNoPII);
    when(transferServiceMock.findByInstallmentId(Mockito.anyLong()))
        .thenReturn(TransferFaker.buildCollectionModelTransfer());

    List<Transfer2ClassifyDTO> expectedTransfer2ClassifyDTOS =
        expectedInstallmentNoPIIs
            .stream()
            .map(installmentNoPIIResponse -> {
              TransferResponse transferResponse = TransferFaker.buildCollectionModelTransfer()
                  .getEmbedded()
                  .getTransfers()
                  .get(0);
              return Transfer2ClassifyDTO.builder()
                  .iuv(installmentNoPIIResponse.getIuv())
                  .iur(installmentNoPIIResponse.getIur())
                  .transferIndex(transferResponse.getTransferIndex())
                  .build();
            })
            .toList();

    IudClassificationActivityResult expectedIudClassificationActivityResult =
        IudClassificationActivityResult
            .builder()
            .organizationId(1L)
            .transfers2classify(expectedTransfer2ClassifyDTOS)
            .build();

    IudClassificationActivityResult iudClassificationActivityResult =
        iudClassificationActivity.classify(ORGANIZATIONID, IUD);

    assertEquals(iudClassificationActivityResult, expectedIudClassificationActivityResult);

    Mockito.verify(installmentServiceMock, Mockito.times(1))
        .getInstallmentsByOrgIdAndIudAndStatus(ORGANIZATIONID, IUD, INSTALLMENT_STATUS_LIST);
    Mockito.verify(transferServiceMock, Mockito.times(expectedInstallmentNoPIIs.size()))
        .findByInstallmentId(Mockito.anyLong());
  }

}

