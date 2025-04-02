package it.gov.pagopa.payhub.activities.service.ingestionflow.email;

import it.gov.pagopa.payhub.activities.config.EmailTemplatesConfiguration;
import it.gov.pagopa.payhub.activities.dto.email.EmailTemplate;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.IngestionFlowTypeNotSupportedException;
import it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Function;

@ExtendWith(MockitoExtension.class)
class IngestionFlowFileEmailTemplateResolverServiceTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private EmailTemplatesConfiguration emailTemplatesConfigurationMock;

    private IngestionFlowFileEmailTemplateResolverService emailTemplateResolverService;

    @BeforeEach
    void init() {
        this.emailTemplateResolverService = new IngestionFlowFileEmailTemplateResolverService(emailTemplatesConfigurationMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(emailTemplatesConfigurationMock);
    }

    @Test
    void givenUnexpectedIngestionFlowFileTypeWhenResolveThenIngestionFlowTypeNotSupportedException() {
        // Given
        IngestionFlowFile ingestionFlowFileDTO = IngestionFlowFileFaker.buildIngestionFlowFile();
        ingestionFlowFileDTO.setIngestionFlowFileType(IngestionFlowFile.IngestionFlowFileTypeEnum.TREASURY_CSV);

        // When, Then
        Assertions.assertThrows(IngestionFlowTypeNotSupportedException.class, () -> emailTemplateResolverService.resolve(ingestionFlowFileDTO, true));
    }

    void test(
            IngestionFlowFile.IngestionFlowFileTypeEnum flowType,
            Function<EmailTemplatesConfiguration, EmailTemplatesConfiguration.EmailOutcomeBasedTemplates> getFlowTypeOutcomeTemplate,
            boolean success
    ) {
        // Given
        IngestionFlowFile ingestionFlowFileDTO = IngestionFlowFileFaker.buildIngestionFlowFile();
        ingestionFlowFileDTO.setIngestionFlowFileType(flowType);
        EmailTemplate expectedResult = new EmailTemplate();

        EmailTemplatesConfiguration.EmailOutcomeBasedTemplates mockedFlowTypeOutcomeTemplate = getFlowTypeOutcomeTemplate.apply(emailTemplatesConfigurationMock);
        Mockito.when(success
                        ? mockedFlowTypeOutcomeTemplate.getOk()
                        : mockedFlowTypeOutcomeTemplate.getKo())
                .thenReturn(expectedResult);

        // When
        EmailTemplate result = emailTemplateResolverService.resolve(ingestionFlowFileDTO, success);

        // Then
        Assertions.assertSame(expectedResult, result);
        getFlowTypeOutcomeTemplate.apply(Mockito.verify(emailTemplatesConfigurationMock, Mockito.times(2))); // one when configuring mock
    }

    //region PAYMENTS_REPORTING
    @Test
    void givenSuccessfulPaymentsReportingTypeWhenResolveThenOk() {
        test(IngestionFlowFile.IngestionFlowFileTypeEnum.PAYMENTS_REPORTING, EmailTemplatesConfiguration::getPaymentsReportingFlow, true);
    }

    @Test
    void givenNotSuccessfulPaymentsReportingTypeWhenResolveThenOk() {
        test(IngestionFlowFile.IngestionFlowFileTypeEnum.PAYMENTS_REPORTING, EmailTemplatesConfiguration::getPaymentsReportingFlow, false);
    }
//endregion

    //region TREASURY_OPI
    @Test
    void givenSuccessfulTreasuryOpiTypeWhenResolveThenOk() {
        test(IngestionFlowFile.IngestionFlowFileTypeEnum.TREASURY_OPI, EmailTemplatesConfiguration::getTreasuryOpiFlow, true);
    }

    @Test
    void givenNotSuccessfulTreasuryOpiTypeWhenResolveThenOk() {
        test(IngestionFlowFile.IngestionFlowFileTypeEnum.TREASURY_OPI, EmailTemplatesConfiguration::getTreasuryOpiFlow, false);
    }
//endregion

    //region DP_INSTALLMENTS
    @Test
    void givenSuccessfulDpInstallmentsTypeWhenResolveThenOk() {
        test(IngestionFlowFile.IngestionFlowFileTypeEnum.DP_INSTALLMENTS, EmailTemplatesConfiguration::getDpInstallmentsFlow, true);
    }

    @Test
    void givenNotSuccessfulDpInstallmentsTypeWhenResolveThenOk() {
        test(IngestionFlowFile.IngestionFlowFileTypeEnum.DP_INSTALLMENTS, EmailTemplatesConfiguration::getDpInstallmentsFlow, false);
    }
//endregion
}
