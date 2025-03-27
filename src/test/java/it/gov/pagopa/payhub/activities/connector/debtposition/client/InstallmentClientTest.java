package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.pu.debtposition.client.generated.InstallmentNoPiiEntityControllerApi;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentNoPII;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstallmentClientTest {
	@Mock
	private DebtPositionApisHolder debtPositionApisHolderMock;
	@Mock
	private InstallmentNoPiiEntityControllerApi installmentNoPiiEntityControllerApiMock;

	@InjectMocks
	private InstallmentClient installmentClient;

	@Test
	void whenFindByIdThenInvokeWithAccessToken() {
		// Given
		String accessToken = "ACCESSTOKEN";
		Long installmentId = 1L;
		InstallmentNoPII expectedResult = mock(InstallmentNoPII.class);

		when(debtPositionApisHolderMock.getInstallmentNoPiiEntityControllerApi(accessToken)).thenReturn(installmentNoPiiEntityControllerApiMock);
		when(installmentNoPiiEntityControllerApiMock.crudGetInstallmentnopii(String.valueOf(installmentId))).thenReturn(expectedResult);

		// When
		InstallmentNoPII result = installmentClient.findById(installmentId, accessToken);
		// Then
		Assertions.assertSame(expectedResult, result);

		verify(debtPositionApisHolderMock, times(1)).getInstallmentNoPiiEntityControllerApi(accessToken);
		verify(installmentNoPiiEntityControllerApiMock, times(1)).crudGetInstallmentnopii(String.valueOf(installmentId));
	}
}