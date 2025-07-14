package it.gov.pagopa.payhub.activities.service.ingestionflow.orgsilservice;

import it.gov.pagopa.payhub.activities.connector.organization.OrgSilServiceService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.orgsilservice.OrgSilServiceErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.orgsilservice.OrgSilServiceIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.orgsilservice.OrgSilServiceIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.orgsilservice.OrgSilServiceMapper;
import it.gov.pagopa.pu.organization.dto.generated.OrgSilServiceDTO;
import it.gov.pagopa.pu.organization.dto.generated.OrgSilServiceType;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker.buildIngestionFlowFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrgSilServiceProcessingServiceTest {


  @Mock
  private OrgSilServiceErrorsArchiverService errorsArchiverServiceMock;

  @Mock
  private Path workingDirectory;

  @Mock
  private OrgSilServiceMapper mapperMock;

  @Mock
  private OrganizationService organizationServiceMock;

  @Mock
  private OrgSilServiceService orgSilServiceServiceMock;
  
  private OrgSilServiceProcessingService service;

  @BeforeEach
  void setUp() {
    service = new OrgSilServiceProcessingService(mapperMock, errorsArchiverServiceMock,
        organizationServiceMock, orgSilServiceServiceMock);
  }

  @Test
  void processOrgSilServiceWithIpaErrors() {
    // Given
    String ipaCode = "IPA123";
    String ipaWrong = "IPA123_WRONG";

    IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
    ingestionFlowFile.setOrganizationId(123L);
    OrgSilServiceIngestionFlowFileDTO dto = mock(OrgSilServiceIngestionFlowFileDTO.class);
    Mockito.when(dto.getIpaCode()).thenReturn(ipaCode);

    Organization organization = new Organization();
    organization.setIpaCode(ipaWrong);

    Mockito.when(organizationServiceMock.getOrganizationById(123L)).thenReturn(Optional.of(organization));
    Mockito.when(errorsArchiverServiceMock.archiveErrorFiles(workingDirectory, ingestionFlowFile)).thenReturn("errors.zip");

    // When
    OrgSilServiceIngestionFlowFileResult result = service.processOrgSilService(
            Stream.of(dto).iterator(), List.of(),
            ingestionFlowFile, workingDirectory);

    // Then
    Assertions.assertSame(ingestionFlowFile.getOrganizationId(), result.getOrganizationId());
    assertEquals(0L, result.getProcessedRows());
    assertEquals(1L, result.getTotalRows());
    assertEquals("Some rows have failed", result.getErrorDescription());
    assertEquals("errors.zip", result.getDiscardedFileName());
  }

  @Test
  void consumeRowWithMatchingIpaCodeProcessesSuccessfully() {
    // Given
    long lineNumber = 1L;
    OrgSilServiceIngestionFlowFileDTO row = OrgSilServiceIngestionFlowFileDTO.builder()
            .ipaCode("IPA123")
            .applicationName("TestApp")
            .serviceUrl("http://test.url")
            .serviceType("ACTUALIZATION")
            .flagLegacy(true)
            .build();
    OrgSilServiceDTO orgSilServiceDTOMapped = OrgSilServiceDTO.builder()
                    .organizationId(123L)
                    .applicationName("TestApp")
                    .serviceUrl("http://test.url")
                    .serviceType(OrgSilServiceType.ACTUALIZATION)
                    .flagLegacy(true)
                    .authConfig(null)
                    .build();

    Mockito.when(mapperMock.map(row, 123L)).thenReturn(orgSilServiceDTOMapped);

    OrgSilServiceIngestionFlowFileResult ingestionFlowFileResult = new OrgSilServiceIngestionFlowFileResult();
    ingestionFlowFileResult.setOrganizationId(123L);
    ingestionFlowFileResult.setIpaCode("IPA123");

    IngestionFlowFile ingestionFlowFile = mock(IngestionFlowFile.class);

    Organization organization = new Organization();
    organization.setIpaCode("IPA123");
    organization.setOrganizationId(123L);
    Mockito.when(organizationServiceMock.getOrganizationByIpaCode("IPA123")).thenReturn(Optional.of(organization));

    List<OrgSilServiceErrorDTO> errorList = new ArrayList<>();

    // When
    boolean result = service.consumeRow(lineNumber, row, ingestionFlowFileResult, errorList, ingestionFlowFile);

    // Then
    Assertions.assertTrue(result);
    Mockito.verify(orgSilServiceServiceMock).createOrUpdateOrgSilService(any());
    Assertions.assertTrue(errorList.isEmpty());
  }

  @Test
  void processOrgSilServiceWithErrors() {
    // Given
    String ipaCode = "IPA123";
    IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
    ingestionFlowFile.setOrganizationId(123L);
    OrgSilServiceIngestionFlowFileDTO dto = OrgSilServiceIngestionFlowFileDTO.builder()
            .ipaCode("IPA123")
            .applicationName("TestApp")
            .serviceUrl("http://test.url")
            .serviceType("ACTUALIZATION")
            .flagLegacy(true)
            .build();
    Organization organization = new Organization();
    organization.setIpaCode(ipaCode);
    organization.setOrganizationId(123L);
    Mockito.when(organizationServiceMock.getOrganizationById(123L)).thenReturn(Optional.of(organization));

    Mockito.when(errorsArchiverServiceMock.archiveErrorFiles(workingDirectory, ingestionFlowFile))
            .thenReturn("zipFileName.csv");


    // When
    OrgSilServiceIngestionFlowFileResult result = service.processOrgSilService(
            Stream.of(dto).iterator(), List.of(),
            ingestionFlowFile, workingDirectory);

    // Then
    assertEquals(1, result.getTotalRows());
    assertEquals(0, result.getProcessedRows());
    assertEquals("Some rows have failed", result.getErrorDescription());
    assertEquals("zipFileName.csv", result.getDiscardedFileName());
    verify(errorsArchiverServiceMock).writeErrors(eq(workingDirectory), eq(ingestionFlowFile), any());
    Mockito.verify(errorsArchiverServiceMock).archiveErrorFiles(workingDirectory, ingestionFlowFile);
  }

  @Test
  void consumeRowWithMissingOrganizationAddsError() {
    // Given
    long lineNumber = 2L;
    OrgSilServiceIngestionFlowFileDTO row = mock(OrgSilServiceIngestionFlowFileDTO.class);
    Mockito.when(row.getIpaCode()).thenReturn("IPA_NOT_FOUND");
    Mockito.when(row.getApplicationName()).thenReturn("TestApp");

    OrgSilServiceIngestionFlowFileResult ingestionFlowFileResult = new OrgSilServiceIngestionFlowFileResult();
    ingestionFlowFileResult.setOrganizationId(456L);
    ingestionFlowFileResult.setIpaCode("IPA_CODE");
    IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
    ingestionFlowFile.setFileName("testfile.csv");

    List<OrgSilServiceErrorDTO> errorList = new ArrayList<>();

    // When
    service.consumeRow(lineNumber, row, ingestionFlowFileResult, errorList, ingestionFlowFile);

    // Then
    OrgSilServiceErrorDTO error = errorList.get(0);
    Assertions.assertEquals("testfile.csv", error.getFileName());
    Assertions.assertEquals("IPA_NOT_FOUND", error.getIpaCode());
    Assertions.assertEquals("TestApp", error.getApplicationName());
    Assertions.assertEquals(lineNumber, error.getRowNumber());
    Assertions.assertEquals("ORGANIZATION_IPA_DOES_NOT_MATCH", error.getErrorCode());
    Assertions.assertTrue(error.getErrorMessage().contains("Organization IPA code IPA_NOT_FOUND does not match with the one in the ingestion flow file IPA_CODE"));
  }
}