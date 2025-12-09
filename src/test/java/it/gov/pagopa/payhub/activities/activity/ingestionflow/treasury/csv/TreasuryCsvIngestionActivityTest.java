package it.gov.pagopa.payhub.activities.activity.ingestionflow.treasury.csv;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.TreasuryIufIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.csv.TreasuryCsvIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.InvalidIngestionFileException;
import it.gov.pagopa.payhub.activities.service.files.CsvService;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.csv.TreasuryCsvProcessingService;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
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
import uk.co.jemos.podam.api.PodamFactory;

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
class TreasuryCsvIngestionActivityTest {
    @Mock
    private CsvService csvServiceMock;
    @Mock
    private TreasuryCsvProcessingService treasuryCsvProcessingServiceMock;
    @Mock
    private IngestionFlowFileService ingestionFlowFileServiceMock;
    @Mock
    private IngestionFlowFileRetrieverService ingestionFlowFileRetrieverServiceMock;
    @Mock
    private FileArchiverService fileArchiverServiceMock;

    private TreasuryCsvIngestionActivityImpl activity;

    @TempDir
    private Path workingDir;

    @BeforeEach
    void setUp() {
        activity = new TreasuryCsvIngestionActivityImpl(
                ingestionFlowFileServiceMock,
                ingestionFlowFileRetrieverServiceMock,
                fileArchiverServiceMock,
                csvServiceMock,
                treasuryCsvProcessingServiceMock
        );
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                ingestionFlowFileServiceMock,
                ingestionFlowFileRetrieverServiceMock,
                fileArchiverServiceMock,
                csvServiceMock,
                treasuryCsvProcessingServiceMock
        );
    }

    private TreasuryIufIngestionFlowFileResult buildTreasuryIufIngestionFlowFileResult() {
        return TreasuryIufIngestionFlowFileResult.builder()
                .organizationId(10L)
                .processedRows(20L)
                .totalRows(30L)
                .discardedFileName("discardedFileName")
                .errorDescription("errorDescription")
                .operatorExternalUserId("OPERATORID")
                .fileSize(100L)
                .build();
    }

    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    private Iterator<TreasuryCsvIngestionFlowFileDTO> buildTreasuryCsvIngestionFlowFileDTO() {
        List<TreasuryCsvIngestionFlowFileDTO> debtPositionTypeIngestionFlowFileDTOS = List.of(
                podamFactory.manufacturePojo(
                        TreasuryCsvIngestionFlowFileDTO.class),
                podamFactory.manufacturePojo(
                        TreasuryCsvIngestionFlowFileDTO.class)
        );
        debtPositionTypeIngestionFlowFileDTOS.forEach(x->x.setBillYear("2025"));

        return debtPositionTypeIngestionFlowFileDTOS.iterator();
    }

    @Test
    void givenMultipleFilesWhenProcessingFileThenThrowsInvalidIngestionFileException() throws Exception {
        Long ingestionFlowFileId = 1L;
        Long organizationId = 10L;
        IngestionFlowFile ingestionFlowFileDTO = buildIngestionFlowFile();
        ingestionFlowFileDTO.setOrganizationId(organizationId);
        ingestionFlowFileDTO.setFilePathName(workingDir.toString());
        ingestionFlowFileDTO.setIngestionFlowFileType(IngestionFlowFile.IngestionFlowFileTypeEnum.TREASURY_CSV);

        Path filePath = Files.createFile(Path.of(ingestionFlowFileDTO.getFilePathName()).resolve(ingestionFlowFileDTO.getFileName()));

        List<Path> mockedListPath = List.of(filePath, filePath);

        Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId))
                .thenReturn(Optional.of(ingestionFlowFileDTO));

        doReturn(mockedListPath).when(ingestionFlowFileRetrieverServiceMock)
                .retrieveAndUnzipFile(ingestionFlowFileDTO.getOrganizationId(), Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());

        assertThrows(InvalidIngestionFileException.class, () -> activity.processFile(ingestionFlowFileId));
    }

    @Test
    void givenValidFileWhenProcessingFileThenFileProcessedSuccessfullyAndArchived() throws Exception{
        Long ingestionFlowFileId = 1L;
        Long organizationId = 10L;
        IngestionFlowFile ingestionFlowFileDTO = buildIngestionFlowFile();
        ingestionFlowFileDTO.setOrganizationId(organizationId);
        ingestionFlowFileDTO.setFilePathName(workingDir.toString());
        ingestionFlowFileDTO.setIngestionFlowFileType(IngestionFlowFile.IngestionFlowFileTypeEnum.TREASURY_CSV);
        ingestionFlowFileDTO.setFileVersion("1.0");
        Iterator<TreasuryCsvIngestionFlowFileDTO> iterator = buildTreasuryCsvIngestionFlowFileDTO();
        List<CsvException> readerExceptions = List.of();

        Path filePath = Files.createFile(Path.of(ingestionFlowFileDTO.getFilePathName()).resolve(ingestionFlowFileDTO.getFileName()));

        List<Path> mockedListPath = List.of(filePath);

        Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId))
                .thenReturn(Optional.of(ingestionFlowFileDTO));

        doReturn(mockedListPath).when(ingestionFlowFileRetrieverServiceMock)
                .retrieveAndUnzipFile(ingestionFlowFileDTO.getOrganizationId(), Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());

        Mockito.when(csvServiceMock.readCsv(eq(filePath), eq(TreasuryCsvIngestionFlowFileDTO.class), any(), eq(ingestionFlowFileDTO.getFileVersion())))
                .thenAnswer(invocation -> {
                    BiFunction<Iterator<TreasuryCsvIngestionFlowFileDTO>, List<CsvException>, TreasuryIufIngestionFlowFileResult> rowProcessor = invocation.getArgument(2);
                    return rowProcessor.apply(iterator, readerExceptions);
                });

        Mockito.when(treasuryCsvProcessingServiceMock.processTreasuryCsv(same(iterator), same(readerExceptions), eq(ingestionFlowFileDTO), eq(filePath.getParent())))
                .thenReturn(buildTreasuryIufIngestionFlowFileResult());

        TreasuryIufIngestionFlowFileResult result = activity.processFile(ingestionFlowFileId);

        Assertions.assertEquals(
                buildTreasuryIufIngestionFlowFileResult(),
                result);
        Mockito.verify(fileArchiverServiceMock, Mockito.times(1)).archive(ingestionFlowFileDTO);
        Assertions.assertFalse(filePath.toFile().exists());
    }

    @Test
    void givenValidIngestionFlowWhenProcessingThrowsExceptionThenThrowInvalidIngestionFileException() throws IOException {
        Long ingestionFlowFileId = 1L;
        Long organizationId = 10L;
        IngestionFlowFile ingestionFlowFileDTO = buildIngestionFlowFile();
        ingestionFlowFileDTO.setFilePathName(workingDir.toString());
        ingestionFlowFileDTO.setOrganizationId(organizationId);
        ingestionFlowFileDTO.setIngestionFlowFileType(IngestionFlowFile.IngestionFlowFileTypeEnum.TREASURY_CSV);
        ingestionFlowFileDTO.setFileVersion("1.0");
        Iterator<TreasuryCsvIngestionFlowFileDTO> iterator = buildTreasuryCsvIngestionFlowFileDTO();
        List<CsvException> readerExceptions = List.of();

        Path filePath = Files.createFile(Path.of(ingestionFlowFileDTO.getFilePathName()).resolve(ingestionFlowFileDTO.getFileName()));
        List<Path> mockedListPath = List.of(filePath);

        Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId))
                .thenReturn(Optional.of(ingestionFlowFileDTO));

        doReturn(mockedListPath).when(ingestionFlowFileRetrieverServiceMock)
                .retrieveAndUnzipFile(ingestionFlowFileDTO.getOrganizationId(), Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());

        Mockito.when(csvServiceMock.readCsv(eq(filePath), eq(TreasuryCsvIngestionFlowFileDTO.class), any(), eq(ingestionFlowFileDTO.getFileVersion())))
                .thenAnswer(invocation -> {
                    BiFunction<Iterator<TreasuryCsvIngestionFlowFileDTO>, List<CsvException>, TreasuryIufIngestionFlowFileResult> rowProcessor = invocation.getArgument(2);
                    return rowProcessor.apply(iterator, readerExceptions);
                });

        Mockito.when(treasuryCsvProcessingServiceMock.processTreasuryCsv(same(iterator), same(readerExceptions), eq(ingestionFlowFileDTO), eq(filePath.getParent())))
                .thenThrow(new RestClientException("Error"));

        assertThrows(InvalidIngestionFileException.class, () -> activity.processFile(ingestionFlowFileId));
    }
}
