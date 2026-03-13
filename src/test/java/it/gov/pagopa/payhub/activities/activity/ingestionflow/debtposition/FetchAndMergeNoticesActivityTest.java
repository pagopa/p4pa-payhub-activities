package it.gov.pagopa.payhub.activities.activity.ingestionflow.debtposition;

import it.gov.pagopa.payhub.activities.config.FoldersPathsConfig;
import it.gov.pagopa.payhub.activities.connector.pagopapayments.PrintPaymentNoticeService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.IngestionFlowFileNotFoundException;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import it.gov.pagopa.payhub.activities.service.files.ZipFileService;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class FetchAndMergeNoticesActivityTest {
    @Mock
    private PrintPaymentNoticeService printPaymentNoticeServiceMock;
    @Mock
    private IngestionFlowFileService ingestionFlowFileServiceMock;
    @Mock
    private FoldersPathsConfig foldersPathsConfigMock;
    @Mock
    private ZipFileService zipFileServiceMock;
    @Mock
    private FileArchiverService fileArchiverServiceMock;
    @Mock
    private RestTemplate restTemplateMock;

    private FetchAndMergeNoticesActivity activity;

    @BeforeEach
    void setUp() {
        activity = new FetchAndMergeNoticesActivityImpl(
                printPaymentNoticeServiceMock,
                ingestionFlowFileServiceMock,
                foldersPathsConfigMock,
                zipFileServiceMock,
                fileArchiverServiceMock
        );

        ReflectionTestUtils.setField(activity, "noRedirectRestTemplate", restTemplateMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                printPaymentNoticeServiceMock,
                ingestionFlowFileServiceMock,
                foldersPathsConfigMock,
                zipFileServiceMock,
                fileArchiverServiceMock,
                restTemplateMock
        );
    }

    @Test
    void givenIngestionFlowFileNotFoundWhenFetchAndMergeNoticesThenThrowsException() {
        Long ingestionFlowFileId = 1L;
        Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId)).thenReturn(Optional.empty());

        Assertions.assertThrows(IngestionFlowFileNotFoundException.class, () -> activity.fetchAndMergeNotices(ingestionFlowFileId));
    }

    @Test
    void givenNullPdfGeneratedIdWhenFetchAndMergeNoticesThenReturnsZero() {
        Long ingestionFlowFileId = 1L;
        IngestionFlowFile file = new IngestionFlowFile();
        file.setIngestionFlowFileId(ingestionFlowFileId);
        file.setPdfGeneratedId(null);

        Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId)).thenReturn(Optional.of(file));

        Integer result = activity.fetchAndMergeNotices(ingestionFlowFileId);

        Assertions.assertEquals(0, result);
    }
}
