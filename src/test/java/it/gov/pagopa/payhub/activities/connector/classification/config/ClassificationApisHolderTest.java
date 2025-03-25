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
}