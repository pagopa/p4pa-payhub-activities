package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.payhub.activities.connector.workflowhub.dto.WfExecutionParameters;
import it.gov.pagopa.pu.debtposition.client.generated.DebtPositionApi;
import it.gov.pagopa.pu.debtposition.dto.generated.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;

import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildDebtPositionDTO;
import static it.gov.pagopa.payhub.activities.util.faker.InstallmentSynchronizeDTOFaker.buildInstallmentSynchronizeDTO;
import static it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionOrigin.ORDINARY_SIL;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DebtPositionClientTest {

    @Mock
    private DebtPositionApisHolder debtPositionApisHolderMock;
    @Mock
    private DebtPositionApi debtPositionApiMock;

    private DebtPositionClient debtPositionClient;

    @BeforeEach
    void setUp() {
        debtPositionClient = new DebtPositionClient(debtPositionApisHolderMock);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(
                debtPositionApisHolderMock
        );
    }

    @Test
    void whenFinalizeSyncStatusThenInvokeWithAccessToken(){
        // Given
        String accessToken = "ACCESSTOKEN";
        Long debtPositionId = 0L;
        IupdSyncStatusUpdateDTO iupdSyncStatusUpdateDTO = IupdSyncStatusUpdateDTO.builder()
                .newStatus(InstallmentStatus.TO_SYNC)
                .build();
        DebtPositionDTO expectedResult = buildDebtPositionDTO();

        Mockito.when(debtPositionApisHolderMock.getDebtPositionApi(accessToken))
                .thenReturn(debtPositionApiMock);
        Mockito.when(debtPositionApiMock.finalizeSyncStatus(debtPositionId, Map.of("iud", iupdSyncStatusUpdateDTO) ))
                .thenReturn(expectedResult);

        // When
        DebtPositionDTO result = debtPositionClient.finalizeSyncStatus(accessToken, debtPositionId, Map.of("iud", iupdSyncStatusUpdateDTO) );

        // Then
        Assertions.assertSame(expectedResult, result);
    }

    @Test
    void whenCheckAndUpdateInstallmentExpirationThenOk(){
        // Given
        String accessToken = "ACCESSTOKEN";
        Long debtPositionId = 0L;

        DebtPositionDTO expectedResult = buildDebtPositionDTO();

        Mockito.when(debtPositionApisHolderMock.getDebtPositionApi(accessToken))
                .thenReturn(debtPositionApiMock);
        Mockito.when(debtPositionApiMock.checkAndUpdateInstallmentExpiration(debtPositionId))
                .thenReturn(expectedResult);

        // When
        DebtPositionDTO result = debtPositionClient.checkAndUpdateInstallmentExpiration(accessToken, debtPositionId);

        // Then
        Assertions.assertSame(expectedResult, result);
    }

    @Test
    void whenInstallmentSynchronizeThenOk() {
        // Given
        String accessToken = "ACCESSTOKEN";
        DebtPositionOrigin origin = ORDINARY_SIL;
        InstallmentSynchronizeDTO installmentSynchronizeDTO = buildInstallmentSynchronizeDTO();
        boolean massive = true;
        boolean partialChange = false;
        WfExecutionParameters wfExecutionParameters = WfExecutionParameters.builder()
                .massive(massive)
                .partialChange(partialChange)
                .build();
        String operatorUserId = "USERID";
        String expectedWorkflowId = "workflow-123";

        HttpHeaders headers = new HttpHeaders();
        headers.add("x-workflow-id", expectedWorkflowId);

        Mockito.when(debtPositionApisHolderMock.getDebtPositionApi(accessToken, operatorUserId))
                .thenReturn(debtPositionApiMock);
        Mockito.when(debtPositionApiMock.installmentSynchronizeWithHttpInfo(origin, installmentSynchronizeDTO, massive, partialChange))
                .thenReturn(new ResponseEntity<>(headers, HttpStatus.OK));

        // When
        String result = debtPositionClient.installmentSynchronize(accessToken, origin, installmentSynchronizeDTO, wfExecutionParameters, operatorUserId);

        // Then
        Assertions.assertEquals(expectedWorkflowId, result);
        verify(debtPositionApiMock).installmentSynchronizeWithHttpInfo(origin, installmentSynchronizeDTO, massive, partialChange);
    }

    @Test
    void whenGetDebtPositionsByIngestionFlowFileIdThenOk() {
        // Given
        String accessToken = "ACCESSTOKEN";
        Long ingestionFlowFileId = 1L;
        Integer page = 0;
        Integer size = 2;

        Mockito.when(debtPositionApisHolderMock.getDebtPositionApi(accessToken))
                .thenReturn(debtPositionApiMock);
        Mockito.when(debtPositionApiMock.getDebtPositionsByIngestionFlowFileId(ingestionFlowFileId, page, size, null))
                .thenReturn(new PagedDebtPositions());

        // When
        PagedDebtPositions result = debtPositionClient.getDebtPositionsByIngestionFlowFileId(accessToken, ingestionFlowFileId, page, size, null);

        // Then
        Assertions.assertEquals(new PagedDebtPositions(), result);
    }

    @Test
    void whenUpdateInstallmentNotificationDateThenOk() {
        // Given
        String accessToken = "ACCESSTOKEN";
        String expectedWorkflowId = "workflow-123";
        OffsetDateTime dateTime = OffsetDateTime.now().withOffsetSameInstant(ZoneOffset.UTC);
        UpdateInstallmentNotificationDateRequest request = UpdateInstallmentNotificationDateRequest.builder()
                .debtPositionId(1L)
                .nav("nav")
                .notificationDate(dateTime)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add("x-workflow-id", expectedWorkflowId);

        Mockito.when(debtPositionApisHolderMock.getDebtPositionApi(accessToken))
                .thenReturn(debtPositionApiMock);
        Mockito.when(debtPositionApiMock.updateInstallmentNotificationDateWithHttpInfo(request))
                .thenReturn(new ResponseEntity<>(headers, HttpStatus.OK));

        // When
        String result = debtPositionClient.updateInstallmentNotificationDate(accessToken, request);

        // Then
        Assertions.assertEquals(expectedWorkflowId, result);
        verify(debtPositionApiMock).updateInstallmentNotificationDateWithHttpInfo(request);
    }

}
