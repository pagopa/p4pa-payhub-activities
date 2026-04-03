package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.payhub.activities.connector.workflowhub.dto.WfExecutionParameters;
import it.gov.pagopa.payhub.activities.dto.debtposition.DebtPositionIdViewFilters;
import it.gov.pagopa.pu.debtposition.client.generated.DebtPositionApi;
import it.gov.pagopa.pu.debtposition.client.generated.DebtPositionIdViewSearchControllerApi;
import it.gov.pagopa.pu.debtposition.client.generated.DebtPositionSearchControllerApi;
import it.gov.pagopa.pu.debtposition.dto.generated.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;

import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildDebtPositionDTO;
import static it.gov.pagopa.payhub.activities.util.faker.InstallmentSynchronizeDTOFaker.buildInstallmentSynchronizeDTO;
import static it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionOrigin.ORDINARY_SIL;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DebtPositionClientTest {
    @Mock
    private DebtPositionApisHolder debtPositionApisHolderMock;
    @Mock
    private DebtPositionApi debtPositionApiMock;
    @Mock
    private DebtPositionSearchControllerApi debtPositionSearchControllerApiMock;
    @Mock
    private DebtPositionIdViewSearchControllerApi debtPositionIdViewSearchControllerApiMock;

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
        SyncStatusUpdateRequestDTO requestDTO = new SyncStatusUpdateRequestDTO();
        DebtPositionDTO expectedResult = buildDebtPositionDTO();

        Mockito.when(debtPositionApisHolderMock.getDebtPositionApi(accessToken))
            .thenReturn(debtPositionApiMock);
        Mockito.when(debtPositionApiMock.finalizeSyncStatus(Mockito.same(debtPositionId), Mockito.same(requestDTO)))
            .thenReturn(expectedResult);

        // When
        DebtPositionDTO result = debtPositionClient.finalizeSyncStatus(accessToken, debtPositionId, requestDTO);

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
        Mockito.when(debtPositionApiMock.getDebtPositionsByIngestionFlowFileId(ingestionFlowFileId, null, page, size, null))
            .thenReturn(new PagedDebtPositions());

        // When
        PagedDebtPositions result = debtPositionClient.getDebtPositionsByIngestionFlowFileId(accessToken, ingestionFlowFileId, null, page, size, null);

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
            .nav(Collections.singletonList("nav"))
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

    @Test
    void whenGetDebtPositionThenOk() {
        // Given
        String accessToken = "ACCESSTOKEN";
        Long debtPositionId = 1L;
        DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();

        Mockito.when(debtPositionApisHolderMock.getDebtPositionApi(accessToken))
            .thenReturn(debtPositionApiMock);

        Mockito.when(debtPositionApiMock.getDebtPosition(debtPositionId))
            .thenReturn(debtPositionDTO);

        // When
        DebtPositionDTO result = debtPositionClient.getDebtPosition(accessToken, debtPositionId);

        // Then
        Assertions.assertEquals(debtPositionDTO, result);
    }

    @Test
    void givenNotExistentDebtPositionWhenGetDebtPositionThenNull() {
        // Given
        String accessToken = "ACCESSTOKEN";
        Long debtPositionId = 0L;

        Mockito.when(debtPositionApisHolderMock.getDebtPositionApi(accessToken))
            .thenReturn(debtPositionApiMock);
        Mockito.when(debtPositionApiMock.getDebtPosition(debtPositionId))
            .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));

        // When
        DebtPositionDTO result = debtPositionClient.getDebtPosition(accessToken, debtPositionId);

        // Then
        Assertions.assertNull(result);
    }

    @Test
    void whenGetDebtPositionByInstallmentIdThenOk() {
        // Given
        String accessToken = "ACCESSTOKEN";
        Long installmentId = 1L;
        DebtPosition debtPosition = new DebtPosition();

        Mockito.when(debtPositionApisHolderMock.getDebtPositionSearchControllerApi(accessToken))
            .thenReturn(debtPositionSearchControllerApiMock);

        Mockito.when(debtPositionSearchControllerApiMock.crudDebtPositionsFindByInstallmentId(installmentId))
            .thenReturn(debtPosition);

        // When
        DebtPosition result = debtPositionClient.getDebtPositionByInstallmentId(accessToken, installmentId);

        // Then
        Assertions.assertEquals(debtPosition, result);
    }

    @Test
    void whenGetDebtPositionByInstallmentIdThenNull() {
        // Given
        String accessToken = "ACCESSTOKEN";
        Long installmentId = 1L;

        Mockito.when(debtPositionApisHolderMock.getDebtPositionSearchControllerApi(accessToken))
            .thenReturn(debtPositionSearchControllerApiMock);

        Mockito.when(debtPositionSearchControllerApiMock.crudDebtPositionsFindByInstallmentId(installmentId))
            .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));

        // When
        DebtPosition result = debtPositionClient.getDebtPositionByInstallmentId(accessToken, installmentId);

        // Then
        Assertions.assertNull(result);
    }

    @Test
    void whenUpdateTransferIbansAndSyncDebtPositionThenOk() {
        String accessToken = "ACCESSTOKEN";
        Long debtPositionId = 1L;
        UpdateTransferIbansAndSyncDebtPositionRequestDTO requestDTO = new UpdateTransferIbansAndSyncDebtPositionRequestDTO();

        Mockito.when(debtPositionApisHolderMock.getDebtPositionApi(accessToken)).thenReturn(debtPositionApiMock);
        Mockito.doNothing().when(debtPositionApiMock).updateTransferIbansAndSyncDebtPosition(debtPositionId, requestDTO);

        Assertions.assertDoesNotThrow(() ->
                debtPositionClient.updateTransferIbansAndSyncDebtPosition(debtPositionId, requestDTO, accessToken)
        );
    }

    @Test
    void whenGetDebtPositionsIdViewThenOk() {
        String accessToken = "ACCESSTOKEN";
        DebtPositionIdViewFilters debtPositionIdViewFilters = DebtPositionIdViewFilters.builder()
                .organizationId(1L)
                .iban("iban")
                .syncError(true)
                .installmentStatuses(Collections.emptyList())
                .postalIban("postalIban")
                .dptoId(1L)
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        PagedModelDebtPositionIdView expectedResponse = new PagedModelDebtPositionIdView();

        Mockito.when(debtPositionApisHolderMock.getDebtPositionIdViewSearchControllerApi(accessToken)).thenReturn(debtPositionIdViewSearchControllerApiMock);
        Mockito.when(debtPositionIdViewSearchControllerApiMock.crudDebtPositionIdViewGetDebtPositionIdsByIbansAndDptoId(
                        debtPositionIdViewFilters.getOrganizationId(),
                        debtPositionIdViewFilters.getIban(),
                        debtPositionIdViewFilters.getSyncError(),
                        debtPositionIdViewFilters.getInstallmentStatuses(),
                        debtPositionIdViewFilters.getPostalIban(),
                        debtPositionIdViewFilters.getDptoId(),
                        0,
                        10,
                        Collections.emptyList()
                ))
                .thenReturn(expectedResponse);

        PagedModelDebtPositionIdView result = debtPositionClient.getDebtPositionsIdView(debtPositionIdViewFilters, pageable, accessToken);

        Assertions.assertEquals(expectedResponse, result);
    }
}
