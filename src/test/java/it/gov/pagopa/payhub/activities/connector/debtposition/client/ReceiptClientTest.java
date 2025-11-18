package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.payhub.activities.dto.email.FileResourceDTO;
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
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

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

	@Test
	void whenGetByReceiptIdThenInvokeWithAccessToken() {
		// Given
		String accessToken = "ACCESSTOKEN";
		Long receiptId = 1L;
		ReceiptDTO expectedResult = mock(ReceiptDTO.class);

		when(debtPositionApisHolderMock.getReceiptApi(accessToken)).thenReturn(receiptApiMock);
		when(receiptApiMock.getReceipt(receiptId)).thenReturn(expectedResult);

		// When
		ReceiptDTO result = receiptClient.getByReceiptId(accessToken, receiptId);
		// Then
		Assertions.assertEquals(expectedResult, result);

		verify(debtPositionApisHolderMock, times(1)).getReceiptApi(accessToken);
		verify(receiptApiMock, times(1)).getReceipt(receiptId);
	}

	@Test
	void whenGetByTransferIdNotFoundThenReturnNull() {
		// Given
		String accessToken = "ACCESSTOKEN";
		Long transferId = 1L;

		when(debtPositionApisHolderMock.getReceiptNoPiiSearchControllerApi(accessToken)).thenReturn(receiptNoPiiSearchControllerApiMock);
		when(receiptNoPiiSearchControllerApiMock.crudReceiptsGetByTransferId(transferId))
				.thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));

		// When
		var result = receiptClient.getByTransferId(accessToken, transferId);
		// Then
		Assertions.assertNull(result);
	}

	@Test
	void whenGetByReceiptIdNotFoundThenReturnNull() {
		// Given
		String accessToken = "ACCESSTOKEN";
		Long receiptId = 2L;

		when(debtPositionApisHolderMock.getReceiptApi(accessToken)).thenReturn(receiptApiMock);
		when(receiptApiMock.getReceipt(receiptId))
				.thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));

		// When
		var result = receiptClient.getByReceiptId(accessToken, receiptId);
		// Then
		Assertions.assertNull(result);
	}

	@Test
	void whenGetByPaymentReceiptIdThenInvokeWithAccessToken() {
		// Given
		String accessToken = "ACCESSTOKEN";
		String paymentReceiptId = "paymentReceiptId";
		ReceiptNoPII expectedResult = mock(ReceiptNoPII.class);

		when(debtPositionApisHolderMock.getReceiptNoPiiSearchControllerApi(accessToken))
				.thenReturn(receiptNoPiiSearchControllerApiMock);
		when(receiptNoPiiSearchControllerApiMock.crudReceiptsGetByPaymentReceiptId(paymentReceiptId))
				.thenReturn(expectedResult);

		// When
		ReceiptNoPII result = receiptClient.getByPaymentReceiptId(accessToken, paymentReceiptId);
		// Then
		Assertions.assertEquals(expectedResult, result);

		verify(debtPositionApisHolderMock, times(1)).getReceiptNoPiiSearchControllerApi(accessToken);
		verify(receiptNoPiiSearchControllerApiMock, times(1)).crudReceiptsGetByPaymentReceiptId(paymentReceiptId);
	}

	@Test
	void whenGetByPaymentReceiptIdThenThrowException() {
		// Given
		String accessToken = "ACCESSTOKEN";
		String paymentReceiptId = "paymentReceiptId";

		when(debtPositionApisHolderMock.getReceiptNoPiiSearchControllerApi(accessToken))
				.thenReturn(receiptNoPiiSearchControllerApiMock);
		when(receiptNoPiiSearchControllerApiMock.crudReceiptsGetByPaymentReceiptId(paymentReceiptId))
				.thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));

		// When
		ReceiptNoPII result = receiptClient.getByPaymentReceiptId(accessToken, paymentReceiptId);
		// Then
		Assertions.assertNull(result);
	}


	@Test
	void whenGetReceiptPdfThenInvokeWithAccessToken() {
		// Given
		String accessToken = "ACCESSTOKEN";
		Long receiptId = 1L;
		Long organizationId = 1L;

		ByteArrayResource expectedResource = new ByteArrayResource("PDF-DATA".getBytes());
		String expectedFileName = "filename";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentDisposition(
				ContentDisposition.attachment().filename(expectedFileName).build());
		ResponseEntity<Resource> responseEntity = new ResponseEntity<>(expectedResource, headers, HttpStatus.OK);

		when(debtPositionApisHolderMock.getReceiptApi(accessToken))
				.thenReturn(receiptApiMock);
		when(receiptApiMock.getReceiptPdfWithHttpInfo(receiptId, organizationId))
				.thenReturn(responseEntity);

		// When
		FileResourceDTO response = receiptClient.getReceiptPdf(accessToken, receiptId, organizationId);

		// Then
		Assertions.assertNotNull(response);
		Assertions.assertEquals(expectedResource,response.getResource());
		Assertions.assertEquals(expectedFileName,response.getFileName());

		verify(debtPositionApisHolderMock, times(1)).getReceiptApi(accessToken);
		verify(receiptApiMock, times(1)).getReceiptPdfWithHttpInfo(receiptId, organizationId);
	}

	@Test
	void whenGetReceiptPdfThenThrowException() {
		// Given
		String accessToken = "ACCESSTOKEN";
		Long receiptId = 1L;
		Long organizationId = 1L;

		when(debtPositionApisHolderMock.getReceiptApi(accessToken))
				.thenReturn(receiptApiMock);
		when(receiptApiMock.getReceiptPdfWithHttpInfo(receiptId, organizationId))
				.thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));

		// When
		FileResourceDTO result = receiptClient.getReceiptPdf(accessToken, receiptId, organizationId);
		// Then
		Assertions.assertNull(result);
	}

}