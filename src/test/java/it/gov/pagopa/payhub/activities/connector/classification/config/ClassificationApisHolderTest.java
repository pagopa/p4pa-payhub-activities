package it.gov.pagopa.payhub.activities.connector.classification.config;

import it.gov.pagopa.payhub.activities.config.json.JsonConfig;
import it.gov.pagopa.payhub.activities.connector.BaseApiHolderTest;
import it.gov.pagopa.pu.classification.dto.generated.*;
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
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClassificationApisHolderTest extends BaseApiHolderTest {

    @Mock
    private RestTemplateBuilder restTemplateBuilderMock;

    private ClassificationApisHolder apisHolder;
    private ClassificationApiClientConfig apiClientConfig;

    @BeforeEach
    void setUp() {
        when(restTemplateBuilderMock.build()).thenReturn(restTemplateMock);
        when(restTemplateMock.getUriTemplateHandler()).thenReturn(new DefaultUriBuilderFactory());

        apiClientConfig = ClassificationApiClientConfig.builder()
                .baseUrl("http://example.com")
                .maxAttempts(3)
                .build();
        apisHolder = new ClassificationApisHolder(apiClientConfig, restTemplateBuilderMock, new JsonConfig().objectMapperJackson3());

        verifyHttpClientErrorJsonBodyHandlerConfiguration(apisHolder.getAssessmentsControllerApi(null));
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
                accessToken -> apisHolder.getClassificationSearchControllerApi(accessToken)
                        .crudClassificationsFindAllByOrganizationIdAndIuvAndIud(1L, "iuv", "iud"),
                new ParameterizedTypeReference<>() {}
        );
    }

//region Classification entity
    @Test
    void whenGetClassificationEntityExtendedControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> apisHolder.getClassificationEntityExtendedControllerApi(accessToken)
                            .saveAll2(List.of(new Classification())),
                new ParameterizedTypeReference<>() {},
                apisHolder::unload);
    }
//endregion

//region PaymentsReporting entity
    @Test
    void whenGetPaymentsReportingSearchApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> apisHolder.getPaymentsReportingSearchApi(accessToken)
                            .crudPaymentsReportingFindByOrganizationIdAndIuf(1L, "iuf"),
                new ParameterizedTypeReference<>() {},
                apisHolder::unload);
    }

    @Test
    void whenGetPaymentsReportingEntityControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> apisHolder.getPaymentsReportingEntityControllerApi(accessToken)
                            .crudCreatePaymentsreporting(new PaymentsReportingRequestBody()),
                new ParameterizedTypeReference<>() {},
                apisHolder::unload);
    }

    @Test
    void whenGetPaymentsReportingEntityExtendedControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> apisHolder.getPaymentsReportingEntityExtendedControllerApi(accessToken)
                            .saveAll1(List.of(new PaymentsReporting())),
                new ParameterizedTypeReference<>() {},
                apisHolder::unload);
    }

    @Test
    void whenGetPaymentsReportingApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> apisHolder.getPaymentReportingApi(accessToken)
                        .findAndDeleteByOrgIdAndIufAndIngestionFlowFileIdNot(1L, "IUF", 100L),
                new ParameterizedTypeReference<>() {},
                apisHolder::unload);
    }
//endregion

//region Treasury entity
    @Test
    void whenGetTreasurySearchApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> apisHolder.getTreasurySearchApi(accessToken)
                            .crudTreasuryGetByOrganizationIdAndIuf(1L, "iuf"),
                new ParameterizedTypeReference<>() {},
                apisHolder::unload);
    }
    @Test
    void whenGetTreasuryEntityControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> apisHolder.getTreasuryEntityControllerApi(accessToken)
                            .crudCreateTreasury(new TreasuryRequestBody()),
                new ParameterizedTypeReference<>() {},
                apisHolder::unload);
    }
    @Test
    void whenGetTreasuryEntityExtendedControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> apisHolder.getTreasuryEntityExtendedControllerApi(accessToken)
                            .deleteByOrganizationIdAndBillCodeAndBillYearAndOrgBtCodeAndOrgIstatCode(1L, "billCode", "2021", "btCode", "istatCode"),
                new ParameterizedTypeReference<>() {},
                apisHolder::unload);
    }
//endregion

//region Assessments entity
    @Test
    void whenGetAssessmentsControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> apisHolder.getAssessmentsControllerApi(accessToken)
                            .createAssessmentByReceiptId(1L),
                new ParameterizedTypeReference<>() {},
                apisHolder::unload);
    }

