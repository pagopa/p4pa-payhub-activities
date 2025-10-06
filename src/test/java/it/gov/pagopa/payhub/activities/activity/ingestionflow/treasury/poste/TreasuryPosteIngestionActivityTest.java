package it.gov.pagopa.payhub.activities.activity.ingestionflow.treasury.poste;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.TreasuryIufIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.poste.TreasuryPosteIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.InvalidIngestionFileException;
import it.gov.pagopa.payhub.activities.service.files.CsvService;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.poste.TreasuryPosteProcessingService;
import it.gov.pagopa.payhub.activities.util.TestUtils;
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
class TreasuryPosteIngestionActivityTest {
  private final PodamFactory podamFactory = TestUtils.getPodamFactory();
  
  @Mock
  private CsvService csvServiceMock;
  @Mock
  private TreasuryPosteProcessingService treasuryPosteProcessingService;
  @Mock
  private IngestionFlowFileService ingestionFlowFileServiceMock;
  @Mock
  private IngestionFlowFileRetrieverService ingestionFlowFileRetrieverServiceMock;
  @Mock
  private FileArchiverService fileArchiverServiceMock;

  private TreasuryPosteIngestionActivityImpl activity;

  @TempDir
  private Path workingDir;

  @BeforeEach
  void setUp() {
    activity = new TreasuryPosteIngestionActivityImpl(
        ingestionFlowFileServiceMock,
        ingestionFlowFileRetrieverServiceMock,
        fileArchiverServiceMock,
        csvServiceMock,
        treasuryPosteProcessingService
    );
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
        ingestionFlowFileServiceMock,
        ingestionFlowFileRetrieverServiceMock,
        fileArchiverServiceMock,
        csvServiceMock,
        treasuryPosteProcessingService
    );
  }

  @Test
  void handleRetrievedFilesMultipleFileException() throws Exception {
    Long ingestionFlowFileId = 1L;
    Long organizationId = 10L;
    IngestionFlowFile ingestionFlowFileDTO = buildIngestionFlowFile();
    ingestionFlowFileDTO.setOrganizationId(organizationId);
    ingestionFlowFileDTO.setFilePathName(workingDir.toString());
    ingestionFlowFileDTO.setIngestionFlowFileType(IngestionFlowFileTypeEnum.TREASURY_POSTE);

    Path filePath = Files.createFile(Path.of(ingestionFlowFileDTO.getFilePathName()).resolve(ingestionFlowFileDTO.getFileName()));

    // There are two of them to allow Exception on testing
    List<Path> mockedListPath = List.of(filePath, filePath);

    Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId))
        .thenReturn(Optional.of(ingestionFlowFileDTO));

    doReturn(mockedListPath).when(ingestionFlowFileRetrieverServiceMock)
        .retrieveAndUnzipFile(ingestionFlowFileDTO.getOrganizationId(), Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());

    assertThrows(InvalidIngestionFileException.class, () -> activity.processFile(ingestionFlowFileId));
  }

  @Test
  void handleRetrievedFilesSuccessfully() throws Exception {
    Long ingestionFlowFileId = 1L;
    Long organizationId = 10L;
    IngestionFlowFile ingestionFlowFileDTO = buildIngestionFlowFile();
    ingestionFlowFileDTO.setOrganizationId(organizationId);
    ingestionFlowFileDTO.setFilePathName(workingDir.toString());
    ingestionFlowFileDTO.setIngestionFlowFileType(IngestionFlowFileTypeEnum.TREASURY_POSTE);
    ingestionFlowFileDTO.setFileVersion("1.0");
    Iterator<TreasuryPosteIngestionFlowFileDTO> iterator = buildTreasuryPosteIngestionFlowFileDTO();
    List<CsvException> readerExceptions = List.of();

    Path filePath = Files.createFile(Path.of(ingestionFlowFileDTO.getFilePathName()).resolve(ingestionFlowFileDTO.getFileName()));

    List<Path> mockedListPath = List.of(filePath);

    Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId))
        .thenReturn(Optional.of(ingestionFlowFileDTO));

    doReturn(mockedListPath).when(ingestionFlowFileRetrieverServiceMock)
        .retrieveAndUnzipFile(ingestionFlowFileDTO.getOrganizationId(), Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());

    Mockito.when(csvServiceMock.readCsvPositionalColumn(eq(filePath), eq(TreasuryPosteIngestionFlowFileDTO.class), any(), eq(ingestionFlowFileDTO.getFileVersion()), eq(1)))
        .thenAnswer(invocation -> {
          BiFunction<Iterator<TreasuryPosteIngestionFlowFileDTO>, List<CsvException>, TreasuryIufIngestionFlowFileResult> rowProcessor = invocation.getArgument(2);
          return rowProcessor.apply(iterator, readerExceptions);
        });

    Mockito.when(treasuryPosteProcessingService.processTreasuryPoste(same(iterator), any(), same(readerExceptions), eq(ingestionFlowFileDTO), eq(filePath.getParent())))
        .thenReturn(buildTreasuryPosteIngestionFlowFileResult());

    // When
    TreasuryIufIngestionFlowFileResult result = activity.processFile(ingestionFlowFileId);

    // Then
    Assertions.assertEquals(
        buildTreasuryPosteIngestionFlowFileResult(),
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
    ingestionFlowFileDTO.setIngestionFlowFileType(IngestionFlowFileTypeEnum.TREASURY_POSTE);
    ingestionFlowFileDTO.setFileVersion("1.0");
    Iterator<TreasuryPosteIngestionFlowFileDTO> iterator = buildTreasuryPosteIngestionFlowFileDTO();
    List<CsvException> readerExceptions = List.of();

    Path filePath = Files.createFile(Path.of(ingestionFlowFileDTO.getFilePathName()).resolve(ingestionFlowFileDTO.getFileName()));
    List<Path> mockedListPath = List.of(filePath);

    Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId))
        .thenReturn(Optional.of(ingestionFlowFileDTO));

    doReturn(mockedListPath).when(ingestionFlowFileRetrieverServiceMock)
        .retrieveAndUnzipFile(ingestionFlowFileDTO.getOrganizationId(), Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());

    Mockito.when(csvServiceMock.readCsvPositionalColumn(eq(filePath), eq(TreasuryPosteIngestionFlowFileDTO.class), any(), eq(ingestionFlowFileDTO.getFileVersion()), eq(1)))
        .thenAnswer(invocation -> {
          BiFunction<Iterator<TreasuryPosteIngestionFlowFileDTO>, List<CsvException>, TreasuryIufIngestionFlowFileResult> rowProcessor = invocation.getArgument(2);
          return rowProcessor.apply(iterator, readerExceptions);
        });

    Mockito.when(treasuryPosteProcessingService.processTreasuryPoste(same(iterator), any(), same(readerExceptions), eq(ingestionFlowFileDTO), eq(filePath.getParent())))
        .thenThrow(new RestClientException("Error"));

    // When & Then
    assertThrows(InvalidIngestionFileException.class, () -> activity.processFile(ingestionFlowFileId));
  }

  private TreasuryIufIngestionFlowFileResult buildTreasuryPosteIngestionFlowFileResult() {
    return TreasuryIufIngestionFlowFileResult.builder()
        .organizationId(10L)
        .processedRows(20L)
        .totalRows(30L)
        .discardedFileName("dicardedFileName")
        .errorDescription("errorDescription")
        .build();
  }
  
  private Iterator<TreasuryPosteIngestionFlowFileDTO> buildTreasuryPosteIngestionFlowFileDTO() {
    List<TreasuryPosteIngestionFlowFileDTO> debtPositionTypeIngestionFlowFileDTOS = List.of(
        podamFactory.manufacturePojo(
            TreasuryPosteIngestionFlowFileDTO.class),
        podamFactory.manufacturePojo(
            TreasuryPosteIngestionFlowFileDTO.class)
    );
    debtPositionTypeIngestionFlowFileDTOS.forEach(x->x.setBillDate("23/09/2025"));

    return debtPositionTypeIngestionFlowFileDTOS.iterator();
  }
}