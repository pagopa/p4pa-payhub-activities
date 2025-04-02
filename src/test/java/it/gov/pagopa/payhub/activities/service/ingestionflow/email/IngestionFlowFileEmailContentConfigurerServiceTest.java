package it.gov.pagopa.payhub.activities.service.ingestionflow.email;

import it.gov.pagopa.payhub.activities.config.EmailTemplatesConfiguration;
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
import org.mockito.stubbing.OngoingStubbing;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;

@ExtendWith(MockitoExtension.class)
class IngestionFlowFileEmailContentConfigurerServiceTest {

    @Mock
    private EmailTemplatesConfiguration emailTemplatesConfigurationMock;

    private IngestionFlowFileEmailContentConfigurerService contentConfigurerService;

    @BeforeEach
    void init() {
        contentConfigurerService = new IngestionFlowFileEmailContentConfigurerService(emailTemplatesConfigurationMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(emailTemplatesConfigurationMock);
    }

    @Test
    void givenSuccessTrueWhenConfigureParamsThenOk() {
        whenConfigureParamsThenOk(true);
    }

    @Test
    void givenSuccessFalseWhenConfigureParamsThenOk() {
        whenConfigureParamsThenOk(false);
    }
    void whenConfigureParamsThenOk(boolean success) {
        // Given
        IngestionFlowFile ingestionFlowFileDTO = IngestionFlowFileFaker.buildIngestionFlowFile();

        String mailText;
        OngoingStubbing<String> whenGetMailText;
        if(success) {
            mailText = "TEXTOK";
            whenGetMailText = Mockito.when(emailTemplatesConfigurationMock.getMailTextLoadOk());
        } else {
            mailText = "TEXTKO";
            whenGetMailText = Mockito.when(emailTemplatesConfigurationMock.getMailTextLoadKo());
        }
        whenGetMailText.thenReturn(mailText);

        Map<String, String> result = contentConfigurerService.configureParams(ingestionFlowFileDTO, success);

        // Then
        Assertions.assertEquals(Map.of(
                        "actualDate", result.get("actualDate"),
                        "totalRowsNumber", String.valueOf(Objects.requireNonNull(ingestionFlowFileDTO.getNumTotalRows())),
                        "fileName", ingestionFlowFileDTO.getFileName(),
                        "mailText", mailText
                ),
                result);
        String expectedStartsWithActualDate = DateTimeFormatter.ofPattern("EEE, MMM dd yyyy, hh:").format(LocalDateTime.now());
        Assertions.assertEquals(
                result.get("actualDate").substring(0, expectedStartsWithActualDate.length()),
                expectedStartsWithActualDate);
    }

}