@Test
    void whenGetAssessmentsSearchControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> apisHolder.getAssessmentsSearchControllerApi(accessToken)
                            .crudAssessmentsFindByOrganizationIdAndDebtPositionTypeOrgCodeAndAssessmentName(1L,"debtPositionTypeOrgCode", "assessmentName"),
                new ParameterizedTypeReference<>() {},
                apisHolder::unload);
    }

@Test
    void whenGetAssessmentsEntityControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> apisHolder.getAssessmentsEntityControllerApi(accessToken)
                            .crudCreateAssessments(new AssessmentsRequestBody()),
                new ParameterizedTypeReference<>() {},
                apisHolder::unload);
    }
//endregion

//region Assessments Detail entity
    @Test
    void whenGetAssessmentsDetailEntityControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> apisHolder.getAssessmentsDetailEntityControllerApi(accessToken)
                        .crudCreateAssessmentsdetail(new AssessmentsDetailRequestBody()),
                new ParameterizedTypeReference<>() {},
                apisHolder::unload);
    }

    @Test
    void whenGetAssessmentsDetailSearchControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> apisHolder.getAssessmentsDetailSearchControllerApi(accessToken)
                        .crudAssessmentsDetailsFindAllByOrganizationIdAndIuvAndIud(1L, "iuv", "iud"),
                new ParameterizedTypeReference<>() {},
                apisHolder::unload);
    }
//endregion

//region Assessments Registry entity
    @Test
    void whenGetAssessmentsRegistryApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
            accessToken -> { apisHolder.getAssessmentsRegistryApi(accessToken)
                .createAssessmentsRegistryByDebtPositionDTOAndIud(new CreateAssessmentsRegistryByDebtPositionDTOAndIudRequest());
                return voidMock;
            },
            new ParameterizedTypeReference<>() {},
            apisHolder::unload);
    }

    @Test
    void getAssessmentsRegistrySearchControllerApiReturnsCorrectInstance() throws InterruptedException {
        Long organizationId = 1L;
        Set<String> debtPositionTypeOrgCodes = Set.of("dptOrgCode1");
        String sectionCode = "a";
        String sectionDescription = "a";
        String officeCode = "a";
        String officeDescription = "a";
        String assessmentCode = "a";
        String assessmentDescription = "a";
        String operatingYear = "2025";
        AssessmentsRegistryStatus status = AssessmentsRegistryStatus.ACTIVE;
        Integer page = 0;
        Integer size = 20;
        List<String> sort = List.of("assessmentCode,asc");

        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken ->  apisHolder.getAssessmentsRegistrySearchControllerApi(accessToken)
                        .crudAssessmentsRegistriesFindAssessmentsRegistriesByFilters(organizationId ,
                                debtPositionTypeOrgCodes, sectionCode, sectionDescription, officeCode, officeDescription,
                                assessmentCode, assessmentDescription, operatingYear, status, page, size, sort)
                ,new ParameterizedTypeReference<>() {},
                apisHolder::unload);
    }


//endregion

//region Payment Notification entity
    @Test
    void whenGetPaymentNotificationApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
            accessToken -> apisHolder.getPaymentNotificationApi(accessToken)
                .createPaymentNotification(new PaymentNotificationDTO()),
            new ParameterizedTypeReference<>() {},
            apisHolder::unload);
    }
//endregion

    @Test
    void whenGetPaymentNotificationNoPiiSearchControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
            accessToken -> apisHolder.getPaymentNotificationNoPiiSearchControllerApi(accessToken)
                .crudPaymentNotificationGetByOrganizationIdAndIud(0L, "IUD"),
            new ParameterizedTypeReference<>() {},
            apisHolder::unload);
    }

    @Test
    void whenGetDataExportControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        String operatorExternalUserId = "operatorExternalUserId";
        List<String> iuf = List.of("IUF");
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
                accessToken -> apisHolder.getDataExportsApi(accessToken)
                        .exportClassifications(0L,
                                operatorExternalUserId,
                                Set.of(ClassificationsEnum.DOPPI),
                                iuf,
                                iud,
                                List.of(iuv),
                                List.of(iur),
                                localDateFrom,
                                localDateTo,
                                localDateFrom,
                                localDateTo,
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
                                Set.of("code"),
                                0,
                                0,
                                List.of("classificationId")
                                ),
                new ParameterizedTypeReference<>() {},
                apisHolder::unload);
    }

//region Classification entity
    @Test
    void whenGetClassificationSearchControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> apisHolder.getClassificationSearchControllerApi(accessToken)
                        .crudClassificationsFindAllByOrganizationIdAndIuvAndIud(1L, "iuv", "iud"),
                new ParameterizedTypeReference<>() {},
                apisHolder::unload);
    }
//endregion
}