package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import static it.gov.pagopa.payhub.activities.util.faker.InstallmentFaker.buildCollectionModelInstallmentNoPII;
import static it.gov.pagopa.payhub.activities.util.faker.InstallmentFaker.buildInstallmentDTO;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.pu.debtposition.client.generated.InstallmentApi;
import it.gov.pagopa.pu.debtposition.client.generated.InstallmentNoPiiEntityControllerApi;
import it.gov.pagopa.pu.debtposition.client.generated.InstallmentNoPiiSearchControllerApi;
import it.gov.pagopa.pu.debtposition.dto.generated.CollectionModelInstallmentNoPII;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentNoPII;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentStatus;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
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

@ExtendWith(MockitoExtension.class)
class InstallmentClientTest {
	@Mock
	private DebtPositionApisHolder debtPositionApisHolderMock;
	@Mock
	private InstallmentApi installmentApiMock;
	@Mock
	private InstallmentNoPiiEntityControllerApi installmentNoPiiEntityControllerApiMock;
	@Mock
	InstallmentNoPiiSearchControllerApi installmentNoPiiSearchControllerApiMock;

	@InjectMocks
	private InstallmentClient installmentClient;

	@BeforeEach
	void setUp() {
		installmentClient = new InstallmentClient(debtPositionApisHolderMock);
	}

	@AfterEach
	void tearDown() {
		verifyNoMoreInteractions(debtPositionApisHolderMock);
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

		verify(debtPositionApisHolderMock, times(1)).getInstallmentNoPiiEntityControllerApi(accessToken);
		verify(installmentNoPiiEntityControllerApiMock, times(1)).crudGetInstallmentnopii(String.valueOf(installmentId));
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

		when(debtPositionApisHolderMock.getInstallmentNoPiiSearchControllerApi(accessToken))
				.thenReturn(installmentNoPiiSearchControllerApiMock);

		// When
		installmentClient.updateDueDate(installmentId, LocalDate.now(), accessToken);

		// Then
		verify(debtPositionApisHolderMock, times(1)).getInstallmentNoPiiSearchControllerApi(accessToken);
		verify(installmentNoPiiSearchControllerApiMock, times(1)).crudInstallmentsUpdateDueDate(installmentId, LocalDate.now());
	}

	@Test
	void whenUpdateStatusAndStatusSyncThenInvokeWithAccessToken() {
		// Given
		String accessToken = "ACCESSTOKEN";
		Long installmentId = 1L;

		when(debtPositionApisHolderMock.getInstallmentNoPiiSearchControllerApi(accessToken))
				.thenReturn(installmentNoPiiSearchControllerApiMock);

		// When
		installmentClient.updateStatusAndStatusSync(installmentId, InstallmentStatus.UNPAID, null, accessToken);

		// Then
		verify(debtPositionApisHolderMock, times(1)).getInstallmentNoPiiSearchControllerApi(accessToken);
		verify(installmentNoPiiSearchControllerApiMock, times(1)).crudInstallmentsUpdateStatusAndToSyncStatus(installmentId, InstallmentStatus.UNPAID, null);
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

        verify(debtPositionApisHolderMock).getInstallmentApi(accessToken);
        verify(installmentApiMock).getInstallmentsByOrganizationIdAndNav(organizationId, nav, null);
    }

	@Test
	void whenInstallmentsGetByOrganizationIdAndIudAndStatusThenInvokeWithAccessToken() {
		// Given
		String accessToken = "ACCESSTOKEN";
		Long organizationId = 0L;
		String iud = "iud";
		Set<InstallmentStatus> statuses = Set.of(InstallmentStatus.PAID, InstallmentStatus.REPORTED);
		CollectionModelInstallmentNoPII expectedResult = buildCollectionModelInstallmentNoPII();

		when(debtPositionApisHolderMock.getInstallmentNoPiiSearchControllerApi(accessToken)).thenReturn(installmentNoPiiSearchControllerApiMock);
		when(installmentNoPiiSearchControllerApiMock.crudInstallmentsGetByOrganizationIdAndIudAndStatus(organizationId, iud, statuses.stream().toList())).thenReturn(expectedResult);

		// When
		CollectionModelInstallmentNoPII result = installmentClient.findCollectionByOrganizationIdAndIudAndStatus(organizationId, iud, statuses, accessToken);

		// Then
		Assertions.assertSame(expectedResult, result);

		verify(debtPositionApisHolderMock).getInstallmentNoPiiSearchControllerApi(accessToken);
		verify(installmentNoPiiSearchControllerApiMock).crudInstallmentsGetByOrganizationIdAndIudAndStatus(organizationId, iud, statuses.stream().toList());
	}
}