package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.pu.debtposition.client.generated.TransferApi;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.TransferReportedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

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
		verifyNoMoreInteractions(debtPositionApisHolderMock, transferApiMock);
	}

	@Test
	void whenNotifyReportedTransferIdThenInvokeWithAccessToken() {
		// Given
		String accessToken = "ACCESSTOKEN";
		Long transferId = 0L;
		TransferReportedRequest request = new TransferReportedRequest();
		DebtPositionDTO expectedResult = new DebtPositionDTO();

		when(debtPositionApisHolderMock.getTransferApi(accessToken)).thenReturn(transferApiMock);
		when(transferApiMock.notifyReportedTransferId(Mockito.same(transferId), Mockito.same(request)))
				.thenReturn(expectedResult);

		// When
		DebtPositionDTO result = transferClient.notifyReportedTransferId(accessToken, transferId, request);
		// Then
		Assertions.assertSame(expectedResult, result);
	}
}