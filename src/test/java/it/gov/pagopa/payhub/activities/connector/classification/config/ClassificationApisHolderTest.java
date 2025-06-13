package it.gov.pagopa.payhub.activities.connector.classification.config;

import it.gov.pagopa.payhub.activities.connector.BaseApiHolderTest;
import it.gov.pagopa.pu.classification.dto.generated.*;
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
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClassificationApisHolderTest extends BaseApiHolderTest {

    @Mock
    private RestTemplateBuilder restTemplateBuilderMock;

    private ClassificationApisHolder classificationApisHolder;

    @BeforeEach
    void setUp() {
        when(restTemplateBuilderMock.build()).thenReturn(restTemplateMock);
        when(restTemplateMock.getUriTemplateHandler()).thenReturn(new DefaultUriBuilderFactory());
        ClassificationApiClientConfig clientConfig = ClassificationApiClientConfig.builder()
                .baseUrl("http://example.com")
                .build();
        classificationApisHolder = new ClassificationApisHolder(clientConfig, restTemplateBuilderMock);
    }
    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                restTemplateBuilderMock,
                restTemplateMock
        );
    }

//region Classification entity
    @Test
    void whenGetClassificationEntityControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> classificationApisHolder.getClassificationEntityControllerApi(accessToken)
                            .crudCreateClassification(new ClassificationRequestBody()),
                new ParameterizedTypeReference<>() {},
                classificationApisHolder::unload);
    }
    @Test
    void whenGetClassificationEntityExtendedControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> classificationApisHolder.getClassificationEntityExtendedControllerApi(accessToken)
                            .saveAll2(List.of(new Classification())),
                new ParameterizedTypeReference<>() {},
                classificationApisHolder::unload);
    }
//endregion

//region PaymentsReporting entity
    @Test
    void whenGetPaymentsReportingSearchApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> classificationApisHolder.getPaymentsReportingSearchApi(accessToken)
                            .crudPaymentsReportingFindByOrganizationIdAndIuf(1L, "iuf"),
                new ParameterizedTypeReference<>() {},
                classificationApisHolder::unload);
    }

    @Test
    void whenGetPaymentsReportingEntityControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> classificationApisHolder.getPaymentsReportingEntityControllerApi(accessToken)
                            .crudCreatePaymentsreporting(new PaymentsReportingRequestBody()),
                new ParameterizedTypeReference<>() {},
                classificationApisHolder::unload);
    }

    @Test
    void whenGetPaymentsReportingEntityExtendedControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> classificationApisHolder.getPaymentsReportingEntityExtendedControllerApi(accessToken)
                            .saveAll1(List.of(new PaymentsReporting())),
                new ParameterizedTypeReference<>() {},
                classificationApisHolder::unload);
    }
//endregion

//region Treasury entity
    @Test
    void whenGetTreasurySearchApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> classificationApisHolder.getTreasurySearchApi(accessToken)
                            .crudTreasuryGetByOrganizationIdAndIuf(1L, "iuf"),
                new ParameterizedTypeReference<>() {},
                classificationApisHolder::unload);
    }
    @Test
    void whenGetTreasuryEntityControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> classificationApisHolder.getTreasuryEntityControllerApi(accessToken)
                            .crudCreateTreasury(new TreasuryRequestBody()),
                new ParameterizedTypeReference<>() {},
                classificationApisHolder::unload);
    }
    @Test
    void whenGetTreasuryEntityExtendedControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> classificationApisHolder.getTreasuryEntityExtendedControllerApi(accessToken)
                            .deleteByOrganizationIdAndBillCodeAndBillYear(1L, "billCode", "2021"),
                new ParameterizedTypeReference<>() {},
                classificationApisHolder::unload);
    }
//endregion

//region Assessments entity
    @Test
    void whenGetAssessmentsControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> classificationApisHolder.getAssessmentsControllerApi(accessToken)
                            .createAssessmentByReceiptId(1L),
                new ParameterizedTypeReference<>() {},
                classificationApisHolder::unload);
    }
//endregion

//region Assessments Registry entity
    @Test
    void whenGetAssessmentsRegistryApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
            accessToken -> { classificationApisHolder.getAssessmentsRegistryApi(accessToken)
                .createAssessmentsRegistryByDebtPositionDTOAndIud(new CreateAssessmentsRegistryByDebtPositionDTOAndIudRequest());
                return voidMock;
            },
            new ParameterizedTypeReference<>() {},
            classificationApisHolder::unload);
    }
//endregion

//region Payment Notification entity
    @Test
    void whenGetPaymentNotificationApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
            accessToken -> classificationApisHolder.getPaymentNotificationApi(accessToken)
                .createPaymentNotification(new PaymentNotificationDTO()),
            new ParameterizedTypeReference<>() {},
            classificationApisHolder::unload);
    }
//endregion

    @Test
    void whenGetPaymentNotificationNoPiiSearchControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
            accessToken -> classificationApisHolder.getPaymentNotificationNoPiiSearchControllerApi(accessToken)
                .crudPaymentNotificationGetByOrganizationIdAndIud(0L, "IUD"),
            new ParameterizedTypeReference<>() {},
            classificationApisHolder::unload);
    }

    @Test
    void whenGetDataExportControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        String operatorExternalUserId = "operatorExternalUserId";
        String iuf = "IUF";
        String iud = "IUD";
        String iuv = "IUV";
        String iur = "IUR";
        OffsetDateTime offsetDateTimeFrom = OffsetDateTime.now().withOffsetSameInstant(ZoneOffset.UTC);
        OffsetDateTime offsetDateTimeTo = OffsetDateTime.now().plusMonths(1).withOffsetSameInstant(ZoneOffset.UTC);
        LocalDate localDateFrom = LocalDate.now();
        LocalDate localDateTo = LocalDate.now().plusMonths(1);
        String regulationUniqueIdentifier = "regulationUniqueIdentifier";
        String accountRegistryCode = "accountRegistryCode";
        String remittanceInformation = "remittanceInformation";
        Long billAmountCents = 100L;
        String pspCompanyName= "PSP_NAME";
        String pspLastName= "PSP_LAST_NAME";
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> classificationApisHolder.getDataExportsApi(accessToken)
                        .exportClassifications(0L,
                                operatorExternalUserId,
                                ClassificationsEnum.DOPPI,
                                localDateFrom,
                                localDateTo,
                                iuf,
                                iud,
                                iuv,
                                iur,
                                offsetDateTimeFrom,
                                offsetDateTimeTo,
                                offsetDateTimeFrom,
                                offsetDateTimeTo,
                                localDateFrom,
                                localDateTo,
                                localDateFrom,
                                localDateTo,
                                localDateFrom,
                                localDateTo,
                                regulationUniqueIdentifier,
                                accountRegistryCode,
                                billAmountCents,
                                remittanceInformation,
                                pspCompanyName,
                                pspLastName,
                                0,
                                0,
                                List.of("classificationId")
                                ),
                new ParameterizedTypeReference<>() {},
                classificationApisHolder::unload);
    }

}