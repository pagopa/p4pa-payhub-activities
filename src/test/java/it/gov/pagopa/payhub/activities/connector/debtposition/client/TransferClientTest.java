package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.pu.debtposition.client.generated.TransferApi;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferClientTest {
	@Mock
	private DebtPositionApisHolder debtPositionApisHolderMock;
	@Mock
	private TransferApi transferApiMock;

	private TransferClient transferClient;

	@BeforeEach
	void setUp() {
		transferClient = new TransferClient(debtPositionApisHolderMock);
	}

	@AfterEach
	void tearDown() {
		verifyNoMoreInteractions(debtPositionApisHolderMock);
	}

	@Test
	void whenNotifyReportedTransferIdThenInvokeWithAccessToken() {
		// Given
		String accessToken = "ACCESSTOKEN";
		Long transferId = 0L;
		DebtPositionDTO expectedResult = mock(DebtPositionDTO.class);

		when(debtPositionApisHolderMock.getTransferApi(accessToken)).thenReturn(transferApiMock);
		when(transferApiMock.notifyReportedTransferId(transferId)).thenReturn(expectedResult);

		// When
		DebtPositionDTO result = transferClient.notifyReportedTransferId(accessToken, transferId);
		// Then
		Assertions.assertSame(expectedResult, result);

		verify(debtPositionApisHolderMock).getTransferApi(accessToken);
		verify(transferApiMock).notifyReportedTransferId(transferId);
	}
}