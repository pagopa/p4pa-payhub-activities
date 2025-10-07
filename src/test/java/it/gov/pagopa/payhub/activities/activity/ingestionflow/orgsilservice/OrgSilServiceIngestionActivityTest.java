package it.gov.pagopa.payhub.activities.activity.ingestionflow.orgsilservice;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.ingestion.orgsilservice.OrgSilServiceIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.orgsilservice.OrgSilServiceIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.InvalidIngestionFileException;
import it.gov.pagopa.payhub.activities.service.files.CsvService;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.orgsilservice.OrgSilServiceProcessingService;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile.IngestionFlowFileTypeEnum;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

import static it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker.buildIngestionFlowFile;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class OrgSilServiceIngestionActivityTest {


    @Mock
    private CsvService csvServiceMock;
    @Mock
    private OrgSilServiceProcessingService orgSilServiceProcessingServiceMock;
    @Mock
    private IngestionFlowFileService ingestionFlowFileServiceMock;
    @Mock
    private IngestionFlowFileRetrieverService ingestionFlowFileRetrieverServiceMock;
    @Mock
    private FileArchiverService fileArchiverServiceMock;

    private OrgSilServiceIngestionActivityImpl activity;

    @TempDir
    private Path workingDir;

    @BeforeEach
    void setUp() {
        activity = new OrgSilServiceIngestionActivityImpl(
                ingestionFlowFileServiceMock,
                ingestionFlowFileRetrieverServiceMock,
                fileArchiverServiceMock,
                csvServiceMock,
                orgSilServiceProcessingServiceMock
        );
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                ingestionFlowFileServiceMock,
                ingestionFlowFileRetrieverServiceMock,
                fileArchiverServiceMock,
                csvServiceMock,
                orgSilServiceProcessingServiceMock
        );
    }

    @Test
    void handleRetrievedFilesSuccessfully() throws Exception {
        Long ingestionFlowFileId = 1L;
        Long organizationId = 10L;
        IngestionFlowFile ingestionFlowFileDTO = buildIngestionFlowFile();
        ingestionFlowFileDTO.setOrganizationId(organizationId);
        ingestionFlowFileDTO.setFilePathName(workingDir.toString());
        ingestionFlowFileDTO.setIngestionFlowFileType(IngestionFlowFileTypeEnum.ORGANIZATIONS_SIL_SERVICE);
        ingestionFlowFileDTO.setFileVersion("1.0");
        Iterator<OrgSilServiceIngestionFlowFileDTO> iterator = buildOrgSilServiceIngestionFlowFileDTO();
        List<CsvException> readerExceptions = List.of();

        Path filePath = Files.createFile(Path.of(ingestionFlowFileDTO.getFilePathName()).resolve(ingestionFlowFileDTO.getFileName()));
        List<Path> mockedListPath = List.of(filePath);

        Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId))
                .thenReturn(Optional.of(ingestionFlowFileDTO));

        doReturn(mockedListPath).when(ingestionFlowFileRetrieverServiceMock)
                .retrieveAndUnzipFile(ingestionFlowFileDTO.getOrganizationId(), Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());

        Mockito.when(csvServiceMock.readCsv(eq(filePath), eq(OrgSilServiceIngestionFlowFileDTO.class), any(), eq(ingestionFlowFileDTO.getFileVersion())))
                .thenAnswer(invocation -> {
                    BiFunction<Iterator<OrgSilServiceIngestionFlowFileDTO>, List<CsvException>, OrgSilServiceIngestionFlowFileResult> rowProcessor = invocation.getArgument(2);
                    return rowProcessor.apply(iterator, readerExceptions);
                });

        Mockito.when(orgSilServiceProcessingServiceMock.processOrgSilService(same(iterator), same(readerExceptions), eq(ingestionFlowFileDTO), eq(filePath.getParent())))
                .thenReturn(buildOrgSilServiceIngestionFlowFileResult());

        // When
        OrgSilServiceIngestionFlowFileResult result = activity.processFile(ingestionFlowFileId);

        // Then
        Assertions.assertEquals(
                buildOrgSilServiceIngestionFlowFileResult(),
                result);
        Mockito.verify(fileArchiverServiceMock, Mockito.times(1)).archive(ingestionFlowFileDTO);
        Assertions.assertFalse(filePath.toFile().exists());
    }


    @Test
    void givenValidIngestionFlowWhenExceptionThenThrowInvalidIngestionFileException() throws IOException {
        // Given
        Long ingestionFlowFileId = 1L;
        Long organizationId = 10L;
        IngestionFlowFile ingestionFlowFileDTO = buildIngestionFlowFile();
        ingestionFlowFileDTO.setFilePathName(workingDir.toString());
        ingestionFlowFileDTO.setOrganizationId(organizationId);
        ingestionFlowFileDTO.setIngestionFlowFileType(IngestionFlowFileTypeEnum.ORGANIZATIONS_SIL_SERVICE);
        ingestionFlowFileDTO.setFileVersion("1.0");
        Iterator<OrgSilServiceIngestionFlowFileDTO> iterator = buildOrgSilServiceIngestionFlowFileDTO();
        List<CsvException> readerExceptions = List.of();

        Path filePath = Files.createFile(Path.of(ingestionFlowFileDTO.getFilePathName()).resolve(ingestionFlowFileDTO.getFileName()));
        List<Path> mockedListPath = List.of(filePath);

        Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId))
                .thenReturn(Optional.of(ingestionFlowFileDTO));

        doReturn(mockedListPath).when(ingestionFlowFileRetrieverServiceMock)
                .retrieveAndUnzipFile(ingestionFlowFileDTO.getOrganizationId(), Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());

        Mockito.when(csvServiceMock.readCsv(eq(filePath), eq(OrgSilServiceIngestionFlowFileDTO.class), any(), eq(ingestionFlowFileDTO.getFileVersion())))
                .thenAnswer(invocation -> {
                    BiFunction<Iterator<OrgSilServiceIngestionFlowFileDTO>, List<CsvException>, OrgSilServiceIngestionFlowFileResult> rowProcessor = invocation.getArgument(2);
                    return rowProcessor.apply(iterator, readerExceptions);
                });

        Mockito.when(orgSilServiceProcessingServiceMock.processOrgSilService(same(iterator), same(readerExceptions), eq(ingestionFlowFileDTO), eq(filePath.getParent())))
                .thenThrow(new RestClientException("Error"));

        // When & Then
        assertThrows(InvalidIngestionFileException.class, () -> activity.processFile(ingestionFlowFileId));
    }

    private OrgSilServiceIngestionFlowFileResult buildOrgSilServiceIngestionFlowFileResult() {
        OrgSilServiceIngestionFlowFileResult result = new OrgSilServiceIngestionFlowFileResult();
        result.setOrganizationId(10L);
        result.setFileVersion("1.0");
        result.setTotalRows(30L);
        result.setProcessedRows(20L);
        result.setDiscardedFileName("dicardedFileName");
        result.setErrorDescription("errorDescription");
        return result;
    }

    private Iterator<OrgSilServiceIngestionFlowFileDTO> buildOrgSilServiceIngestionFlowFileDTO() {
        List<OrgSilServiceIngestionFlowFileDTO> orgSilServiceIngestionFlowFileDTOS = List.of(
                OrgSilServiceIngestionFlowFileDTO.builder()
                        .ipaCode("IPA123")
                        .applicationName("App1")
                        .build(),
                OrgSilServiceIngestionFlowFileDTO.builder()
                        .ipaCode("IPA46")
                        .applicationName("App2")
                        .build()
        );

        return orgSilServiceIngestionFlowFileDTOS.iterator();
    }
}
