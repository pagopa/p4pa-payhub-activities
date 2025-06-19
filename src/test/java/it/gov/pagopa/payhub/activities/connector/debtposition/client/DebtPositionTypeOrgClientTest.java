package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.pu.debtposition.client.generated.DebtPositionTypeOrgApi;
import it.gov.pagopa.pu.debtposition.client.generated.DebtPositionTypeOrgEntityControllerApi;
import it.gov.pagopa.pu.debtposition.client.generated.DebtPositionTypeOrgSearchControllerApi;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionTypeOrg;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionTypeOrgRequestBody;
import it.gov.pagopa.pu.debtposition.dto.generated.IONotificationDTO;
import it.gov.pagopa.pu.workflowhub.dto.generated.PaymentEventType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import static it.gov.pagopa.payhub.activities.util.faker.IONotificationDTOFaker.buildIONotificationDTO;

@ExtendWith(MockitoExtension.class)
class DebtPositionTypeOrgClientTest {

    @Mock
    private DebtPositionApisHolder debtPositionApisHolderMock;
    @Mock
    private DebtPositionTypeOrgApi debtPositionTypeOrgApiMock;
    @Mock
    private DebtPositionTypeOrgEntityControllerApi debtPositionTypeOrgEntityApiMock;
    @Mock
    private DebtPositionTypeOrgSearchControllerApi debtPositionTypeOrgSearchApiMock;

    private DebtPositionTypeOrgClient debtPositionTypeOrgClient;

    @BeforeEach
    void setUp() {
        debtPositionTypeOrgClient = new DebtPositionTypeOrgClient(debtPositionApisHolderMock);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(
                debtPositionApisHolderMock
        );
    }

    @Test
    void whenGetIONotificationDetailsThenInvokeWithAccessToken(){
        // Given
        String accessToken = "ACCESSTOKEN";
        IONotificationDTO expectedResult = buildIONotificationDTO();

        Mockito.when(debtPositionApisHolderMock.getDebtPositionTypeOrgApi(accessToken))
                .thenReturn(debtPositionTypeOrgApiMock);

        Mockito.when(debtPositionTypeOrgApiMock.getIONotificationDetails(1L, it.gov.pagopa.pu.debtposition.dto.generated.PaymentEventType.DP_CREATED))
                .thenReturn(expectedResult);

        // When
        IONotificationDTO result = debtPositionTypeOrgClient.getIONotificationDetails(1L, PaymentEventType.DP_CREATED, accessToken);

        // Then
        Assertions.assertSame(expectedResult, result);
    }

