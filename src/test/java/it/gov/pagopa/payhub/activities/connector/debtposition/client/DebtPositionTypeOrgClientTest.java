package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.payhub.activities.exception.common.RestInvokeNotFoundException;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.debtpositions.client.generated.*;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionTypeOrg;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionTypeOrgRequestBody;
import it.gov.pagopa.pu.debtpositions.dto.generated.IONotificationDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.SpontaneousForm;
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
import uk.co.jemos.podam.api.PodamFactory;

import static it.gov.pagopa.payhub.activities.util.faker.IONotificationDTOFaker.buildIONotificationDTO;
import static org.mockito.Mockito.when;

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
    @Mock
    private SpontaneousFormApi spontaneousFormApiMock;
    @Mock
    private SpontaneousFormSearchControllerApi spontaneousFormSearchApiMock;

    private DebtPositionTypeOrgClient debtPositionTypeOrgClient;

    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

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

        when(debtPositionApisHolderMock.getDebtPositionTypeOrgApi(accessToken))
            .thenReturn(debtPositionTypeOrgApiMock);

        when(debtPositionTypeOrgApiMock.getIONotificationDetails(1L, it.gov.pagopa.pu.debtpositions.dto.generated.PaymentEventType.DP_CREATED))
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

        when(debtPositionApisHolderMock.getDebtPositionTypeOrgApi(accessToken))
            .thenReturn(debtPositionTypeOrgApiMock);
        when(debtPositionTypeOrgApiMock.getIONotificationDetails(debtPositionTypeOrgId, it.gov.pagopa.pu.debtpositions.dto.generated.PaymentEventType.DP_CREATED))
            .thenThrow(new RestInvokeNotFoundException("APPNAME", HttpStatus.NOT_FOUND, "ERROR", "ERRORCODE", "ERRORMESSAGE"));

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

        when(debtPositionApisHolderMock.getDebtPositionTypeOrgEntityApi(accessToken))
            .thenReturn(debtPositionTypeOrgEntityApiMock);
        when(debtPositionTypeOrgEntityApiMock.crudGetDebtpositiontypeorg(debtPositionTypeOrgId+""))
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

        when(debtPositionApisHolderMock.getDebtPositionTypeOrgEntityApi(accessToken))
            .thenReturn(debtPositionTypeOrgEntityApiMock);
        when(debtPositionTypeOrgEntityApiMock.crudGetDebtpositiontypeorg(debtPositionTypeOrgId+""))
            .thenThrow(new RestInvokeNotFoundException("APPNAME", HttpStatus.NOT_FOUND, "ERROR", "ERRORCODE", "ERRORMESSAGE"));

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

        when(debtPositionApisHolderMock.getDebtPositionTypeOrgSearchControllerApi(accessToken))
            .thenReturn(debtPositionTypeOrgSearchApiMock);
        when(debtPositionTypeOrgSearchApiMock.crudDebtPositionTypeOrgsGetDebtPositionTypeOrgByInstallmentId(installmentId))
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

        when(debtPositionApisHolderMock.getDebtPositionTypeOrgSearchControllerApi(accessToken))
            .thenReturn(debtPositionTypeOrgSearchApiMock);
        when(debtPositionTypeOrgSearchApiMock.crudDebtPositionTypeOrgsGetDebtPositionTypeOrgByInstallmentId(installmentId))
            .thenThrow(new RestInvokeNotFoundException("APPNAME", HttpStatus.NOT_FOUND, "ERROR", "ERRORCODE", "ERRORMESSAGE"));

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

        when(debtPositionApisHolderMock.getDebtPositionTypeOrgSearchControllerApi(accessToken))
            .thenReturn(debtPositionTypeOrgSearchApiMock);
        when(debtPositionTypeOrgSearchApiMock.crudDebtPositionTypeOrgsFindByOrganizationIdAndCode(organizationId, code))
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

        when(debtPositionApisHolderMock.getDebtPositionTypeOrgSearchControllerApi(accessToken))
            .thenReturn(debtPositionTypeOrgSearchApiMock);
        when(debtPositionTypeOrgSearchApiMock.crudDebtPositionTypeOrgsFindByOrganizationIdAndCode(organizationId, code))
            .thenThrow(new RestInvokeNotFoundException("APPNAME", HttpStatus.NOT_FOUND, "ERROR", "ERRORCODE", "ERRORMESSAGE"));

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

        when(debtPositionApisHolderMock.getDebtPositionTypeOrgEntityApi(accessToken))
            .thenReturn(debtPositionTypeOrgEntityApiMock);
        when(debtPositionTypeOrgEntityApiMock.crudCreateDebtpositiontypeorg(requestBody))
            .thenReturn(expectedDebtPositionType);

        // When
        DebtPositionTypeOrg result = debtPositionTypeOrgClient.createDebtPositionTypeOrg(requestBody, accessToken);

        // Then
        Assertions.assertSame(expectedDebtPositionType, result);
    }

    @Test
    void whenFindSpontaneousFormByOrganizationIdAndCodeThenInvokeWithAccessToken() {
        // Given
        String accessToken = "ACCESSTOKEN";
        Long organizationId = 1L;
        String code = "SF_CODE";
        SpontaneousForm expectedResult = podamFactory.manufacturePojo(SpontaneousForm.class);
        expectedResult.setOrganizationId(organizationId);
        expectedResult.setCode(code);

        when(debtPositionApisHolderMock.getSpontaneousFormSearchControllerApi(accessToken))
            .thenReturn(spontaneousFormSearchApiMock);
        when(spontaneousFormSearchApiMock.crudSpontaneousFormsFindByOrganizationIdAndCode(organizationId, code))
            .thenReturn(expectedResult);

        // When
        SpontaneousForm result = debtPositionTypeOrgClient.findSpontaneousFormByOrganizationIdAndCode(organizationId, code, accessToken);

        // Then
        Assertions.assertSame(expectedResult, result);
    }

    @Test
    void givenNotExistentSpontaneousFormWhenFindByOrganizationIdAndCodeThenReturnNull() {
        // Given
        String accessToken = "ACCESSTOKEN";
        Long organizationId = 1L;
        String code = "SF_CODE_NOT_FOUND";

        when(debtPositionApisHolderMock.getSpontaneousFormSearchControllerApi(accessToken))
            .thenReturn(spontaneousFormSearchApiMock);
        when(spontaneousFormSearchApiMock.crudSpontaneousFormsFindByOrganizationIdAndCode(organizationId, code))
            .thenThrow(new RestInvokeNotFoundException("APPNAME", HttpStatus.NOT_FOUND, "ERROR", "ERRORCODE", "ERRORMESSAGE"));

        // When
        SpontaneousForm result = debtPositionTypeOrgClient.findSpontaneousFormByOrganizationIdAndCode(organizationId, code, accessToken);

        // Then
        Assertions.assertNull(result);
    }

    @Test
    void whenCreateSpontaneousFormThenInvokeWithAccessToken() {
        // Given
        String accessToken = "ACCESSTOKEN";
        SpontaneousForm formToCreate = new SpontaneousForm();
        SpontaneousForm createdForm = new SpontaneousForm();

        when(debtPositionApisHolderMock.getSpontaneousFormApi(accessToken))
            .thenReturn(spontaneousFormApiMock);
        when(spontaneousFormApiMock.createSpontaneousForm(Mockito.same(formToCreate)))
            .thenReturn(createdForm);

        // When
        SpontaneousForm result = debtPositionTypeOrgClient.createSpontaneousForm(formToCreate, accessToken);

        // Then
        Assertions.assertSame(createdForm, result);
    }


}
