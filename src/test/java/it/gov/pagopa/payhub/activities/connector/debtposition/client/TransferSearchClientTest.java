package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.payhub.activities.dto.classifications.TransferSemanticKeyDTO;
import it.gov.pagopa.payhub.activities.util.faker.TransferFaker;
import it.gov.pagopa.pu.debtposition.client.generated.TransferSearchControllerApi;
import it.gov.pagopa.pu.debtposition.dto.generated.Transfer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferSearchClientTest {
	@Mock
	DebtPositionApisHolder debtPositionApisHolderMock;
	@Mock
	private TransferSearchControllerApi transferSearchControllerApiMock;

	private TransferSearchClient transferSearchClient;

	@BeforeEach
	void setUp() { transferSearchClient = new TransferSearchClient(debtPositionApisHolderMock);	}

	@AfterEach
	void tearDown() { verifyNoMoreInteractions(debtPositionApisHolderMock); }

	@Test
	void whenFindBySemanticKeyThenInvokeWithAccessToken() {
		// Given
		String accessToken = "ACCESSTOKEN";
		TransferSemanticKeyDTO transferSemanticKeyMock = mock(TransferSemanticKeyDTO.class);
		Transfer expectedResult = TransferFaker.buildTransfer();

		when(debtPositionApisHolderMock.getTransferSearchControllerApi(accessToken)).thenReturn(transferSearchControllerApiMock);
		when(transferSearchControllerApiMock.crudTransfersFindBySemanticKey(
				transferSemanticKeyMock.getOrgId(),
				transferSemanticKeyMock.getIuv(),
				transferSemanticKeyMock.getIur(),
				transferSemanticKeyMock.getTransferIndex()
			)).thenReturn(expectedResult);

		// When
		Transfer result = transferSearchClient.findBySemanticKey(
			transferSemanticKeyMock.getOrgId(),
			transferSemanticKeyMock.getIuv(),
			transferSemanticKeyMock.getIur(),
			transferSemanticKeyMock.getTransferIndex(),
			accessToken
		);

		// Then
		assertSame(expectedResult, result);

		verify(debtPositionApisHolderMock).getTransferSearchControllerApi(accessToken);
		verify(transferSearchControllerApiMock).crudTransfersFindBySemanticKey(
			transferSemanticKeyMock.getOrgId(),
			transferSemanticKeyMock.getIuv(),
			transferSemanticKeyMock.getIur(),
			transferSemanticKeyMock.getTransferIndex()
		);
	}
}