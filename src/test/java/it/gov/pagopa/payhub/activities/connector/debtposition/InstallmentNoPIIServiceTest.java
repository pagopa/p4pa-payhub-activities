package it.gov.pagopa.payhub.activities.connector.debtposition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.debtposition.client.InstallmentNoPIIClient;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentNoPIIResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InstallmentNoPIIServiceTest {
	@Mock
	private AuthnService authnServiceMock;
	@Mock
	private InstallmentNoPIIClient installmentNoPIIClientMock;
	@InjectMocks
	private InstallmentNoPIIServiceImpl installmentNoPIIService;


	@Test
	void whenGetByReceiptIdThenInvokeClient() {
		// Given
		String accessToken = "ACCESSTOKEN";
		List<InstallmentNoPIIResponse> expected = mock(List.class);
		Long receiptId = 1L;

		when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
		when(installmentNoPIIClientMock.getByReceiptId(accessToken, receiptId)).thenReturn(expected);

		// When
		List<InstallmentNoPIIResponse> result = installmentNoPIIService.getByReceiptId(receiptId);

		// Then
		assertEquals(expected, result);
		verify(authnServiceMock, times(1)).getAccessToken();
		verify(installmentNoPIIClientMock, times(1)).getByReceiptId(accessToken, receiptId);
	}
}