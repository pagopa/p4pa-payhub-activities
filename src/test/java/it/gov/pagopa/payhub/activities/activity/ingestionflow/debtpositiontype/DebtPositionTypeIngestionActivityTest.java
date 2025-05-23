package it.gov.pagopa.payhub.activities.activity.ingestionflow.debtpositiontype;

import static it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker.buildIngestionFlowFile;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doReturn;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontype.DebtPositionTypeIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontype.DebtPositionTypeIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.InvalidIngestionFileException;
import it.gov.pagopa.payhub.activities.service.files.CsvService;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.debtpositiontype.DebtPositionTypeProcessingService;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile.IngestionFlowFileTypeEnum;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
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

@ExtendWith(MockitoExtension.class)
class DebtPositionTypeIngestionActivityTest {


  @Mock
  private CsvService csvServiceMock;
  @Mock
  private DebtPositionTypeProcessingService debtPositionTypeProcessingServiceMock;
  @Mock
  private IngestionFlowFileService ingestionFlowFileServiceMock;
  @Mock
  private IngestionFlowFileRetrieverService ingestionFlowFileRetrieverServiceMock;
  @Mock
  private FileArchiverService fileArchiverServiceMock;

  private DebtPositionTypeIngestionActivityImpl activity;

  @TempDir
  private Path workingDir;

