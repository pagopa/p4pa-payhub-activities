package it.gov.pagopa.payhub.activities.connector.debtposition.config;

import it.gov.pagopa.payhub.activities.connector.BaseApiHolderTest;
import it.gov.pagopa.pu.debtposition.dto.generated.*;
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
import java.util.Set;

import static it.gov.pagopa.pu.debtposition.dto.generated.InstallmentNoPII.*;

@ExtendWith(MockitoExtension.class)
class DebtPositionApisHolderTest extends BaseApiHolderTest {
    @Mock
    private RestTemplateBuilder restTemplateBuilderMock;

    private DebtPositionApisHolder debtPositionApisHolder;

    @BeforeEach
    void setUp() {
        Mockito.when(restTemplateBuilderMock.build()).thenReturn(restTemplateMock);
        Mockito.when(restTemplateMock.getUriTemplateHandler()).thenReturn(new DefaultUriBuilderFactory());
        DebtPositionApiClientConfig clientConfig = DebtPositionApiClientConfig.builder()
                .baseUrl("http://example.com")
                .build();
        debtPositionApisHolder = new DebtPositionApisHolder(clientConfig, restTemplateBuilderMock);
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

    @Test
    void whenGetTransferSearchControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
            accessToken -> debtPositionApisHolder.getTransferSearchControllerApi(accessToken)
                .crudTransfersFindBySemanticKey(0L, "iuv", "iud", 1,
                    Set.of(StatusEnum.PAID.getValue(), StatusEnum.REPORTED.getValue())),
            Object.class,
            debtPositionApisHolder::unload);
    }

    @Test
    void whenGetTransferApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
            accessToken -> debtPositionApisHolder.getTransferApi(accessToken)
                .notifyReportedTransferId(0L),
            Object.class,
            debtPositionApisHolder::unload);
    }

    @Test
    void whenGetReceiptApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
            accessToken -> debtPositionApisHolder.getReceiptApi(accessToken)
              .createReceipt(new ReceiptWithAdditionalNodeDataDTO()),
            ReceiptDTO.class,
            debtPositionApisHolder::unload);
    }
}
