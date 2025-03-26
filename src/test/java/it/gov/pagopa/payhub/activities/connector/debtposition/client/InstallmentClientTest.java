package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.pu.debtposition.client.generated.InstallmentApi;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static it.gov.pagopa.payhub.activities.util.faker.InstallmentFaker.buildInstallmentDTO;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstallmentClientTest {
	@Mock
	private DebtPositionApisHolder debtPositionApisHolderMock;
	@Mock
	private InstallmentApi installmentApiMock;

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
}