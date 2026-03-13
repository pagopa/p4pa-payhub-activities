package it.gov.pagopa.payhub.activities.activity.ingestionflow.notice;

import it.gov.pagopa.payhub.activities.config.FoldersPathsConfig;
import it.gov.pagopa.payhub.activities.connector.pagopapayments.PrintPaymentNoticeService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.connector.signedurl.SignedUrlService;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.IngestionFlowFileNotFoundException;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import it.gov.pagopa.payhub.activities.service.files.ZipFileService;
import it.gov.pagopa.pu.pagopapayments.dto.generated.SignedUrlResultDTO;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;

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
    private SignedUrlService signedUrlServiceMock;

    private FetchAndMergeNoticesActivity activity;

    @BeforeEach
    void setUp() {
        activity = new FetchAndMergeNoticesActivityImpl(
                printPaymentNoticeServiceMock,
                ingestionFlowFileServiceMock,
                foldersPathsConfigMock,
                zipFileServiceMock,
                fileArchiverServiceMock,
                signedUrlServiceMock
        );
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                printPaymentNoticeServiceMock,
                ingestionFlowFileServiceMock,
                foldersPathsConfigMock,
                zipFileServiceMock,
                fileArchiverServiceMock,
                signedUrlServiceMock
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

    @Test
    void givenSignedUrlReturns404WhenFetchAndMergeNoticesThenReturnsZero() {
        Long ingestionFlowFileId = 1L;
        Long organizationId = 1L;

        IngestionFlowFile file = new IngestionFlowFile();
        file.setIngestionFlowFileId(ingestionFlowFileId);
        file.setOrganizationId(organizationId);
        file.setPdfGeneratedId("folderId1,folderId2");

        Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId)).thenReturn(Optional.of(file));

        HttpClientErrorException notFoundException = HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", null, null, null);
        Mockito.when(printPaymentNoticeServiceMock.getSignedUrl(organizationId, "folderId1")).thenThrow(notFoundException);

        Integer result = activity.fetchAndMergeNotices(ingestionFlowFileId);

        Assertions.assertEquals(0, result);
    }

    @Test
    void givenAllSignedUrlsFetchedWhenFetchAndMergeNoticesThenDownloadsAndMergesSuccessfully() throws Exception {
        Long ingestionFlowFileId = 1L;
        Long organizationId = 2L;

        IngestionFlowFile file = new IngestionFlowFile();
        file.setIngestionFlowFileId(ingestionFlowFileId);
        file.setOrganizationId(organizationId);
        file.setPdfGeneratedId("folderId1,folderId2");
        file.setFilePathName("filePathName");
        file.setFileName("ingestionFile.zip");

        Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId)).thenReturn(Optional.of(file));

        SignedUrlResultDTO dto1 = new SignedUrlResultDTO();
        dto1.setSignedUrl("http://url1");
        SignedUrlResultDTO dto2 = new SignedUrlResultDTO();
        dto2.setSignedUrl("http://url2");

        Mockito.when(printPaymentNoticeServiceMock.getSignedUrl(organizationId, "folderId1")).thenReturn(dto1);
        Mockito.when(printPaymentNoticeServiceMock.getSignedUrl(organizationId, "folderId2")).thenReturn(dto2);

        Mockito.when(foldersPathsConfigMock.getTmp()).thenReturn(Path.of("/tmp"));
        Mockito.when(foldersPathsConfigMock.getShared()).thenReturn(Path.of("/shared"));
        Mockito.when(foldersPathsConfigMock.getProcessTargetSubFolders())
                .thenReturn(FoldersPathsConfig.ProcessTargetSubFolders.builder()
                        .archive("archive")
                        .build());

        byte[] dummyBytes = "dummy_zip_content".getBytes();
        Mockito.when(signedUrlServiceMock.downloadFileFromSignedUrl("http://url1")).thenReturn(dummyBytes);
        Mockito.when(signedUrlServiceMock.downloadFileFromSignedUrl("http://url2")).thenReturn(dummyBytes);

        Path extracted1 = Path.of("extracted1.pdf");
        Path extracted2 = Path.of("extracted2.pdf");

        Mockito.when(zipFileServiceMock.unzip(any(Path.class), any(Path.class)))
                .thenReturn(List.of(extracted1))
                .thenReturn(List.of(extracted2));

        Mockito.when(fileArchiverServiceMock.compressAndArchive(anyList(), any(Path.class), any(Path.class))).thenReturn(100L);

        Integer result = activity.fetchAndMergeNotices(ingestionFlowFileId);

        Assertions.assertEquals(2, result);
    }

    @Test
    void givenDownloadFailsWhenFetchAndMergeNoticesThenThrowsException() {
        Long ingestionFlowFileId = 1L;
        Long organizationId = 1L;

        IngestionFlowFile file = new IngestionFlowFile();
        file.setIngestionFlowFileId(ingestionFlowFileId);
        file.setOrganizationId(organizationId);
        file.setPdfGeneratedId("folderId1");
        file.setFilePathName("filePathName");

        Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId)).thenReturn(Optional.of(file));

        SignedUrlResultDTO dto1 = new SignedUrlResultDTO(); dto1.setSignedUrl("http://url1");
        Mockito.when(printPaymentNoticeServiceMock.getSignedUrl(organizationId, "folderId1")).thenReturn(dto1);

        Mockito.when(foldersPathsConfigMock.getTmp()).thenReturn(Path.of("/tmp"));

        Mockito.when(signedUrlServiceMock.downloadFileFromSignedUrl("http://url1"))
                .thenThrow(new RestClientException("Connection timed out"));

        Assertions.assertThrows(RestClientException.class, () -> activity.fetchAndMergeNotices(ingestionFlowFileId));
    }
}
