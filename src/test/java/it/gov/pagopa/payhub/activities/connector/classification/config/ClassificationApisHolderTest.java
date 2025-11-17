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
import java.util.Set;

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
                            .deleteByOrganizationIdAndBillCodeAndBillYearAndOrgBtCodeAndOrgIstatCode(1L, "billCode", "2021", "btCode", "istatCode"),
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

@Test
    void whenGetAssessmentsSearchControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> classificationApisHolder.getAssessmentsSearchControllerApi(accessToken)
                            .crudAssessmentsFindByOrganizationIdAndDebtPositionTypeOrgCodeAndAssessmentName(1L,"debtPositionTypeOrgCode", "assessmentName"),
                new ParameterizedTypeReference<>() {},
                classificationApisHolder::unload);
    }

@Test
    void whenGetAssessmentsEntityControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> classificationApisHolder.getAssessmentsEntityControllerApi(accessToken)
                            .crudCreateAssessments(new AssessmentsRequestBody()),
                new ParameterizedTypeReference<>() {},
                classificationApisHolder::unload);
    }
//endregion

//region Assessments Detail entity
    @Test
    void whenGetAssessmentsDetailEntityControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> classificationApisHolder.getAssessmentsDetailEntityControllerApi(accessToken)
                        .crudCreateAssessmentsdetail(new AssessmentsDetailRequestBody()),
                new ParameterizedTypeReference<>() {},
                classificationApisHolder::unload);
    }

    @Test
    void whenGetAssessmentsDetailSearchControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> classificationApisHolder.getAssessmentsDetailSearchControllerApi(accessToken)
                        .crudAssessmentsDetailsFindAllByOrganizationIdAndIuvAndIud(1L, "iuv", "iud"),
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
                accessToken ->  classificationApisHolder.getAssessmentsRegistrySearchControllerApi(accessToken)
                        .crudAssessmentsRegistriesFindAssessmentsRegistriesByFilters(organizationId ,
                                debtPositionTypeOrgCodes, sectionCode, sectionDescription, officeCode, officeDescription,
                                assessmentCode, assessmentDescription, operatingYear, status, page, size, sort)
                ,new ParameterizedTypeReference<>() {},
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
                accessToken -> classificationApisHolder.getDataExportsApi(accessToken)
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
                classificationApisHolder::unload);
    }

//region Classification entity
    @Test
    void whenGetClassificationSearchControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> classificationApisHolder.getClassificationSearchControllerApi(accessToken)
                        .crudClassificationsFindAllByOrganizationIdAndIuvAndIud(1L, "iuv", "iud"),
                new ParameterizedTypeReference<>() {},
                classificationApisHolder::unload);
    }
//endregion

}