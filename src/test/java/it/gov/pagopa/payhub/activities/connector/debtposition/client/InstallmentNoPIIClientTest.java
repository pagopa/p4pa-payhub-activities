package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.pu.debtposition.client.generated.InstallmentNoPiiSearchControllerApi;
import it.gov.pagopa.pu.debtposition.dto.generated.CollectionModelInstallmentNoPII;
import it.gov.pagopa.pu.debtposition.dto.generated.CollectionModelInstallmentNoPIIEmbedded;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentNoPIIResponse;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;

@ExtendWith(MockitoExtension.class)
class InstallmentNoPIIClientTest {

  @Mock
  private DebtPositionApisHolder debtPositionApisHolderMock;
  @Mock
  private InstallmentNoPiiSearchControllerApi installmentNoPiiSearchControllerApiMock;

  @InjectMocks
  private InstallmentNoPIIClient installmentNoPIIClient;


	@Test
	void getByReceiptId_withValidReceiptId_returnsInstallments() {
		String accessToken = "ACCESSTOKEN";
		Long receiptId = 1L;
		List<InstallmentNoPIIResponse> expectedInstallments = List.of(new InstallmentNoPIIResponse());
		CollectionModelInstallmentNoPIIEmbedded embedded = CollectionModelInstallmentNoPIIEmbedded.builder()
				.installmentNoPIIs(expectedInstallments).build();
		CollectionModelInstallmentNoPII collectionModel = CollectionModelInstallmentNoPII.builder()
				.embedded(embedded)
				.build();

		when(debtPositionApisHolderMock.getInstallmentNoPIISearchControllerApi(accessToken))
				.thenReturn(installmentNoPiiSearchControllerApiMock);
		when(installmentNoPiiSearchControllerApiMock.crudInstallmentsFindByReceiptId(receiptId))
				.thenReturn(collectionModel);

		List<InstallmentNoPIIResponse> result = installmentNoPIIClient.getByReceiptId(accessToken, receiptId);

		Assertions.assertEquals(expectedInstallments, result);
		verify(debtPositionApisHolderMock, times(1)).getInstallmentNoPIISearchControllerApi(accessToken);
		verify(installmentNoPiiSearchControllerApiMock, times(1)).crudInstallmentsFindByReceiptId(receiptId);
	}

  @Test
  void getByReceiptId_withInvalidReceiptId_returnsNull() {
    String accessToken = "ACCESSTOKEN";
    Long receiptId = 2L;

    when(debtPositionApisHolderMock.getInstallmentNoPIISearchControllerApi(accessToken))
        .thenReturn(installmentNoPiiSearchControllerApiMock);
    when(installmentNoPiiSearchControllerApiMock.crudInstallmentsFindByReceiptId(receiptId))
        .thenThrow(HttpClientErrorException.NotFound.class);

    List<InstallmentNoPIIResponse> result = installmentNoPIIClient.getByReceiptId(accessToken,
        receiptId);

    Assertions.assertEquals(Collections.emptyList(),result);
    verify(debtPositionApisHolderMock, times(1)).getInstallmentNoPIISearchControllerApi(
        accessToken);
    verify(installmentNoPiiSearchControllerApiMock, times(1)).crudInstallmentsFindByReceiptId(
        receiptId);
  }

}