  @BeforeEach
  void setUp() {
    activity = new DebtPositionTypeIngestionActivityImpl(
        ingestionFlowFileServiceMock,
        ingestionFlowFileRetrieverServiceMock,
        fileArchiverServiceMock,
        csvServiceMock,
        debtPositionTypeProcessingServiceMock
    );
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
        ingestionFlowFileServiceMock,
        ingestionFlowFileRetrieverServiceMock,
        fileArchiverServiceMock,
        csvServiceMock,
        debtPositionTypeProcessingServiceMock
    );
  }

  @Test
  void handleRetrievedFilesSuccessfully() throws Exception {
    Long ingestionFlowFileId = 1L;
    Long organizationId = 10L;
    IngestionFlowFile ingestionFlowFileDTO = buildIngestionFlowFile();
    ingestionFlowFileDTO.setOrganizationId(organizationId);
    ingestionFlowFileDTO.setFilePathName(workingDir.toString());
    ingestionFlowFileDTO.setIngestionFlowFileType(IngestionFlowFileTypeEnum.DEBT_POSITIONS_TYPE);
    Iterator<DebtPositionTypeIngestionFlowFileDTO> iterator = buildDebtPositionTypeIngestionFlowFileDTO();
    List<CsvException> readerExceptions = List.of();

    Path filePath = Files.createFile(Path.of(ingestionFlowFileDTO.getFilePathName()).resolve(ingestionFlowFileDTO.getFileName()));
    List<Path> mockedListPath = List.of(filePath);

    Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId))
        .thenReturn(Optional.of(ingestionFlowFileDTO));

    doReturn(mockedListPath).when(ingestionFlowFileRetrieverServiceMock)
        .retrieveAndUnzipFile(ingestionFlowFileDTO.getOrganizationId(), Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());

    Mockito.when(csvServiceMock.readCsv(eq(filePath), eq(DebtPositionTypeIngestionFlowFileDTO.class), any()))
        .thenAnswer(invocation -> {
          BiFunction<Iterator<DebtPositionTypeIngestionFlowFileDTO>, List<CsvException>, DebtPositionTypeIngestionFlowFileResult> rowProcessor = invocation.getArgument(2);
          return rowProcessor.apply(iterator, readerExceptions);
        });

    Mockito.when(debtPositionTypeProcessingServiceMock.processDebtPositionType(same(iterator), same(readerExceptions), eq(ingestionFlowFileDTO), eq(filePath.getParent())))
        .thenReturn(buildDebtPositionTypeIngestionFlowFileResult());

    // When
    DebtPositionTypeIngestionFlowFileResult result = activity.processFile(ingestionFlowFileId);

    // Then
    Assertions.assertEquals(
        buildDebtPositionTypeIngestionFlowFileResult(),
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
    ingestionFlowFileDTO.setIngestionFlowFileType(IngestionFlowFileTypeEnum.DEBT_POSITIONS_TYPE);
    Iterator<DebtPositionTypeIngestionFlowFileDTO> iterator = buildDebtPositionTypeIngestionFlowFileDTO();
    List<CsvException> readerExceptions = List.of();

    Path filePath = Files.createFile(Path.of(ingestionFlowFileDTO.getFilePathName()).resolve(ingestionFlowFileDTO.getFileName()));
    List<Path> mockedListPath = List.of(filePath);

    Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId))
        .thenReturn(Optional.of(ingestionFlowFileDTO));

    doReturn(mockedListPath).when(ingestionFlowFileRetrieverServiceMock)
        .retrieveAndUnzipFile(ingestionFlowFileDTO.getOrganizationId(), Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());

    Mockito.when(csvServiceMock.readCsv(eq(filePath), eq(DebtPositionTypeIngestionFlowFileDTO.class), any()))
        .thenAnswer(invocation -> {
          BiFunction<Iterator<DebtPositionTypeIngestionFlowFileDTO>, List<CsvException>, DebtPositionTypeIngestionFlowFileResult> rowProcessor = invocation.getArgument(2);
          return rowProcessor.apply(iterator, readerExceptions);
        });

    Mockito.when(debtPositionTypeProcessingServiceMock.processDebtPositionType(same(iterator), same(readerExceptions), eq(ingestionFlowFileDTO), eq(filePath.getParent())))
        .thenThrow(new RestClientException("Error"));

    // When & Then
    assertThrows(InvalidIngestionFileException.class, () -> activity.processFile(ingestionFlowFileId));
  }

  private DebtPositionTypeIngestionFlowFileResult buildDebtPositionTypeIngestionFlowFileResult() {
    return DebtPositionTypeIngestionFlowFileResult.builder()
        .processedRows(20L)
        .totalRows(30L)
        .discardedFileName("dicardedFileName")
        .errorDescription("errorDescription")
        .brokerFiscalCode("BrokerFiscalCode")
        .build();
  }

  private Iterator<DebtPositionTypeIngestionFlowFileDTO> buildDebtPositionTypeIngestionFlowFileDTO() {
    List<DebtPositionTypeIngestionFlowFileDTO> debtPositionTypeIngestionFlowFileDTOS = List.of(
        DebtPositionTypeIngestionFlowFileDTO.builder()
            .debtPositionTypeCode("code1")
            .description("description1")
            .orgType("orgType1")
            .brokerCf("brokerCf1")
            .collectingReason("collectingReason1")
            .serviceType("serviceType1")
            .taxonomyCode("taxonomyCode1")
            .ioTemplateMessage("ioTemplateMessage1")
            .ioTemplateSubject("ioTemplateSubject1")
            .flagAnonymousFiscalCode(false)
            .flagNotifyIo(false)
            .flagMandatoryDueDate(false)
            .macroArea("macroArea1")
            .build(),
        DebtPositionTypeIngestionFlowFileDTO.builder()
            .debtPositionTypeCode("code2")
            .description("description2")
            .orgType("orgType2")
            .brokerCf("brokerCf2")
            .collectingReason("collectingReason2")
            .serviceType("serviceType2")
            .taxonomyCode("taxonomyCode2")
            .ioTemplateMessage("ioTemplateMessage2")
            .ioTemplateSubject("ioTemplateSubject2")
            .flagAnonymousFiscalCode(false)
            .flagNotifyIo(false)
            .flagMandatoryDueDate(false)
            .macroArea("macroArea2")
            .build()
    );

    return debtPositionTypeIngestionFlowFileDTOS.iterator();
  }





}
