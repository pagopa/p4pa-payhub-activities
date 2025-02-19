package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.payhub.activities.util.faker.TransferFaker;
import it.gov.pagopa.pu.debtposition.client.generated.TransferSearchControllerApi;
import it.gov.pagopa.pu.debtposition.dto.generated.Transfer;
import it.gov.pagopa.pu.pagopapayments.dto.generated.InstallmentStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Set;

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
		Set<String> installmentStatusSet = Set.of(InstallmentStatus.PAID.getValue(), InstallmentStatus.REPORTED.getValue());

		when(debtPositionApisHolderMock.getTransferSearchControllerApi(accessToken)).thenReturn(transferSearchControllerApiMock);
		when(transferSearchControllerApiMock.crudTransfersFindBySemanticKey(
				organizationId,
				iuv,
				iur,
				transferIndex,
				installmentStatusSet
			)).thenReturn(expectedResult);

		// When
		Transfer result = transferSearchClient.findBySemanticKey(
				organizationId,
				iuv,
				iur,
				transferIndex,
				installmentStatusSet,
			accessToken
		);

		// Then
		assertSame(expectedResult, result);
	}

	@Test
	void givenNotExistentTransferWhenFindBySemanticKeyThenNull() {
		// Given
		String accessToken = "ACCESSTOKEN";
		Long organizationId = 0L;
		String iuv = "IUV";
		String iur = "IUR";
		Integer transferIndex = 1;
		Set<String> installmentStatusSet = Set.of(InstallmentStatus.PAID.getValue(), InstallmentStatus.REPORTED.getValue());

		when(debtPositionApisHolderMock.getTransferSearchControllerApi(accessToken)).thenReturn(transferSearchControllerApiMock);
		when(transferSearchControllerApiMock.crudTransfersFindBySemanticKey(
				organizationId,
				iuv,
				iur,
				transferIndex,
				installmentStatusSet
		)).thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));

		// When
		Transfer result = transferSearchClient.findBySemanticKey(
				organizationId,
				iuv,
				iur,
				transferIndex,
				installmentStatusSet,
				accessToken
		);

		// Then
		Assertions.assertNull(result);
	}
}