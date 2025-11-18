package it.gov.pagopa.payhub.activities.connector.debtposition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.debtposition.client.ReceiptClient;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptNoPII;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptWithAdditionalNodeDataDTO;
import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

	@Test
	void whenGetByPaymentReceiptIdThenInvokeClient() {
		// Given
		String accessToken = "ACCESSTOKEN";
		ReceiptNoPII expected = mock(ReceiptNoPII.class);
		String paymentReceiptId = "paymentReceiptId";

		when(authnServiceMock.getAccessToken())
				.thenReturn(accessToken);
		when(receiptClientMock.getByPaymentReceiptId(accessToken, paymentReceiptId))
				.thenReturn(expected);

		// When
		ReceiptNoPII result = receiptService.getByPaymentReceiptId(paymentReceiptId);

		// Then
		assertEquals(expected, result);
		verify(authnServiceMock, times(1)).getAccessToken();
		verify(receiptClientMock, times(1)).getByPaymentReceiptId(accessToken, paymentReceiptId);
	}

	@Test
	void whenGetReceiptPdfThenInvokeClient() {
		// Given
		String accessToken = "ACCESSTOKEN";
		File expected = mock(File.class);
		Long receiptId = 1L;
		Long organizationId = 1L;

		when(authnServiceMock.getAccessToken())
				.thenReturn(accessToken);
		when(receiptClientMock.getReceiptPdf(accessToken, receiptId, organizationId))
				.thenReturn(expected);

		// When
		File result = receiptService.getReceiptPdf(receiptId, organizationId);

		// Then
		assertEquals(expected, result);
		verify(authnServiceMock, times(1)).getAccessToken();
		verify(receiptClientMock, times(1)).getReceiptPdf(accessToken, receiptId, organizationId);
	}
}