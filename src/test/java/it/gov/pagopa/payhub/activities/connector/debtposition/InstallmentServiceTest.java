package it.gov.pagopa.payhub.activities.connector.debtposition;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.debtposition.client.InstallmentClient;
import it.gov.pagopa.payhub.activities.util.DebtPositionUtilities;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionOrigin;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentNoPII;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
		List<InstallmentStatus> statuses = List.of(InstallmentStatus.PAID, InstallmentStatus.REPORTED);

		when(authnServiceMock.getAccessToken()).thenReturn(accessToken);

		// When
		installmentService.getInstallmentsByOrgIdAndIudAndStatus(orgId,iud, statuses);

		// Then
		verify(authnServiceMock, times(1)).getAccessToken();
		verify(installmentClientMock, times(1)).findCollectionByOrganizationIdAndIudAndStatus(orgId, iud, statuses, accessToken);
	}

	@Test
	void whenUpdateIunByDebtPositionIdThenInvokeClient() {
		// Given
		String accessToken = "ACCESSTOKEN";
		Long debtPositionId = 1L;
		String iun = "IUN";

		when(authnServiceMock.getAccessToken()).thenReturn(accessToken);

		// When
		installmentService.updateIunByDebtPositionId(debtPositionId, iun);

		// Then
		verify(authnServiceMock, times(1)).getAccessToken();
		verify(installmentClientMock, times(1)).updateIunByDebtPositionId(debtPositionId, iun, accessToken);
	}

	@Test
	void whenGetInstallmentsByOrgIdAndReceiptIdThenInvokeClient() {
		// Given
		String accessToken = "ACCESSTOKEN";
		Long orgId = 1L;
		Long receiptId = 999L;
		List<DebtPositionOrigin> origins = List.of(DebtPositionOrigin.ORDINARY, DebtPositionOrigin.ORDINARY_SIL);

		when(authnServiceMock.getAccessToken()).thenReturn(accessToken);

		// When
		installmentService.getByOrganizationIdAndReceiptId(orgId,receiptId, origins);

		// Then
		verify(authnServiceMock, times(1)).getAccessToken();
		verify(installmentClientMock, times(1)).getByOrganizationIdAndReceiptId(orgId, receiptId, origins, accessToken);
	}

    @Test
    void whenFindByIuvOrNavThenInvokeClient() {
        // Given
        String accessToken = "ACCESSTOKEN";
        Long orgId = 1L;
        String iuv = "IUV";

        when(authnServiceMock.getAccessToken()).thenReturn(accessToken);

        // When
        installmentService.findByIuvOrNav(iuv, null, orgId, DebtPositionUtilities.UNPAID_OR_PAID_INSTALLMENT_STATUSES_LIST);

        // Then
        verify(authnServiceMock, times(1)).getAccessToken();
        verify(installmentClientMock, times(1)).findByIuvOrNav(iuv, null, orgId,  DebtPositionUtilities.UNPAID_OR_PAID_INSTALLMENT_STATUSES_LIST, accessToken);
    }

}