package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.pu.debtposition.client.generated.InstallmentApi;
import it.gov.pagopa.pu.debtposition.client.generated.InstallmentNoPiiEntityControllerApi;
import it.gov.pagopa.pu.debtposition.client.generated.InstallmentNoPiiSearchControllerApi;
import it.gov.pagopa.pu.debtposition.client.generated.InstallmentsEntityExtendedControllerApi;
import it.gov.pagopa.pu.debtposition.dto.generated.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static it.gov.pagopa.payhub.activities.util.faker.InstallmentFaker.buildCollectionModelInstallmentNoPII;
import static it.gov.pagopa.payhub.activities.util.faker.InstallmentFaker.buildInstallmentDTO;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstallmentClientTest {
	@Mock
	private DebtPositionApisHolder debtPositionApisHolderMock;
	@Mock
	private InstallmentApi installmentApiMock;
	@Mock
	private InstallmentNoPiiEntityControllerApi installmentNoPiiEntityControllerApiMock;
	@Mock
	private InstallmentNoPiiSearchControllerApi installmentNoPiiSearchControllerApiMock;
	@Mock
	private InstallmentsEntityExtendedControllerApi installmentsEntityExtendedControllerApiMock;

	@InjectMocks
	private InstallmentClient installmentClient;

	@BeforeEach
	void setUp() {
		installmentClient = new InstallmentClient(debtPositionApisHolderMock);
	}

	@AfterEach
	void tearDown() {
		verifyNoMoreInteractions(
				debtPositionApisHolderMock,
				installmentApiMock,
				installmentNoPiiEntityControllerApiMock,
				installmentNoPiiSearchControllerApiMock,
				installmentsEntityExtendedControllerApiMock);
	}

	@Test
	void whenFindByIdThenInvokeWithAccessToken() {
		// Given
		String accessToken = "ACCESSTOKEN";
		Long installmentId = 1L;
		InstallmentNoPII expectedResult = mock(InstallmentNoPII.class);

		when(debtPositionApisHolderMock.getInstallmentNoPiiEntityControllerApi(accessToken))
			.thenReturn(installmentNoPiiEntityControllerApiMock);
		when(installmentNoPiiEntityControllerApiMock.crudGetInstallmentnopii(String.valueOf(installmentId)))
			.thenReturn(expectedResult);

		// When
		InstallmentNoPII result = installmentClient.findById(installmentId, accessToken);
		// Then
		Assertions.assertSame(expectedResult, result);
	}

	@Test
	void givenNotExistentInstallmentWhenFindByIdThenNull() {
		// Given
		String accessToken = "ACCESSTOKEN";
		Long installmentId = 0L;

		when(debtPositionApisHolderMock.getInstallmentNoPiiEntityControllerApi(accessToken))
			.thenReturn(installmentNoPiiEntityControllerApiMock);
		when(installmentNoPiiEntityControllerApiMock.crudGetInstallmentnopii(String.valueOf(installmentId)))
			.thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));

		// When
		InstallmentNoPII result = installmentClient.findById(installmentId, accessToken);

		// Then
		Assertions.assertNull(result);
	}

	@Test
	void whenUpdateDueDateThenInvokeWithAccessToken() {
		// Given
		String accessToken = "ACCESSTOKEN";
		Long installmentId = 1L;

		when(debtPositionApisHolderMock.getInstallmentsEntityExtendedControllerApi(accessToken))
				.thenReturn(installmentsEntityExtendedControllerApiMock);

		// When
		installmentClient.updateDueDate(installmentId, LocalDate.now(), accessToken);

		// Then
		verify(installmentsEntityExtendedControllerApiMock, times(1)).updateDueDate(installmentId, LocalDate.now());
	}

	@Test
	void whenUpdateStatusAndStatusSyncThenInvokeWithAccessToken() {
		// Given
		String accessToken = "ACCESSTOKEN";
		Long installmentId = 1L;
		InstallmentStatus status = InstallmentStatus.TO_SYNC;
		InstallmentSyncStatus syncStatus = new InstallmentSyncStatus(InstallmentStatus.UNPAID, InstallmentStatus.UNPAYABLE, null);

		when(debtPositionApisHolderMock.getInstallmentsEntityExtendedControllerApi(accessToken))
				.thenReturn(installmentsEntityExtendedControllerApiMock);

		// When
		installmentClient.updateStatusAndStatusSync(installmentId, status, syncStatus, accessToken);

		// Then
		verify(installmentsEntityExtendedControllerApiMock, times(1)).updateStatusAndToSyncStatus(installmentId, status, syncStatus.getSyncStatusFrom(), syncStatus.getSyncStatusTo());
	}

	@Test
	void givenNoSyncStatusWhenUpdateStatusAndStatusSyncThenInvokeWithAccessToken() {
		// Given
		String accessToken = "ACCESSTOKEN";
		Long installmentId = 1L;

		when(debtPositionApisHolderMock.getInstallmentsEntityExtendedControllerApi(accessToken))
				.thenReturn(installmentsEntityExtendedControllerApiMock);

		// When
		installmentClient.updateStatusAndStatusSync(installmentId, InstallmentStatus.UNPAID, null, accessToken);

		// Then
		verify(installmentsEntityExtendedControllerApiMock, times(1)).updateStatusAndToSyncStatus(installmentId, InstallmentStatus.UNPAID, null, null);
	}

    @Test
    void whenNotifyReportedTransferIdThenInvokeWithAccessToken() {
        // Given
        String accessToken = "ACCESSTOKEN";
        Long organizationId = 0L;
        String nav = "123456789123456789";
        List<InstallmentDTO> expectedResult = Collections.singletonList(buildInstallmentDTO());

        when(debtPositionApisHolderMock.getInstallmentApi(accessToken)).thenReturn(installmentApiMock);
        when(installmentApiMock.getInstallmentsByOrganizationIdAndNav(organizationId, nav, null)).thenReturn(expectedResult);

        // When
        List<InstallmentDTO> result = installmentClient.getInstallmentsByOrganizationIdAndNav(accessToken, organizationId, nav, null);

        // Then
        Assertions.assertSame(expectedResult, result);
    }

	@Test
	void whenInstallmentsGetByOrganizationIdAndIudAndStatusThenInvokeWithAccessToken() {
		// Given
		String accessToken = "ACCESSTOKEN";
		Long organizationId = 0L;
		String iud = "iud";
		List<InstallmentStatus> statuses = List.of(InstallmentStatus.PAID, InstallmentStatus.REPORTED);
		CollectionModelInstallmentNoPII expectedResult = buildCollectionModelInstallmentNoPII();

		when(debtPositionApisHolderMock.getInstallmentNoPiiSearchControllerApi(accessToken)).thenReturn(installmentNoPiiSearchControllerApiMock);
		when(installmentNoPiiSearchControllerApiMock.crudInstallmentsGetByOrganizationIdAndIudAndStatus(organizationId, iud, statuses.stream().toList())).thenReturn(expectedResult);

		// When
		CollectionModelInstallmentNoPII result = installmentClient.findCollectionByOrganizationIdAndIudAndStatus(organizationId, iud, statuses, accessToken);

		// Then
		Assertions.assertSame(expectedResult, result);
	}

	@Test
	void whenUpdateIunByDebtPositionIdThenInvokeWithAccessToken() {
		// Given
		String accessToken = "ACCESSTOKEN";
		Long debtPositionId = 1L;
		String iun = "IUN";

		when(debtPositionApisHolderMock.getInstallmentsEntityExtendedControllerApi(accessToken))
				.thenReturn(installmentsEntityExtendedControllerApiMock);

		// When
		installmentClient.updateIunByDebtPositionId(debtPositionId, iun, accessToken);

		// Then
		verify(installmentsEntityExtendedControllerApiMock, times(1)).updateIunByDebtPositionId(debtPositionId, iun);
	}
}