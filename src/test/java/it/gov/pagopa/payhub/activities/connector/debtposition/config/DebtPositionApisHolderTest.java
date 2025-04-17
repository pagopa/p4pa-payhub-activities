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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Set;

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
                new ParameterizedTypeReference<>() {},
                debtPositionApisHolder::unload);
    }

    @Test
    void whenGetDebtPositionApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        IupdSyncStatusUpdateDTO iupdSyncStatusUpdateDTO = IupdSyncStatusUpdateDTO.builder()
                .newStatus(InstallmentStatus.TO_SYNC)
                .build();
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> debtPositionApisHolder.getDebtPositionApi(accessToken)
                        .finalizeSyncStatus(0L, Map.of("iud", iupdSyncStatusUpdateDTO)),
                new ParameterizedTypeReference<>() {},
                debtPositionApisHolder::unload);
    }

    @Test
    void givenExternalUserIdWhenGetDebtPositionApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        IupdSyncStatusUpdateDTO iupdSyncStatusUpdateDTO = IupdSyncStatusUpdateDTO.builder()
                .newStatus(InstallmentStatus.TO_SYNC)
                .build();
        assertAuthenticationShouldBeSetInThreadSafeMode(
                (accessToken, userId) -> debtPositionApisHolder.getDebtPositionApi(accessToken, userId)
                        .finalizeSyncStatus(0L, Map.of("iud", iupdSyncStatusUpdateDTO)),
                new ParameterizedTypeReference<>() {},
                debtPositionApisHolder::unload,
                true);
    }

    @Test
    void whenGetTransferSearchControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
            accessToken -> debtPositionApisHolder.getTransferSearchControllerApi(accessToken)
                .crudTransfersFindBySemanticKey(0L, "iuv", "iud", 1,
                    Set.of(InstallmentStatus.PAID, InstallmentStatus.REPORTED)),
            new ParameterizedTypeReference<>() {},
            debtPositionApisHolder::unload);
    }

    @Test
    void whenGetTransferApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
            accessToken -> debtPositionApisHolder.getTransferApi(accessToken)
                .notifyReportedTransferId(0L, new TransferReportedRequest()),
            new ParameterizedTypeReference<>() {},
            debtPositionApisHolder::unload);
    }

    @Test
    void whenGetReceiptApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
            accessToken -> debtPositionApisHolder.getReceiptApi(accessToken)
              .createReceipt(new ReceiptWithAdditionalNodeDataDTO()),
            new ParameterizedTypeReference<>() {},
            debtPositionApisHolder::unload);
    }

    @Test
    void whenGetDebtPositionTypeOrgApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> debtPositionApisHolder.getDebtPositionTypeOrgApi(accessToken)
                        .getIONotificationDetails(1L, PaymentEventType.DP_CREATED),
                new ParameterizedTypeReference<>() {},
                debtPositionApisHolder::unload);
    }

    @Test
    void whenGetDebtPositionTypeOrgEntityApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> debtPositionApisHolder.getDebtPositionTypeOrgEntityApi(accessToken)
                        .crudGetDebtpositiontypeorg("1"),
                new ParameterizedTypeReference<>() {},
                debtPositionApisHolder::unload);
    }

    @Test
    void whenGetReceiptNoPiiSearchControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> debtPositionApisHolder.getReceiptNoPiiSearchControllerApi(accessToken)
                        .crudReceiptsGetByTransferId(1L),
                new ParameterizedTypeReference<>() {},
                debtPositionApisHolder::unload);
    }

    @Test
    void whenGetDataExportsApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        Long organizationId = 1L;
        String operatorExternalUserId = "operatorExternalUserId";
        OffsetDateTime paymentDateFrom = OffsetDateTime.now().withOffsetSameInstant(ZoneOffset.UTC);
        OffsetDateTime paymentDateTo = OffsetDateTime.now().plusMonths(1).withOffsetSameInstant(ZoneOffset.UTC);
        Long debtPositionTypeOrgId = 1L;

        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> debtPositionApisHolder.getDataExportsApi(accessToken)
                        .exportPaidInstallments(organizationId, operatorExternalUserId, paymentDateFrom, paymentDateTo, debtPositionTypeOrgId, 0, 10, null),
                new ParameterizedTypeReference<>() {},
                debtPositionApisHolder::unload);
    }

    @Test
    void whenGetDebtPositionTypeOrgSearchControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> debtPositionApisHolder.getDebtPositionTypeOrgSearchControllerApi(accessToken)
                        .crudDebtPositionTypeOrgsGetDebtPositionTypeOrgByInstallmentId(1L),
                new ParameterizedTypeReference<>() {},
                debtPositionApisHolder::unload);
    }

    @Test
    void whenGetInstallmentNoPiiEntityControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> debtPositionApisHolder.getInstallmentNoPiiEntityControllerApi(accessToken)
                        .crudGetInstallmentnopii("1"),
                new ParameterizedTypeReference<>() {},
                debtPositionApisHolder::unload);
    }

    @Test
    void whenGetInstallmentNoPiiSearchControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken ->
                    debtPositionApisHolder.getInstallmentNoPiiSearchControllerApi(accessToken)
                            .crudInstallmentsFindByReceiptId(1L),
                new ParameterizedTypeReference<>() {},
                debtPositionApisHolder::unload
        );
    }

    @Test
    void whenGetInstallmentsEntityExtendedControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> {
                    debtPositionApisHolder.getInstallmentsEntityExtendedControllerApi(accessToken)
                            .updateDueDate(1L, LocalDate.now());
                    return voidMock;
                },
                new ParameterizedTypeReference<>() {},
                debtPositionApisHolder::unload
        );
    }

    @Test
    void whenGetPaymentOptionEntityExtendedControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> {
                    debtPositionApisHolder.getPaymentOptionEntityExtendedControllerApi(accessToken)
                            .updateStatus(1L, PaymentOptionStatus.PAID);
                    return voidMock;
                },
                new ParameterizedTypeReference<>() {},
                debtPositionApisHolder::unload
        );
    }

    @Test
    void whenGetPaymentOptionSearchControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> {
                    debtPositionApisHolder.getPaymentOptionSearchControllerApi(accessToken)
                            .crudPaymentOptionsUpdateStatus(1L, PaymentOptionStatus.UNPAYABLE);
                    return voidMock;
                },
                new ParameterizedTypeReference<>() {},
                debtPositionApisHolder::unload
        );
    }

    @Test
    void whenGetInstallmentApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> debtPositionApisHolder.getInstallmentApi(accessToken)
                        .getInstallmentsByOrganizationIdAndNav(0L, "nav", null),
                new ParameterizedTypeReference<>() {},
                debtPositionApisHolder::unload);
    }
}
