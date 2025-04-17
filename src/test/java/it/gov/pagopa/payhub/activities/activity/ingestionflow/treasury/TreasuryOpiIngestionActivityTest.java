package it.gov.pagopa.payhub.activities.activity.ingestionflow.treasury;

import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.ingestion.IngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.TreasuryIufIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.exception.NotRetryableActivityException;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.IngestionFlowFileNotFoundException;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.IngestionFlowTypeNotSupportedException;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.TreasuryErrorsArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.TreasuryOpiParserService;
import it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TreasuryOpiIngestionActivityTest {

    @Mock
    private IngestionFlowFileService ingestionFlowFileServiceMock;
    @Mock
    private IngestionFlowFileRetrieverService ingestionFlowFileRetrieverServiceMock;
    @Mock
    private TreasuryOpiParserService treasuryOpiParserServiceMock;
    @Mock
    private FileArchiverService fileArchiverServiceMock;
    @Mock
    private TreasuryErrorsArchiverService treasuryErrorsArchiverServiceMock;

    private TreasuryOpiIngestionActivity activity;

    @TempDir
    Path workingDir;

    @BeforeEach
    void setUp() {
        activity = new TreasuryOpiIngestionActivityImpl(
                ingestionFlowFileServiceMock,
                ingestionFlowFileRetrieverServiceMock,
                treasuryOpiParserServiceMock,
                fileArchiverServiceMock,
                treasuryErrorsArchiverServiceMock
        );
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                ingestionFlowFileServiceMock,
                ingestionFlowFileRetrieverServiceMock,
                treasuryOpiParserServiceMock,
                fileArchiverServiceMock,
                treasuryErrorsArchiverServiceMock
        );
    }

    @Test
    void givenValidIngestionFlowWhenProcessFileThenOk() throws IOException {
        // Given
        Long ingestionFlowFileId = 1L;
        IngestionFlowFile ingestionFlowFileDTO = buildIngestionFlowFile(ingestionFlowFileId);

        Path filePath = Files.createFile(Path.of(ingestionFlowFileDTO.getFilePathName()).resolve(ingestionFlowFileDTO.getFileName()));
        List<Path> mockedListPath = List.of(filePath);

        TreasuryIufIngestionFlowFileResult expectedResult = TreasuryIufIngestionFlowFileResult.builder()
                .organizationId(ingestionFlowFileDTO.getOrganizationId())
                .iuf2TreasuryIdMap(Map.of("IUF123", "treasury123"))
                .discardedFileName("DISCARDFILENAME")
                .errorDescription("There were some errors during TreasuryOPI file ingestion. Please check error file.")
                .processedRows(10L)
                .totalRows(100L)
                .build();

        Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId))
                .thenReturn(Optional.of(ingestionFlowFileDTO));

        doReturn(mockedListPath).when(ingestionFlowFileRetrieverServiceMock)
                .retrieveAndUnzipFile(ingestionFlowFileDTO.getOrganizationId(), Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());

        Mockito.when(treasuryOpiParserServiceMock.parseData(filePath, ingestionFlowFileDTO, mockedListPath.size()))
                .thenReturn(Pair.of(
                        IngestionFlowFileResult.builder()
                                .processedRows(10L)
                                .totalRows(100L)
                                .build(),
                        Collections.singletonMap("IUF123", "treasury123")));

        Mockito.when(treasuryErrorsArchiverServiceMock.archiveErrorFiles(mockedListPath.getFirst().getParent(), ingestionFlowFileDTO))
                .thenReturn("DISCARDFILENAME");

        // When
        TreasuryIufIngestionFlowFileResult result = activity.processFile(ingestionFlowFileId);

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedResult, result);

        Mockito.verify(fileArchiverServiceMock, Mockito.times(1))
                .archive(ingestionFlowFileDTO);

        Assertions.assertFalse(filePath.toFile().exists());
    }

    @Test
    void givenIngestionFlowNotFoundWhenProcessFileThenNoSuccess() {
        // Given
        long ingestionFlowId = 1L;
        when(ingestionFlowFileServiceMock.findById(ingestionFlowId)).thenReturn(Optional.empty());

        // When, Then
        Assertions.assertThrows(IngestionFlowFileNotFoundException.class, () -> activity.processFile(ingestionFlowId));
    }

    @Test
    void givenIngestionFlowTypeInvalidWhenProcessFileThenNoSuccess() {
        //given
        long ingestionFlowFileId = 1L;
        IngestionFlowFile ingestionFlowFile = IngestionFlowFileFaker.buildIngestionFlowFile()
                .ingestionFlowFileId(ingestionFlowFileId)
                .ingestionFlowFileType(IngestionFlowFile.IngestionFlowFileTypeEnum.PAYMENTS_REPORTING);

        when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId)).thenReturn(Optional.of(ingestionFlowFile));

        //when
        Assertions.assertThrows(IngestionFlowTypeNotSupportedException.class, () -> activity.processFile(ingestionFlowFileId));
    }

    @Test
    void givenExceptionWithoutDiscardFileWhenProcessFileThenReportFileErrorDescription() throws IOException {
        // Given
        Long ingestionFlowFileId = 1L;
        IngestionFlowFile ingestionFlowFileDTO = buildIngestionFlowFile(ingestionFlowFileId);

        Pair<List<Path>, Map<String, String>> mockConfig = configureExceptionWhenParse(ingestionFlowFileDTO, null);

        // When
        TreasuryIufIngestionFlowFileResult result = activity.processFile(ingestionFlowFileId);

        // Then
        assertEquals(ingestionFlowFileDTO.getOrganizationId(), result.getOrganizationId());
        assertEquals(mockConfig.getValue(), result.getIuf2TreasuryIdMap());
        assertNull(result.getDiscardedFileName());
        assertEquals(
                "There were some errors during TreasuryOPI file ingestion.\ntestFile.zip1:error occurred",
                result.getErrorDescription());

        mockConfig.getKey().forEach(f -> Assertions.assertFalse(f.toFile().exists()));
    }

    @Test
    void givenExceptionWithDiscardFileWhenProcessFileThenCompleteErrorDescription() throws IOException {
        // Given
        Long ingestionFlowFileId = 1L;
        IngestionFlowFile ingestionFlowFileDTO = buildIngestionFlowFile(ingestionFlowFileId);
        String discardFileName = "discardFileName";

        Pair<List<Path>, Map<String, String>> mockConfig = configureExceptionWhenParse(ingestionFlowFileDTO, discardFileName);

        // When
        TreasuryIufIngestionFlowFileResult result = activity.processFile(ingestionFlowFileId);

        // Then
        assertEquals(ingestionFlowFileDTO.getOrganizationId(), result.getOrganizationId());
        assertEquals(mockConfig.getValue(), result.getIuf2TreasuryIdMap());
        assertEquals(discardFileName, result.getDiscardedFileName());
        assertEquals(
                "There were some errors during TreasuryOPI file ingestion. Please check error file.\ntestFile.zip1:error occurred",
                result.getErrorDescription());

        mockConfig.getKey().forEach(f -> Assertions.assertFalse(f.toFile().exists()));
    }

    private Pair<List<Path>, Map<String, String>> configureExceptionWhenParse(IngestionFlowFile ingestionFlowFileDTO, String discardFileName) throws IOException {
        Path filePath1 = Files.createFile(Path.of(ingestionFlowFileDTO.getFilePathName()).resolve(ingestionFlowFileDTO.getFileName() + "1"));
        Path filePath2 = Files.createFile(Path.of(ingestionFlowFileDTO.getFilePathName()).resolve(ingestionFlowFileDTO.getFileName() + "2"));
        List<Path> files = List.of(filePath1, filePath2);

        when(ingestionFlowFileServiceMock.findById(ingestionFlowFileDTO.getIngestionFlowFileId()))
                .thenReturn(Optional.of(ingestionFlowFileDTO));
        doReturn(files).when(ingestionFlowFileRetrieverServiceMock)
                .retrieveAndUnzipFile(ingestionFlowFileDTO.getOrganizationId(), Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());

        NotRetryableActivityException expectedException = new NotRetryableActivityException("error occurred");
        Mockito.when(treasuryOpiParserServiceMock.parseData(filePath1, ingestionFlowFileDTO, files.size()))
                .thenThrow(expectedException);

        Map<String, String> expectedParseResult = Map.of(
                "IUF_FILE2", "TREASURYID_1",
                "IUF2_FILE2", "TREASURYID_2");
        Mockito.when(treasuryOpiParserServiceMock.parseData(filePath2, ingestionFlowFileDTO, files.size()))
                .thenReturn(Pair.of(
                        IngestionFlowFileResult.builder()
                                .processedRows(1L)
                                .totalRows(5L)
                                .build(),
                        expectedParseResult));

        Mockito.when(treasuryErrorsArchiverServiceMock.archiveErrorFiles(workingDir, ingestionFlowFileDTO))
                .thenReturn(discardFileName);

        Mockito.doNothing().when(fileArchiverServiceMock)
                .archive(ingestionFlowFileDTO);

        return Pair.of(files, expectedParseResult);
    }

    private IngestionFlowFile buildIngestionFlowFile(Long ingestionFlowFileId) {
        return IngestionFlowFileFaker.buildIngestionFlowFile()
                .ingestionFlowFileId(ingestionFlowFileId)
                .ingestionFlowFileType(IngestionFlowFile.IngestionFlowFileTypeEnum.TREASURY_OPI)
                .filePathName(workingDir.toString())
                .fileName("testFile.zip")
                .organizationId(0L);
    }
}