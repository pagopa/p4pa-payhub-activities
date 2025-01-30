package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
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
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

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
		Long organizationId = 0L;
		String iuv = "IUV";
		String iur = "IUR";
		Integer transferIndex = 1;
		Transfer expectedResult = TransferFaker.buildTransfer();

		when(debtPositionApisHolderMock.getTransferSearchControllerApi(accessToken)).thenReturn(transferSearchControllerApiMock);
		when(transferSearchControllerApiMock.crudTransfersFindBySemanticKey(
				organizationId,
				iuv,
				iur,
				transferIndex,
				null
			)).thenReturn(expectedResult);

		// When
		Transfer result = transferSearchClient.findBySemanticKey(
				organizationId,
				iuv,
				iur,
				transferIndex,
			accessToken
		);

		// Then
		assertSame(expectedResult, result);
	}
}