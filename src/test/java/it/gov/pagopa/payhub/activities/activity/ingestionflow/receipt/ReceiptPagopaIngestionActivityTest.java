package it.gov.pagopa.payhub.activities.activity.ingestionflow.receipt;

import it.gov.pagopa.payhub.activities.connector.debtposition.ReceiptService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.ingestion.receipt.ReceiptPagopaIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.InvalidIngestionFileException;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.receipt.ReceiptMapper;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.receipt.ReceiptParserService;
import it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker;
import it.gov.pagopa.payhub.activities.xsd.receipt.pagopa.PaSendRTV2Request;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptWithAdditionalNodeDataDTO;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ReceiptPagopaIngestionActivityTest {

  @Mock
  private IngestionFlowFileService ingestionFlowFileServiceMock;
  @Mock
  private IngestionFlowFileRetrieverService ingestionFlowFileRetrieverServiceMock;
  @Mock
  private ReceiptParserService receiptParserServiceMock;
  @Mock
  private FileArchiverService fileArchiverServiceMock;
  @Mock
  private ReceiptService receiptServiceMock;
  @Mock
  private ReceiptMapper receiptMapperMock;

  @InjectMocks
  private ReceiptPagopaIngestionActivityImpl receiptPagopaIngestionActivity;

  @TempDir
  Path workingDir;


  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
      ingestionFlowFileServiceMock,
      ingestionFlowFileRetrieverServiceMock,
      receiptParserServiceMock,
      fileArchiverServiceMock,
      receiptServiceMock,
      receiptMapperMock
    );
  }

  @Test
  void givenValidIngestionFlowWhenProcessFileThenOk() throws IOException {
    // Given
    Long ingestionFlowFileId = 1L;
    IngestionFlowFile ingestionFlowFileDTO = buildIngestionFlowFile(ingestionFlowFileId);

    Path filePath = Files.createFile(Path.of(ingestionFlowFileDTO.getFilePathName()).resolve(ingestionFlowFileDTO.getFileName()));
    List<Path> mockedListPath = List.of(filePath);

    Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId))
      .thenReturn(Optional.of(ingestionFlowFileDTO));

    Mockito.when(ingestionFlowFileRetrieverServiceMock.retrieveAndUnzipFile(
        ingestionFlowFileDTO.getOrganizationId(), Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName()))
      .thenReturn(mockedListPath);

    Mockito.doNothing().when(fileArchiverServiceMock).archive(ingestionFlowFileDTO);

    PaSendRTV2Request paSendRTV2Request = new PaSendRTV2Request();

    Mockito.when(receiptParserServiceMock.parseReceiptPagopaFile(filePath, ingestionFlowFileDTO))
      .thenReturn(paSendRTV2Request);

    ReceiptWithAdditionalNodeDataDTO receiptWithAdditionalNodeDataDTO = new ReceiptWithAdditionalNodeDataDTO();

    Mockito.when(receiptMapperMock.map(ingestionFlowFileDTO, paSendRTV2Request)).thenReturn(receiptWithAdditionalNodeDataDTO);

    ReceiptDTO receiptDTO = new ReceiptDTO();
    receiptDTO.setReceiptId(1L);

    ReceiptPagopaIngestionFlowFileResult expectedResult = ReceiptPagopaIngestionFlowFileResult.builder()
            .receiptDTO(receiptWithAdditionalNodeDataDTO)
            .organizationId(ingestionFlowFileDTO.getOrganizationId())
            .fileVersion("1.0.0")
            .totalRows(1L)
            .processedRows(1L)
            .operatorExternalUserId("OPERATORID")
            .fileSize(100L)
            .build();

    Mockito.when(receiptServiceMock.createReceipt(receiptWithAdditionalNodeDataDTO)).thenReturn(receiptDTO);

    // When
    ReceiptPagopaIngestionFlowFileResult result = receiptPagopaIngestionActivity.processFile(ingestionFlowFileId);

    // Then
    Assertions.assertEquals(expectedResult, result);
    Assertions.assertFalse(filePath.toFile().exists());
  }

  @Test
  void givenReceiptProcessingErrorWhenProcessFileThenError() throws IOException {
    // Given
    Long ingestionFlowFileId = 1L;
    IngestionFlowFile ingestionFlowFileDTO = buildIngestionFlowFile(ingestionFlowFileId);

    Path filePath = Files.createFile(Path.of(ingestionFlowFileDTO.getFilePathName()).resolve(ingestionFlowFileDTO.getFileName()));
    List<Path> mockedListPath = List.of(filePath);

    Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId))
      .thenReturn(Optional.of(ingestionFlowFileDTO));

    Mockito.when(ingestionFlowFileRetrieverServiceMock.retrieveAndUnzipFile(
        ingestionFlowFileDTO.getOrganizationId(), Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName()))
      .thenReturn(mockedListPath);

    Mockito.when(receiptParserServiceMock.parseReceiptPagopaFile(filePath, ingestionFlowFileDTO))
      .thenThrow(new RuntimeException("test error"));

    // When
    RuntimeException response = Assertions.assertThrows(RuntimeException.class, () -> receiptPagopaIngestionActivity.processFile(ingestionFlowFileId));

    // Then
    Assertions.assertEquals("test error", response.getMessage());
    Mockito.verify(ingestionFlowFileServiceMock, Mockito.times(1)).findById(ingestionFlowFileId);
    Mockito.verify(ingestionFlowFileRetrieverServiceMock, Mockito.times(1)).retrieveAndUnzipFile(
      ingestionFlowFileDTO.getOrganizationId(), Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());
    Mockito.verify(receiptParserServiceMock, Mockito.times(1)).parseReceiptPagopaFile(filePath, ingestionFlowFileDTO);

    Assertions.assertFalse(filePath.toFile().exists());
  }

  @Test
  void givenMultipleIngestionFilesWhenProcessFileThenError() throws IOException {
    // Given
    Long ingestionFlowFileId = 1L;
    IngestionFlowFile ingestionFlowFileDTO = buildIngestionFlowFile(ingestionFlowFileId);

    Path filePath = Files.createFile(Path.of(ingestionFlowFileDTO.getFilePathName()).resolve(ingestionFlowFileDTO.getFileName()));
    List<Path> mockedListPath = List.of(filePath, filePath);

    Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId))
      .thenReturn(Optional.of(ingestionFlowFileDTO));

    Mockito.when(ingestionFlowFileRetrieverServiceMock.retrieveAndUnzipFile(
        ingestionFlowFileDTO.getOrganizationId(), Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName()))
      .thenReturn(mockedListPath);

    // When, Then
    Assertions.assertThrows(InvalidIngestionFileException.class, () -> receiptPagopaIngestionActivity.processFile(ingestionFlowFileId));

    Assertions.assertFalse(filePath.toFile().exists());
  }

  private IngestionFlowFile buildIngestionFlowFile(Long ingestionFlowFileId) {
    return IngestionFlowFileFaker.buildIngestionFlowFile()
      .ingestionFlowFileId(ingestionFlowFileId)
      .ingestionFlowFileType(IngestionFlowFile.IngestionFlowFileTypeEnum.RECEIPT_PAGOPA)
      .filePathName(workingDir.toString())
      .fileName("RT_12345678901234567.xml")
      .organizationId(0L);
  }
}