    @Test
    void givenNotExistentDebtPositionTypeOrgWhenGetIONotificationDetailsThenNull(){
        // Given
        String accessToken = "ACCESSTOKEN";
        Long debtPositionTypeOrgId = 0L;

        Mockito.when(debtPositionApisHolderMock.getDebtPositionTypeOrgApi(accessToken))
                .thenReturn(debtPositionTypeOrgApiMock);
        Mockito.when(debtPositionTypeOrgApiMock.getIONotificationDetails(debtPositionTypeOrgId, it.gov.pagopa.pu.debtposition.dto.generated.PaymentEventType.DP_CREATED))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));

        // When
        IONotificationDTO result = debtPositionTypeOrgClient.getIONotificationDetails(debtPositionTypeOrgId, PaymentEventType.DP_CREATED, accessToken);

        // Then
        Assertions.assertNull(result);
    }

    @Test
    void whenFindByIdThenInvokeWithAccessToken(){
        // Given
        String accessToken = "ACCESSTOKEN";
        Long debtPositionTypeOrgId = 0L;
        DebtPositionTypeOrg expectedResult = new DebtPositionTypeOrg();

        Mockito.when(debtPositionApisHolderMock.getDebtPositionTypeOrgEntityApi(accessToken))
                .thenReturn(debtPositionTypeOrgEntityApiMock);
        Mockito.when(debtPositionTypeOrgEntityApiMock.crudGetDebtpositiontypeorg(debtPositionTypeOrgId+""))
                .thenReturn(expectedResult);

        // When
        DebtPositionTypeOrg result = debtPositionTypeOrgClient.findById(debtPositionTypeOrgId, accessToken);

        // Then
        Assertions.assertSame(expectedResult, result);
    }

    @Test
    void givenNotExistentDebtPositionTypeOrgWhenFindByIdThenNull(){
        // Given
        String accessToken = "ACCESSTOKEN";
        Long debtPositionTypeOrgId = 0L;

        Mockito.when(debtPositionApisHolderMock.getDebtPositionTypeOrgEntityApi(accessToken))
                .thenReturn(debtPositionTypeOrgEntityApiMock);
        Mockito.when(debtPositionTypeOrgEntityApiMock.crudGetDebtpositiontypeorg(debtPositionTypeOrgId+""))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));

        // When
        DebtPositionTypeOrg result = debtPositionTypeOrgClient.findById(debtPositionTypeOrgId, accessToken);

        // Then
        Assertions.assertNull(result);
    }

	@Test
	void whenGetDebtPositionTypeOrgByInstallmentIdInvokeWithAccessToken() {
        // Given
        String accessToken = "ACCESSTOKEN";
        Long installmentId = 0L;
        DebtPositionTypeOrg expectedResult = new DebtPositionTypeOrg();

        Mockito.when(debtPositionApisHolderMock.getDebtPositionTypeOrgSearchControllerApi(accessToken))
                .thenReturn(debtPositionTypeOrgSearchApiMock);
        Mockito.when(debtPositionTypeOrgSearchApiMock.crudDebtPositionTypeOrgsGetDebtPositionTypeOrgByInstallmentId(installmentId))
                .thenReturn(expectedResult);

        // When
        DebtPositionTypeOrg result = debtPositionTypeOrgClient.getDebtPositionTypeOrgByInstallmentId(installmentId, accessToken);

        // Then
        Assertions.assertSame(expectedResult, result);
	}

    @Test
    void givenNotExistentDebtPositionTypeOrgWhenGetDebtPositionTypeOrgByInstallmentIdThenNull() {
        // Given
        String accessToken = "ACCESSTOKEN";
        Long installmentId = 0L;

        Mockito.when(debtPositionApisHolderMock.getDebtPositionTypeOrgSearchControllerApi(accessToken))
                .thenReturn(debtPositionTypeOrgSearchApiMock);
        Mockito.when(debtPositionTypeOrgSearchApiMock.crudDebtPositionTypeOrgsGetDebtPositionTypeOrgByInstallmentId(installmentId))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));

        // When
        DebtPositionTypeOrg result = debtPositionTypeOrgClient.getDebtPositionTypeOrgByInstallmentId(installmentId, accessToken);

        // Then
        Assertions.assertNull(result);
    }

	@Test
	void whenGetDebtPositionTypeOrgByOrganizationIdAndCodeInvokeWithAccessToken() {
        // Given
        String accessToken = "ACCESSTOKEN";
        Long organizationId = 0L;
        String code = "CODE";
        DebtPositionTypeOrg expectedResult = new DebtPositionTypeOrg();

        Mockito.when(debtPositionApisHolderMock.getDebtPositionTypeOrgSearchControllerApi(accessToken))
                .thenReturn(debtPositionTypeOrgSearchApiMock);
        Mockito.when(debtPositionTypeOrgSearchApiMock.crudDebtPositionTypeOrgsFindByOrganizationIdAndCode(organizationId, code))
                .thenReturn(expectedResult);

        // When
        DebtPositionTypeOrg result = debtPositionTypeOrgClient.getDebtPositionTypeOrgByOrganizationIdAndCode(organizationId, code, accessToken);

        // Then
        Assertions.assertSame(expectedResult, result);
	}

    @Test
    void givenNotExistentDebtPositionTypeOrgWhenGetDebtPositionTypeOrgByOrganizationIdAndCodeThenNull() {
        // Given
        String accessToken = "ACCESSTOKEN";
        Long organizationId = 0L;
        String code = "CODE";

        Mockito.when(debtPositionApisHolderMock.getDebtPositionTypeOrgSearchControllerApi(accessToken))
                .thenReturn(debtPositionTypeOrgSearchApiMock);
        Mockito.when(debtPositionTypeOrgSearchApiMock.crudDebtPositionTypeOrgsFindByOrganizationIdAndCode(organizationId, code))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));

        // When
        DebtPositionTypeOrg result = debtPositionTypeOrgClient.getDebtPositionTypeOrgByOrganizationIdAndCode(organizationId, code, accessToken);

        // Then
        Assertions.assertNull(result);
    }

    @Test
    void testCreateDebtPositionTypeOrg() {
        // Given
        String accessToken = "accessToken";
        DebtPositionTypeOrg expectedDebtPositionType = new DebtPositionTypeOrg();
        DebtPositionTypeOrgRequestBody requestBody = new DebtPositionTypeOrgRequestBody();

        Mockito.when(debtPositionApisHolderMock.getDebtPositionTypeOrgEntityApi(accessToken))
                .thenReturn(debtPositionTypeOrgEntityApiMock);
        Mockito.when(debtPositionTypeOrgEntityApiMock.crudCreateDebtpositiontypeorg(requestBody))
                .thenReturn(expectedDebtPositionType);

        // When
        DebtPositionTypeOrg result = debtPositionTypeOrgClient.createDebtPositionTypeOrg(requestBody, accessToken);

        // Then
        Assertions.assertSame(expectedDebtPositionType, result);
    }


}
