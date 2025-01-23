package it.gov.pagopa.payhub.activities.connector.debtposition.config;

import it.gov.pagopa.payhub.activities.connector.BaseApiHolderTest;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPosition;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.IupdSyncStatusUpdateDTO;
import it.gov.pagopa.pu.debtposition.generated.ApiClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
class DebtPositionApisHolderTest extends BaseApiHolderTest {
    @Mock
    private RestTemplateBuilder restTemplateBuilderMock;

    private DebtPositionApisHolder debtPositionApisHolder;

    @BeforeEach
    void setUp() {
        Mockito.when(restTemplateBuilderMock.build()).thenReturn(restTemplateMock);
        Mockito.when(restTemplateMock.getUriTemplateHandler()).thenReturn(new DefaultUriBuilderFactory());
        ApiClient apiClient = new ApiClient(restTemplateMock);
        String baseUrl = "http://example.com";
        apiClient.setBasePath(baseUrl);
        debtPositionApisHolder = new DebtPositionApisHolder(baseUrl, restTemplateBuilderMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                restTemplateBuilderMock,
                restTemplateMock
        );
    }

    @Test
    void whenGetDebtPositionSearchControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> debtPositionApisHolder.getDebtPositionSearchControllerApi(accessToken)
                        .crudDebtPositionsFindOneWithAllDataByDebtPositionId(0L),
                DebtPosition.class,
                debtPositionApisHolder::unload);
    }

    @Test
    void whenGetDebtPositionApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        IupdSyncStatusUpdateDTO iupdSyncStatusUpdateDTO = IupdSyncStatusUpdateDTO.builder()
                .iupdPagopa("iudpPagopa")
                .newStatus(IupdSyncStatusUpdateDTO.NewStatusEnum.TO_SYNC)
                .build();
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> debtPositionApisHolder.getDebtPositionApi(accessToken)
                        .finalizeSyncStatus(0L, Map.of("iud", iupdSyncStatusUpdateDTO)),
                DebtPositionDTO.class,
                debtPositionApisHolder::unload);
    }

}
