package it.gov.pagopa.payhub.activities.service.ingestionflow.email;

import it.gov.pagopa.payhub.activities.config.EmailTemplatesConfiguration;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.email.EmailDTO;
import it.gov.pagopa.payhub.activities.dto.email.EmailTemplate;
import it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@ExtendWith(MockitoExtension.class)
class IngestionFlowFileEmailContentConfigurerServiceTest {

    @Mock
    private IngestionFlowFileEmailTemplateResolverService emailTemplateResolverServiceMock;
    @Mock
    private EmailTemplatesConfiguration emailTemplatesConfigurationMock;

    private IngestionFlowFileEmailContentConfigurerService contentConfigurerService;

    @BeforeEach
    void init() {
        contentConfigurerService = new IngestionFlowFileEmailContentConfigurerService(
                emailTemplateResolverServiceMock,
                emailTemplatesConfigurationMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                emailTemplateResolverServiceMock,
                emailTemplatesConfigurationMock);
    }

    @Test
    void givenPaymentsReportingTypeAndSuccessWhenConfigureThenOk() {
        // Given
        IngestionFlowFileDTO ingestionFlowFileDTO = IngestionFlowFileFaker.buildIngestionFlowFileDTO();
        boolean success = true;

        Mockito.when(emailTemplatesConfigurationMock.getMailTextLoadOk())
                        .thenReturn("TEXTOK");

        Mockito.when(emailTemplateResolverServiceMock.resolve(ingestionFlowFileDTO, success))
                .thenReturn(EmailTemplate.builder()
                        .subject("SUBJECTOK_{fileName}_{totalRowsNumber}_{mailText}_{actualDate}")
                        .body("BODYOK_{fileName}_{totalRowsNumber}_{mailText}_{actualDate}")
                        .build());

        // When
        EmailDTO result = contentConfigurerService.configure(ingestionFlowFileDTO, success);

        // Then
        result.setParams(null);
        Assertions.assertTrue(result.getMailSubject().startsWith("SUBJECTOK_fileName.csv_3_TEXTOK_"), "Unexpected mail subject: " + result.getMailSubject());
        Assertions.assertTrue(result.getHtmlText().startsWith("BODYOK_fileName.csv_3_TEXTOK_"), "Unexpected html text: " + result.getHtmlText());
    }

    @Test
    void givenPaymentsReportingTypeAndNotSuccessWhenConfigureThenOk() {
        // Given
        IngestionFlowFileDTO ingestionFlowFileDTO = IngestionFlowFileFaker.buildIngestionFlowFileDTO();
        boolean success = false;

        Mockito.when(emailTemplatesConfigurationMock.getMailTextLoadKo())
                .thenReturn("TEXTKO");

        Mockito.when(emailTemplateResolverServiceMock.resolve(ingestionFlowFileDTO, success))
                .thenReturn(EmailTemplate.builder()
                        .subject("SUBJECTKO_{fileName}_{totalRowsNumber}_{mailText}_{actualDate}")
                        .body("BODYKO_{fileName}_{totalRowsNumber}_{mailText}_{actualDate}")
                        .build());

        // When
        EmailDTO result = contentConfigurerService.configure(ingestionFlowFileDTO, success);

        // Then
        result.setParams(null);
        String localDate = LocalDate.now().format(DateTimeFormatter.ofPattern("EEE, MMM dd yyyy, "));
        Assertions.assertTrue(result.getMailSubject().startsWith("SUBJECTKO_fileName.csv_3_TEXTKO_"+localDate), "Unexpected mail subject: " + result.getMailSubject());
        Assertions.assertTrue(result.getHtmlText().startsWith("BODYKO_fileName.csv_3_TEXTKO_"+localDate), "Unexpected html text: " + result.getHtmlText());
    }
}
