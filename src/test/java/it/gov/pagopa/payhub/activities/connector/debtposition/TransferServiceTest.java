package it.gov.pagopa.payhub.activities.connector.debtposition;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.debtposition.client.TransferClient;
import it.gov.pagopa.payhub.activities.connector.debtposition.client.TransferSearchClient;
import it.gov.pagopa.payhub.activities.dto.classifications.TransferSemanticKeyDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.CollectionModelTransfer;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentStatus;
import it.gov.pagopa.pu.debtposition.dto.generated.Transfer;
import it.gov.pagopa.pu.debtposition.dto.generated.TransferReportedRequest;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {
	@Mock
	private AuthnService authnServiceMock;
	@Mock
	private TransferClient transferClientMock;
	@Mock
	private TransferSearchClient transferSearchClientMock;

	private TransferService transferService;

	@BeforeEach
	void setUp() {
		transferService = new TransferServiceImpl(authnServiceMock, transferClientMock, transferSearchClientMock);
	}

	@AfterEach
	void tearDown() {
		verifyNoMoreInteractions(authnServiceMock, transferClientMock, transferSearchClientMock);
	}

	@Test
	void whenFindBySemanticKeyThenInvokeClient() {
		// Given
		String accessToken = "ACCESSTOKEN";
		Transfer expected = mock(Transfer.class);
		TransferSemanticKeyDTO transferSemanticKey = new TransferSemanticKeyDTO(1L, "IUV", "IUR", 1);
		Set<InstallmentStatus> installmentStatusSet = Set.of(InstallmentStatus.PAID, InstallmentStatus.REPORTED);

		when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
		when(transferSearchClientMock.findBySemanticKey(1L, "IUV", "IUR", 1, installmentStatusSet, accessToken)).thenReturn(expected);

		// When
		Transfer result = transferService.findBySemanticKey(transferSemanticKey, installmentStatusSet);

		// Then
		assertSame(expected, result);
	}

	@Test
	void whenNotifyReportedTransferIdThenInvokeClient() {
		// Given
		String accessToken = "ACCESSTOKEN";
		long transferId = 1L;
		TransferReportedRequest request = new TransferReportedRequest();
		DebtPositionDTO expected = new DebtPositionDTO();

		when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
		when(transferClientMock.notifyReportedTransferId(Mockito.same(accessToken), Mockito.same(transferId), Mockito.same(request)))
				.thenReturn(expected);

		// When
		DebtPositionDTO result = transferService.notifyReportedTransferId(transferId, request);

		// Then
		assertSame(expected, result);
	}

	@Test
	void whenFindByInstallmentIdThenInvokeClient() {
		// Given
		String accessToken = "ACCESSTOKEN";
		CollectionModelTransfer expected = mock(CollectionModelTransfer.class);

		when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
		when(transferSearchClientMock.findByInstallmentId(1L, accessToken)).thenReturn(expected);

		// When
		CollectionModelTransfer result = transferService.findByInstallmentId(1L);

		// Then
		assertSame(expected, result);
	}

}