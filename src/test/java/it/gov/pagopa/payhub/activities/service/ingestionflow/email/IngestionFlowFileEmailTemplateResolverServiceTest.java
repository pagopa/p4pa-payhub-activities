package it.gov.pagopa.payhub.activities.service.ingestionflow.email;

import it.gov.pagopa.payhub.activities.config.EmailTemplatesConfiguration;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.email.EmailTemplate;
import it.gov.pagopa.payhub.activities.enums.IngestionFlowFileType;
import it.gov.pagopa.payhub.activities.exception.IngestionFlowTypeNotSupportedException;
import it.gov.pagopa.payhub.activities.utility.faker.IngestionFlowFileFaker;
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
        IngestionFlowFileDTO ingestionFlowFileDTO = IngestionFlowFileFaker.buildIngestionFlowFileDTO();
        ingestionFlowFileDTO.setFlowFileType(IngestionFlowFileType.OPI);

        // When, Then
        Assertions.assertThrows(IngestionFlowTypeNotSupportedException.class, () -> emailTemplateResolverService.resolve(ingestionFlowFileDTO, true));
    }

    @Test
    void givenSuccessfulPaymentsReportingTypeWhenResolveThenOk(){
        // Given
        IngestionFlowFileDTO ingestionFlowFileDTO = IngestionFlowFileFaker.buildIngestionFlowFileDTO();
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
        IngestionFlowFileDTO ingestionFlowFileDTO = IngestionFlowFileFaker.buildIngestionFlowFileDTO();
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
