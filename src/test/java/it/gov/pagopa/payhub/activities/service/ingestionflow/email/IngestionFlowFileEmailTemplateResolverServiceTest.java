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
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IngestionFlowFileEmailTemplateResolverServiceTest {

    @Mock
    private EmailTemplatesConfiguration emailTemplatesConfigurationMock;

    private IngestionFlowFileEmailTemplateResolverService emailTemplateResolverService;

    @BeforeEach
    void init(){
        this.emailTemplateResolverService = new IngestionFlowFileEmailTemplateResolverService(emailTemplatesConfigurationMock);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(emailTemplatesConfigurationMock);
    }

    @Test
    void givenUnexpectedIngestionFlowFileTypeWhenResolveThenIngestionFlowTypeNotSupportedException() {
        // Given
        IngestionFlowFile ingestionFlowFileDTO = IngestionFlowFileFaker.buildIngestionFlowFile();
        ingestionFlowFileDTO.setFlowFileType(IngestionFlowFile.FlowFileTypeEnum.TREASURY_OPI);

        // When, Then
        Assertions.assertThrows(IngestionFlowTypeNotSupportedException.class, () -> emailTemplateResolverService.resolve(ingestionFlowFileDTO, true));
    }

    @Test
    void givenSuccessfulPaymentsReportingTypeWhenResolveThenOk(){
        // Given
        IngestionFlowFile ingestionFlowFileDTO = IngestionFlowFileFaker.buildIngestionFlowFile();
        EmailTemplate expectedResult = new EmailTemplate();

        Mockito.when(emailTemplatesConfigurationMock.getPaymentsReportingFlowOk())
                .thenReturn(expectedResult);

        // When
        EmailTemplate result = emailTemplateResolverService.resolve(ingestionFlowFileDTO, true);

        // Then
        Assertions.assertSame(expectedResult, result);
    }

    @Test
    void givenNotSuccessfulPaymentsReportingTypeWhenResolveThenOk(){
        // Given
        IngestionFlowFile ingestionFlowFileDTO = IngestionFlowFileFaker.buildIngestionFlowFile();
        EmailTemplate expectedResult = EmailTemplate.builder()
                .build();

        Mockito.when(emailTemplatesConfigurationMock.getPaymentsReportingFlowKo())
                .thenReturn(expectedResult);

        // When
        EmailTemplate result = emailTemplateResolverService.resolve(ingestionFlowFileDTO, false);

        // Then
        Assertions.assertSame(expectedResult, result);
    }
}
