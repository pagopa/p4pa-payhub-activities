package it.gov.pagopa.payhub.activities.activity.ingestionflow.debtpositiontypeorg;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontypeorg.DebtPositionTypeOrgIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontypeorg.DebtPositionTypeOrgIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.InvalidIngestionFileException;
import it.gov.pagopa.payhub.activities.service.files.CsvService;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.debtpositiontypeorg.DebtPositionTypeOrgProcessingService;
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
class DebtPositionTypeOrgIngestionActivityTest {


  @Mock
  private CsvService csvServiceMock;
  @Mock
  private DebtPositionTypeOrgProcessingService debtPositionTypeOrgProcessingServiceMock;
  @Mock
  private IngestionFlowFileService ingestionFlowFileServiceMock;
  @Mock
  private IngestionFlowFileRetrieverService ingestionFlowFileRetrieverServiceMock;
  @Mock
  private FileArchiverService fileArchiverServiceMock;

  private DebtPositionTypeOrgIngestionActivity activity;

  @TempDir
  private Path workingDir;

  @BeforeEach
  void setUp() {
    activity = new DebtPositionTypeOrgIngestionActivityImpl(
        ingestionFlowFileServiceMock,
        ingestionFlowFileRetrieverServiceMock,
        fileArchiverServiceMock,
        csvServiceMock,
        debtPositionTypeOrgProcessingServiceMock
    );
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
        ingestionFlowFileServiceMock,
        ingestionFlowFileRetrieverServiceMock,
        fileArchiverServiceMock,
        csvServiceMock,
        debtPositionTypeOrgProcessingServiceMock
    );
  }

  @Test
  void handleRetrievedFilesSuccessfully() throws Exception {
    Long ingestionFlowFileId = 1L;
    Long organizationId = 10L;
    IngestionFlowFile ingestionFlowFileDTO = buildIngestionFlowFile();
    ingestionFlowFileDTO.setOrganizationId(organizationId);
    ingestionFlowFileDTO.setFilePathName(workingDir.toString());
    ingestionFlowFileDTO.setIngestionFlowFileType(IngestionFlowFileTypeEnum.DEBT_POSITIONS_TYPE_ORG);
    Iterator<DebtPositionTypeOrgIngestionFlowFileDTO> iterator = buildDebtPositionTypeOrgIngestionFlowFileDTO();
    List<CsvException> readerExceptions = List.of();

    Path filePath = Files.createFile(Path.of(ingestionFlowFileDTO.getFilePathName()).resolve(ingestionFlowFileDTO.getFileName()));
    List<Path> mockedListPath = List.of(filePath);

    Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId))
        .thenReturn(Optional.of(ingestionFlowFileDTO));

    doReturn(mockedListPath).when(ingestionFlowFileRetrieverServiceMock)
        .retrieveAndUnzipFile(ingestionFlowFileDTO.getOrganizationId(), Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());

    Mockito.when(csvServiceMock.readCsv(eq(filePath), eq(DebtPositionTypeOrgIngestionFlowFileDTO.class), any(), isNull()))
        .thenAnswer(invocation -> {
          BiFunction<Iterator<DebtPositionTypeOrgIngestionFlowFileDTO>, List<CsvException>, DebtPositionTypeOrgIngestionFlowFileResult> rowProcessor = invocation.getArgument(2);
          return rowProcessor.apply(iterator, readerExceptions);
        });

    Mockito.when(debtPositionTypeOrgProcessingServiceMock.processDebtPositionTypeOrg(same(iterator), same(readerExceptions), eq(ingestionFlowFileDTO), eq(filePath.getParent())))
        .thenReturn(buildDebtPositionTypeOrgIngestionFlowFileResult());

    // When
    DebtPositionTypeOrgIngestionFlowFileResult result = activity.processFile(ingestionFlowFileId);

    // Then
    Assertions.assertEquals(
        buildDebtPositionTypeOrgIngestionFlowFileResult(),
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
    ingestionFlowFileDTO.setIngestionFlowFileType(IngestionFlowFileTypeEnum.DEBT_POSITIONS_TYPE_ORG);
    Iterator<DebtPositionTypeOrgIngestionFlowFileDTO> iterator = buildDebtPositionTypeOrgIngestionFlowFileDTO();
    List<CsvException> readerExceptions = List.of();

    Path filePath = Files.createFile(Path.of(ingestionFlowFileDTO.getFilePathName()).resolve(ingestionFlowFileDTO.getFileName()));
    List<Path> mockedListPath = List.of(filePath);

    Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId))
        .thenReturn(Optional.of(ingestionFlowFileDTO));

    doReturn(mockedListPath).when(ingestionFlowFileRetrieverServiceMock)
        .retrieveAndUnzipFile(ingestionFlowFileDTO.getOrganizationId(), Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());

    Mockito.when(csvServiceMock.readCsv(eq(filePath), eq(DebtPositionTypeOrgIngestionFlowFileDTO.class), any(), isNull()))
        .thenAnswer(invocation -> {
          BiFunction<Iterator<DebtPositionTypeOrgIngestionFlowFileDTO>, List<CsvException>, DebtPositionTypeOrgIngestionFlowFileResult> rowProcessor = invocation.getArgument(2);
          return rowProcessor.apply(iterator, readerExceptions);
        });

    Mockito.when(debtPositionTypeOrgProcessingServiceMock.processDebtPositionTypeOrg(same(iterator), same(readerExceptions), eq(ingestionFlowFileDTO), eq(filePath.getParent())))
        .thenThrow(new RestClientException("Error"));

    // When & Then
    assertThrows(InvalidIngestionFileException.class, () -> activity.processFile(ingestionFlowFileId));
  }

  private DebtPositionTypeOrgIngestionFlowFileResult buildDebtPositionTypeOrgIngestionFlowFileResult() {
    return DebtPositionTypeOrgIngestionFlowFileResult.builder()
        .organizationId(10L)
        .processedRows(20L)
        .totalRows(30L)
        .discardedFileName("dicardedFileName")
        .errorDescription("errorDescription")
        .brokerFiscalCode("BrokerFiscalCode")
        .build();
  }

  private Iterator<DebtPositionTypeOrgIngestionFlowFileDTO> buildDebtPositionTypeOrgIngestionFlowFileDTO() {
    List<DebtPositionTypeOrgIngestionFlowFileDTO> debtPositionTypeOrgIngestionFlowFileDTOS = List.of(
        DebtPositionTypeOrgIngestionFlowFileDTO.builder()
            .code("code1")
            .description("description1")
            .ioTemplateMessage("ioTemplateMessage1")
            .ioTemplateSubject("ioTemplateSubject1")
            .flagAnonymousFiscalCode(false)
            .flagNotifyIo(false)
            .flagMandatoryDueDate(false)
            .build(),
            DebtPositionTypeOrgIngestionFlowFileDTO.builder()
            .code("code2")
            .description("description2")
            .ioTemplateMessage("ioTemplateMessage2")
            .ioTemplateSubject("ioTemplateSubject2")
            .flagAnonymousFiscalCode(false)
            .flagNotifyIo(false)
            .flagMandatoryDueDate(false)
            .build()
    );

    return debtPositionTypeOrgIngestionFlowFileDTOS.iterator();
  }
}
