package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.pu.debtposition.client.generated.ReceiptApi;
import it.gov.pagopa.pu.debtposition.client.generated.ReceiptNoPiiSearchControllerApi;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptNoPII;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptWithAdditionalNodeDataDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReceiptClientTest {
	@Mock
	private DebtPositionApisHolder debtPositionApisHolderMock;
	@Mock
	private ReceiptApi receiptApiMock;
	@Mock
	private ReceiptNoPiiSearchControllerApi receiptNoPiiSearchControllerApiMock;

	@InjectMocks
	private ReceiptClient receiptClient;

	@Test
	void whenNotifyReportedTransferIdThenInvokeWithAccessToken() {
		// Given
		String accessToken = "ACCESSTOKEN";
		ReceiptWithAdditionalNodeDataDTO receiptWithAdditionalNodeDataDTO = new ReceiptWithAdditionalNodeDataDTO();
		ReceiptDTO expectedResult = new ReceiptDTO();

		when(debtPositionApisHolderMock.getReceiptApi(accessToken)).thenReturn(receiptApiMock);
		when(receiptApiMock.createReceipt(receiptWithAdditionalNodeDataDTO)).thenReturn(expectedResult);

		// When
		ReceiptDTO result = receiptClient.createReceipt(accessToken, receiptWithAdditionalNodeDataDTO);
		// Then
		Assertions.assertSame(expectedResult, result);

		verify(debtPositionApisHolderMock, times(1)).getReceiptApi(accessToken);
		verify(receiptApiMock, times(1)).createReceipt(receiptWithAdditionalNodeDataDTO);
	}

	@Test
	void whenGetByTransferIdThenInvokeWithAccessToken() {
		// Given
		String accessToken = "ACCESSTOKEN";
		Long transferId = 1L;
		ReceiptNoPII expectedResult = mock(ReceiptNoPII.class);

		when(debtPositionApisHolderMock.getReceiptNoPiiSearchControllerApi(accessToken)).thenReturn(receiptNoPiiSearchControllerApiMock);
		when(receiptNoPiiSearchControllerApiMock.crudReceiptsGetByTransferId(transferId)).thenReturn(expectedResult);

		// When
		ReceiptNoPII result = receiptClient.getByTransferId(accessToken, transferId);
		// Then
		Assertions.assertEquals(expectedResult, result);

		verify(debtPositionApisHolderMock, times(1)).getReceiptNoPiiSearchControllerApi(accessToken);
		verify(receiptNoPiiSearchControllerApiMock, times(1)).crudReceiptsGetByTransferId(transferId);
	}
}