package it.gov.pagopa.payhub.activities.activity.ingestionflow.email;

import it.gov.pagopa.payhub.activities.activity.email.SendEmailActivity;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.email.EmailDTO;
import it.gov.pagopa.payhub.activities.exception.IngestionFlowFileNotFoundException;
import it.gov.pagopa.payhub.activities.service.ingestionflow.email.IngestionFlowFileEmailContentConfigurerService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.email.IngestionFlowFileEmailDestinationRetrieverService;
import it.gov.pagopa.payhub.activities.util.faker.EmailDTOFaker;
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

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class SendEmailIngestionFlowActivityTest {

    @Mock
    private IngestionFlowFileService ingestionFlowFileServiceMock;
    @Mock
    private IngestionFlowFileEmailDestinationRetrieverService destinationRetrieverServiceMock;
    @Mock
    private IngestionFlowFileEmailContentConfigurerService contentConfigurerServiceMock;
    @Mock
    private SendEmailActivity sendEmailActivityMock;

    private SendEmailIngestionFlowActivity activity;

    @BeforeEach
    void init() {
        activity = new SendEmailIngestionFlowActivityImpl(
                ingestionFlowFileServiceMock,
                destinationRetrieverServiceMock,
                contentConfigurerServiceMock,
                sendEmailActivityMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                ingestionFlowFileServiceMock,
                destinationRetrieverServiceMock,
                contentConfigurerServiceMock,
                sendEmailActivityMock);
    }

    @Test
    void givenNotIngestionFlowFileRecordWhenSendEmailThenIngestionFlowFileNotFoundException() {
        // Given
        long ingestionFlowFileId = 1L;
        Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId))
                .thenReturn(Optional.empty());

        // When, Then
        Assertions.assertThrows(IngestionFlowFileNotFoundException.class, () -> activity.sendEmail(ingestionFlowFileId, true));
    }

    @Test
    void givenCompleteConfigurationWhenSendEmailThenOk() {
        // Given
        IngestionFlowFile ingestionFlowFileDTO = IngestionFlowFileFaker.buildIngestionFlowFile();
        EmailDTO emailDTO = EmailDTOFaker.buildEmailDTO();
        boolean success = true;

        Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileDTO.getIngestionFlowFileId()))
                .thenReturn(Optional.of(ingestionFlowFileDTO));
        Mockito.when(contentConfigurerServiceMock.configure(ingestionFlowFileDTO, success))
                .thenReturn(emailDTO);

        // When
        activity.sendEmail(ingestionFlowFileDTO.getIngestionFlowFileId(), success);

        // Then
        Mockito.verify(destinationRetrieverServiceMock).configure(ingestionFlowFileDTO, emailDTO);
        Mockito.verify(sendEmailActivityMock).send(emailDTO);

    }

}

