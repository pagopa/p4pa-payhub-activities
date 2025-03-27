package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.debtposition.client.InstallmentClient;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentNoPII;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstallmentServiceTest {
	@Mock
	private AuthnService authnServiceMock;
	@Mock
	private InstallmentClient installmentClientMock;

	private InstallmentService installmentService;

	@BeforeEach
	void setUp() {
		installmentService = new InstallmentServiceImpl(authnServiceMock, installmentClientMock);
	}

	@Test
	void whenGetInstallmentByIdThenInvokeClient() {
		// Given
		String accessToken = "ACCESSTOKEN";
		Long installmentId = 1L;
		InstallmentNoPII expected = mock(InstallmentNoPII.class);

		when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
		when(installmentClientMock.findById(installmentId, accessToken)).thenReturn(expected);

		// When
		Optional<InstallmentNoPII> result = installmentService.getInstallmentById(installmentId);

		// Then
		assertTrue(result.isPresent());
		assertSame(expected, result.get());

		verify(authnServiceMock, times(1)).getAccessToken();
		verify(installmentClientMock, times(1)).findById(installmentId, accessToken);
	}
}