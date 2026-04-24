package it.gov.pagopa.payhub.activities.service.email;

import it.gov.pagopa.payhub.activities.connector.emailtemplates.DownloadTemplateFileClient;
import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class EmailTemplateRetrieverServiceImplTest {

    @Mock
    private DownloadTemplateFileClient downloadTemplateFileClientMock;

    @InjectMocks
    private EmailTemplateRetrieverServiceImpl emailTemplateRetrieverService;

    @AfterEach
    void tear() {
        Mockito.verifyNoMoreInteractions(downloadTemplateFileClientMock);
    }

    @Test
    void retrieveTemplate() {
        Mockito.when(downloadTemplateFileClientMock.downloadTemplateFile(Mockito.anyString(),Mockito.anyString()))
                .thenReturn(Optional.empty());
        emailTemplateRetrieverService.retrieveTemplate("BROKER_EXTERNAL_ID", EmailTemplateName.INGESTION_ASSESSMENTS_OK, "EMAIL_SUBJECT");
    }
}