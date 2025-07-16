package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.debtposition.client.ReceiptClient;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptNoPII;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptWithAdditionalNodeDataDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReceiptServiceTest {
	@Mock
	private AuthnService authnServiceMock;
	@Mock
	private ReceiptClient receiptClientMock;
	@InjectMocks
	private ReceiptServiceImpl receiptService;

	@Test
	void whenCreateReceiptThenInvokeClient() {
		// Given
		String accessToken = "ACCESSTOKEN";
		ReceiptDTO expected = new ReceiptDTO();
		ReceiptWithAdditionalNodeDataDTO receiptWithAdditionalNodeDataDTO = new ReceiptWithAdditionalNodeDataDTO();

		when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
		when(receiptClientMock.createReceipt(accessToken, receiptWithAdditionalNodeDataDTO)).thenReturn(expected);

		// When
		ReceiptDTO result = receiptService.createReceipt(receiptWithAdditionalNodeDataDTO);

		// Then
		assertSame(expected, result);
		verify(authnServiceMock,times(1)).getAccessToken();
		verify(receiptClientMock,times(1)).createReceipt(accessToken, receiptWithAdditionalNodeDataDTO);
	}

	@Test
	void whenGetByTransferIdThenInvokeClient() {
		// Given
		String accessToken = "ACCESSTOKEN";
		ReceiptNoPII expected = mock(ReceiptNoPII.class);
		Long transferId = 1L;

		when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
		when(receiptClientMock.getByTransferId(accessToken, transferId)).thenReturn(expected);

		// When
		ReceiptNoPII result = receiptService.getByTransferId(transferId);

		// Then
		assertEquals(expected, result);
		verify(authnServiceMock, times(1)).getAccessToken();
		verify(receiptClientMock, times(1)).getByTransferId(accessToken, transferId);
	}

	@Test
	void whenGetByReceiptIdThenInvokeClient() {
		// Given
		String accessToken = "ACCESSTOKEN";
		ReceiptDTO expected = mock(ReceiptDTO.class);
		Long receiptId = 1L;

		when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
		when(receiptClientMock.getByReceiptId(accessToken, receiptId)).thenReturn(expected);

		// When
		ReceiptDTO result = receiptService.getByReceiptId(receiptId);

		// Then
		assertEquals(expected, result);
		verify(authnServiceMock, times(1)).getAccessToken();
		verify(receiptClientMock, times(1)).getByReceiptId(accessToken, receiptId);
	}
}