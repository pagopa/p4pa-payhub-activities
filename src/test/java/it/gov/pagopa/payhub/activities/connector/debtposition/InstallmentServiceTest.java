package it.gov.pagopa.payhub.activities.connector.debtposition;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.debtposition.client.InstallmentClient;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentNoPII;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentStatus;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

	@Test
	void whenUpdateDueDateThenInvokeClient() {
		// Given
		String accessToken = "ACCESSTOKEN";
		Long installmentId = 1L;

		when(authnServiceMock.getAccessToken()).thenReturn(accessToken);

		// When
		installmentService.updateDueDate(installmentId, LocalDate.now());

		// Then
		verify(authnServiceMock, times(1)).getAccessToken();
		verify(installmentClientMock, times(1)).updateDueDate(installmentId, LocalDate.now(), accessToken);
	}

	@Test
	void whenUpdateStatusAndSyncStatusThenInvokeClient() {
		// Given
		String accessToken = "ACCESSTOKEN";
		Long installmentId = 1L;

		when(authnServiceMock.getAccessToken()).thenReturn(accessToken);

		// When
		installmentService.updateStatusAndSyncStatus(installmentId, InstallmentStatus.UNPAID, null);

		// Then
		verify(authnServiceMock, times(1)).getAccessToken();
		verify(installmentClientMock, times(1)).updateStatusAndStatusSync(installmentId, InstallmentStatus.UNPAID, null, accessToken);
	}

	@Test
	void whenGetInstallmentsByOrgIdAndIudAndStatusThenInvokeClient() {
		// Given
		String accessToken = "ACCESSTOKEN";
		Long orgId = 1L;
		String iud = "IUD";
		List <InstallmentStatus> statuses = List.of(InstallmentStatus.PAID, InstallmentStatus.REPORTED);

		when(authnServiceMock.getAccessToken()).thenReturn(accessToken);

		// When
		installmentService.getInstallmentsByOrgIdAndIudAndStatus(orgId,iud, statuses);

		// Then
		verify(authnServiceMock, times(1)).getAccessToken();
		verify(installmentClientMock, times(1)).findCollectionByOrganizationIdAndIud(orgId, iud, statuses, accessToken);
	}

}