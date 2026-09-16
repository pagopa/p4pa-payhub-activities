package it.gov.pagopa.payhub.activities.connector.debtposition.config;

import it.gov.pagopa.payhub.activities.config.json.JsonConfig;
import it.gov.pagopa.payhub.activities.connector.BaseApiHolderTest;
import it.gov.pagopa.pu.debtpositions.dto.generated.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DebtPositionApisHolderTest extends BaseApiHolderTest {
    @Mock
    private RestTemplateBuilder restTemplateBuilderMock;

    private DebtPositionApisHolder apisHolder;
    private DebtPositionApiClientConfig apiClientConfig;

    @BeforeEach
    void setUp() {
        when(restTemplateBuilderMock.build()).thenReturn(restTemplateMock);
        when(restTemplateMock.getUriTemplateHandler()).thenReturn(new DefaultUriBuilderFactory());

        apiClientConfig = DebtPositionApiClientConfig.builder()
            .baseUrl("http://example.com")
                .maxAttempts(3)
            .build();
        apisHolder = new DebtPositionApisHolder(apiClientConfig, restTemplateBuilderMock, new JsonConfig().objectMapperJackson3());

        verifyHttpClientErrorJsonBodyHandlerConfiguration(apisHolder.getInstallmentApi(null));
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
            restTemplateBuilderMock,
            restTemplateMock
        );
    }

    @Test
    void testRetryConfiguration() {
        assertRetry(apiClientConfig,
                accessToken -> apisHolder.getSpontaneousFormSearchControllerApi(accessToken)
                        .crudSpontaneousFormsFindByOrganizationIdAndCode(1L, "code"),
                new ParameterizedTypeReference<>() {}
        );
    }

    @Test
    void whenGetDebtPositionEntityControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
            accessToken -> apisHolder.getDebtPositionEntityControllerApi(accessToken)
                .crudGetDebtposition("0"),
            new ParameterizedTypeReference<>() {},
            apisHolder::unload);
    }

    @Test
    void whenGetDebtPositionApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        SyncStatusUpdateRequestDTO iupdSyncStatusUpdateDTO = new SyncStatusUpdateRequestDTO();
        assertAuthenticationShouldBeSetInThreadSafeMode(
            accessToken -> apisHolder.getDebtPositionApi(accessToken)
                .finalizeSyncStatus(0L, iupdSyncStatusUpdateDTO),
            new ParameterizedTypeReference<>() {},
            apisHolder::unload);
    }

    @Test
    void givenExternalUserIdWhenGetDebtPositionApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        SyncStatusUpdateRequestDTO iupdSyncStatusUpdateDTO = new SyncStatusUpdateRequestDTO();
        assertAuthenticationShouldBeSetInThreadSafeMode(
            (accessToken, userId) -> apisHolder.getDebtPositionApi(accessToken, userId)
                .finalizeSyncStatus(0L, iupdSyncStatusUpdateDTO),
            new ParameterizedTypeReference<>() {},
            apisHolder::unload,
            true);
    }

    @Test
    void whenGetTransferSearchControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
            accessToken -> apisHolder.getTransferSearchControllerApi(accessToken)
                .crudTransfersFindBySemanticKey(0L, "iuv", "iud", 1,
                    Set.of(InstallmentStatus.PAID, InstallmentStatus.REPORTED)),
            new ParameterizedTypeReference<>() {},
            apisHolder::unload);
    }

    @Test
    void whenGetTransferApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
            accessToken -> apisHolder.getTransferApi(accessToken)
                .notifyReportedTransferId(0L, new TransferReportedRequest()),
            new ParameterizedTypeReference<>() {},
            apisHolder::unload);
    }

    @Test
    void whenGetReceiptApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
            accessToken -> apisHolder.getReceiptApi(accessToken)
                .createReceipt(new ReceiptWithAdditionalNodeDataDTO()),
            new ParameterizedTypeReference<>() {},
            apisHolder::unload);
    }

    @Test
    void whenGetDebtPositionTypeOrgApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
            accessToken -> apisHolder.getDebtPositionTypeOrgApi(accessToken)
                .getIONotificationDetails(1L, PaymentEventType.DP_CREATED),
            new ParameterizedTypeReference<>() {},
            apisHolder::unload);
    }

    @Test
    void whenGetDebtPositionTypeOrgEntityApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
            accessToken -> apisHolder.getDebtPositionTypeOrgEntityApi(accessToken)
                .crudGetDebtpositiontypeorg("1"),
            new ParameterizedTypeReference<>() {},
            apisHolder::unload);
    }

    @Test
    void whenGetReceiptNoPiiSearchControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
            accessToken -> apisHolder.getReceiptNoPiiSearchControllerApi(accessToken)
                .crudReceiptsGetByTransferId(1L),
            new ParameterizedTypeReference<>() {},
            apisHolder::unload);
    }

    @Test
    void whenGetDataExportsApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        Long organizationId = 1L;
        String operatorExternalUserId = "operatorExternalUserId";
        OffsetDateTime paymentDateFrom = OffsetDateTime.now().withOffsetSameInstant(ZoneOffset.UTC);
        OffsetDateTime paymentDateTo = OffsetDateTime.now().plusMonths(1).withOffsetSameInstant(ZoneOffset.UTC);
        Long debtPositionTypeOrgId = 1L;

        assertAuthenticationShouldBeSetInThreadSafeMode(
            accessToken -> apisHolder.getDataExportsApi(accessToken)
                .exportPaidInstallments(organizationId, operatorExternalUserId, paymentDateFrom, paymentDateTo, null, null, debtPositionTypeOrgId, null, 0, 10, null),
            new ParameterizedTypeReference<>() {},
            apisHolder::unload);
    }

    @Test
    void whenGetDebtPositionTypeOrgSearchControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
            accessToken -> apisHolder.getDebtPositionTypeOrgSearchControllerApi(accessToken)
                .crudDebtPositionTypeOrgsGetDebtPositionTypeOrgByInstallmentId(1L),
            new ParameterizedTypeReference<>() {},
            apisHolder::unload);
    }

    @Test
    void whenGetInstallmentNoPiiEntityControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
            accessToken -> apisHolder.getInstallmentNoPiiEntityControllerApi(accessToken)
                .crudGetInstallmentnopii("1"),
            new ParameterizedTypeReference<>() {},
            apisHolder::unload);
    }

    @Test
    void whenGetInstallmentNoPiiSearchControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
            accessToken ->
                apisHolder.getInstallmentNoPiiSearchControllerApi(accessToken)
                    .crudInstallmentsGetByOrganizationIdAndReceiptId(0L, 1L, List.of()),
            new ParameterizedTypeReference<>() {},
            apisHolder::unload
        );
    }

    @Test
    void whenGetInstallmentsEntityExtendedControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
            accessToken -> {
                apisHolder.getInstallmentsEntityExtendedControllerApi(accessToken)
                    .updateDueDate(1L, LocalDate.now());
                return voidMock;
            },
            new ParameterizedTypeReference<>() {},
            apisHolder::unload
        );
    }

    @Test
    void whenGetPaymentOptionEntityExtendedControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
            accessToken -> {
                apisHolder.getPaymentOptionEntityExtendedControllerApi(accessToken)
                    .updateStatus(1L, PaymentOptionStatus.PAID);
                return voidMock;
            },
            new ParameterizedTypeReference<>() {},
            apisHolder::unload
        );
    }

    @Test
    void whenGetInstallmentApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
            accessToken -> apisHolder.getInstallmentApi(accessToken)
                .getInstallmentsByOrganizationIdAndNav(0L, "nav", null),
            new ParameterizedTypeReference<>() {},
            apisHolder::unload);
    }

    @Test
    void whenGetDebtPositionTypeEntityControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
            accessToken -> apisHolder.getDebtPositionTypeEntityControllerApi(accessToken)
                .crudCreateDebtpositiontype(new DebtPositionTypeRequestBody()),
            new ParameterizedTypeReference<>() {},
            apisHolder::unload);
    }

    @Test
    void whenGetDebtPositionTypeSearchControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
            accessToken -> apisHolder.getDebtPositionTypeSearchControllerApi(accessToken)
                .crudDebtPositionTypesFindByMainFields(
                    "debtPositionTypeCode",
                    1L,
                    "orgType",
                    "macroArea",
                    null,
                    null,
                    null),
            new ParameterizedTypeReference<>() {},
            apisHolder::unload);
    }

    @Test
    void whenGetDebtPositionSearchControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
            accessToken -> apisHolder.getDebtPositionSearchControllerApi(accessToken)
                .crudDebtPositionsFindByInstallmentId(1L),
            new ParameterizedTypeReference<>() {},
            apisHolder::unload);
    }

    @Test
    void whenGetSpontaneousFormApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
            accessToken -> apisHolder.getSpontaneousFormApi(accessToken)
                .createSpontaneousForm(new SpontaneousForm()),
            new ParameterizedTypeReference<>() {},
            apisHolder::unload);
    }

    @Test
    void whenGetSpontaneousFormSearchControllerApiThenAuthenticationShouldBeSetInThreadSafe() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
            accessToken -> apisHolder.getSpontaneousFormSearchControllerApi(accessToken)
                .crudSpontaneousFormsFindByOrganizationIdAndCode(1L, "code"),
            new ParameterizedTypeReference<>() {},
            apisHolder::unload);
    }

    @Test
    void whenGetDebtPositionIdViewSearchControllerApiThenAuthenticationShouldBeSetInThreadSafe() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> apisHolder.getDebtPositionIdViewSearchControllerApi(accessToken)
                        .crudDebtPositionIdViewGetDebtPositionIdsByIbansAndDptoId(1L, "iban", true, Collections.emptyList(), "postalIban", 1L, 0, 10, Collections.emptyList()),
                new ParameterizedTypeReference<>() {},
                apisHolder::unload);
    }
}
