package it.gov.pagopa.payhub.activities.activity.ingestionflow.organization;

import static it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker.buildIngestionFlowFile;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doReturn;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.ingestion.organization.OrganizationIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.organization.OrganizationIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.InvalidIngestionFileException;
import it.gov.pagopa.payhub.activities.service.files.CsvService;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.organization.OrganizationProcessingService;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile.IngestionFlowFileTypeEnum;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
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
class OrganizationIngestionActivityImplTest {

  @Mock
  private CsvService csvServiceMock;
  @Mock
  private OrganizationProcessingService organizationProcessingServiceMock;
  @Mock
  private IngestionFlowFileService ingestionFlowFileServiceMock;
  @Mock
  private IngestionFlowFileRetrieverService ingestionFlowFileRetrieverServiceMock;
  @Mock
  private FileArchiverService fileArchiverServiceMock;

  private OrganizationIngestionActivityImpl activity;

  @TempDir
  private Path workingDir;

  @BeforeEach
  void setUp() {
    activity = new OrganizationIngestionActivityImpl(
        ingestionFlowFileServiceMock,
        ingestionFlowFileRetrieverServiceMock,
        fileArchiverServiceMock,
        csvServiceMock,
        organizationProcessingServiceMock
    );
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
        ingestionFlowFileServiceMock,
        ingestionFlowFileRetrieverServiceMock,
        fileArchiverServiceMock,
        csvServiceMock,
        organizationProcessingServiceMock
    );
  }

  @Test
  void handleRetrievedFilesSuccessfully() throws Exception {
    Long ingestionFlowFileId = 1L;
    Long organizationId = 10L;
    IngestionFlowFile ingestionFlowFileDTO = buildIngestionFlowFile();
    ingestionFlowFileDTO.setOrganizationId(organizationId);
    ingestionFlowFileDTO.setFilePathName(workingDir.toString());
    ingestionFlowFileDTO.setIngestionFlowFileType(IngestionFlowFileTypeEnum.ORGANIZATIONS);
    Iterator<OrganizationIngestionFlowFileDTO> iterator = buildOrganizationIngestionFlowFileDTO();
    List<CsvException> readerExceptions = List.of();

    Path filePath = Files.createFile(Path.of(ingestionFlowFileDTO.getFilePathName()).resolve(ingestionFlowFileDTO.getFileName()));
    List<Path> mockedListPath = List.of(filePath);

    Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId))
        .thenReturn(Optional.of(ingestionFlowFileDTO));

    doReturn(mockedListPath).when(ingestionFlowFileRetrieverServiceMock)
        .retrieveAndUnzipFile(ingestionFlowFileDTO.getOrganizationId(), Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());

    Mockito.when(csvServiceMock.readCsv(eq(filePath), eq(OrganizationIngestionFlowFileDTO.class), any(), any()))
        .thenAnswer(invocation -> {
          BiFunction<Iterator<OrganizationIngestionFlowFileDTO>, List<CsvException>, OrganizationIngestionFlowFileResult> rowProcessor = invocation.getArgument(2);
          return rowProcessor.apply(iterator, readerExceptions);
        });

    Mockito.when(organizationProcessingServiceMock.processOrganization(same(iterator), same(readerExceptions), eq(ingestionFlowFileDTO), eq(filePath.getParent())))
        .thenReturn(buildOrganizationIngestionFlowFileResult());

    // When
    OrganizationIngestionFlowFileResult result = activity.processFile(ingestionFlowFileId);

    // Then
    Assertions.assertEquals(
        buildOrganizationIngestionFlowFileResult(),
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
    ingestionFlowFileDTO.setIngestionFlowFileType(IngestionFlowFileTypeEnum.ORGANIZATIONS);
    Iterator<OrganizationIngestionFlowFileDTO> iterator = buildOrganizationIngestionFlowFileDTO();
    List<CsvException> readerExceptions = List.of();

    Path filePath = Files.createFile(Path.of(ingestionFlowFileDTO.getFilePathName()).resolve(ingestionFlowFileDTO.getFileName()));
    List<Path> mockedListPath = List.of(filePath);

    Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId))
        .thenReturn(Optional.of(ingestionFlowFileDTO));

    doReturn(mockedListPath).when(ingestionFlowFileRetrieverServiceMock)
        .retrieveAndUnzipFile(ingestionFlowFileDTO.getOrganizationId(), Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());

    Mockito.when(csvServiceMock.readCsv(eq(filePath), eq(OrganizationIngestionFlowFileDTO.class), any(), any()))
        .thenAnswer(invocation -> {
          BiFunction<Iterator<OrganizationIngestionFlowFileDTO>, List<CsvException>, OrganizationIngestionFlowFileResult> rowProcessor = invocation.getArgument(2);
          return rowProcessor.apply(iterator, readerExceptions);
        });

    Mockito.when(organizationProcessingServiceMock.processOrganization(same(iterator), same(readerExceptions), eq(ingestionFlowFileDTO), eq(filePath.getParent())))
        .thenThrow(new RestClientException("Error"));

    // When & Then
    assertThrows(InvalidIngestionFileException.class, () -> activity.processFile(ingestionFlowFileId));
  }

  private OrganizationIngestionFlowFileResult buildOrganizationIngestionFlowFileResult() {
    return OrganizationIngestionFlowFileResult.builder()
        .processedRows(20L)
        .totalRows(30L)
        .discardedFileName("dicardedFileName")
        .errorDescription("errorDescription")
        .organizationIpaCodeList(List.of(
            "ipa1",
            "ipa2"))
        .build();
  }

  private Iterator<OrganizationIngestionFlowFileDTO> buildOrganizationIngestionFlowFileDTO() {
    List<OrganizationIngestionFlowFileDTO> organizationIngestionFlowFileDTOList = List.of(
        OrganizationIngestionFlowFileDTO.builder()
            .ipaCode("ipaCode1")
            .orgFiscalCode("orgFiscalCode1")
            .orgName("orgName1")
            .orgTypeCode("orgTypeCode1")
            .orgEmail("orgEmail1")
            .iban("iban1")
            .postalIban("postalIban1")
            .segregationCode("segregationCode1")
            .cbillInterBankCode("cbillInterBankCode1")
            .orgLogo("orgLogo1")
            .status("status1")
            .additionalLanguage("additionalLanguage1")
            .startDate(LocalDateTime.now())
            .brokerCf("brokerCf1")
            .ioApiKey("ioApiKey1")
            .flagNotifyIo(true)
            .flagNotifyOutcomePush(true)
            .sendApiKey("sendApiKey1")
            .build(),
        OrganizationIngestionFlowFileDTO.builder()
            .ipaCode("ipaCode2")
            .orgFiscalCode("orgFiscalCode2")
            .orgName("orgName2")
            .orgTypeCode("orgTypeCode2")
            .orgEmail("orgEmail2")
            .iban("iban2")
            .postalIban("postalIban2")
            .segregationCode("segregationCode2")
            .cbillInterBankCode("cbillInterBankCode2")
            .orgLogo("orgLogo2")
            .status("status2")
            .additionalLanguage("additionalLanguage2")
            .startDate(LocalDateTime.now())
            .brokerCf("brokerCf2")
            .ioApiKey("ioApiKey2")
            .flagNotifyIo(true)
            .flagNotifyOutcomePush(true)
            .sendApiKey("sendApiKey2")
            .build()
    );

    return organizationIngestionFlowFileDTOList.iterator();
  }




}
