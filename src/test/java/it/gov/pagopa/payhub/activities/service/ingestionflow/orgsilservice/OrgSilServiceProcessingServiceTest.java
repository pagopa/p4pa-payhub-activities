package it.gov.pagopa.payhub.activities.service.ingestionflow.orgsilservice;

import it.gov.pagopa.payhub.activities.connector.organization.OrgSilServiceService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.orgsilservice.OrgSilServiceErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.orgsilservice.OrgSilServiceIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.orgsilservice.OrgSilServiceIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.orgsilservice.OrgSilServiceMapper;
import it.gov.pagopa.pu.organization.dto.generated.OrgSilServiceDTO;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

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

    Mockito.when(organizationServiceMock.getOrganizationByIpaCode("IPA123")).thenReturn(Optional.of(organization));

    // When
    OrgSilServiceIngestionFlowFileResult result = service.processOrgSilService(
            Stream.of(dto).iterator(), List.of(),
            ingestionFlowFile, workingDirectory);

    // Then
    Assertions.assertSame(ingestionFlowFile.getOrganizationId(), result.getOrganizationId());
    assertEquals(1L, result.getProcessedRows());
    assertEquals(1L, result.getTotalRows());
  }

  @Test
  void consumeRowWithMatchingIpaCodeProcessesSuccessfully() {
    // Given
    long lineNumber = 1L;
    OrgSilServiceIngestionFlowFileDTO row = mock(OrgSilServiceIngestionFlowFileDTO.class);
    Mockito.when(row.getIpaCode()).thenReturn("IPA123");

    OrgSilServiceIngestionFlowFileResult ingestionFlowFileResult = new OrgSilServiceIngestionFlowFileResult();
    ingestionFlowFileResult.setOrganizationId(123L);

    IngestionFlowFile ingestionFlowFile = mock(IngestionFlowFile.class);

    OrgSilServiceDTO orgSilServiceDTO = mock(OrgSilServiceDTO.class);
    Organization organization = new Organization();
    organization.setIpaCode("IPA123");
    organization.setOrganizationId(123L);
    Mockito.when(organizationServiceMock.getOrganizationByIpaCode("IPA123")).thenReturn(Optional.of(organization));
    Mockito.when(mapperMock.map(row, 123L)).thenReturn(orgSilServiceDTO);

    List<OrgSilServiceErrorDTO> errorList = new ArrayList<>();

    // When
    boolean result = service.consumeRow(lineNumber, row, ingestionFlowFileResult, errorList, ingestionFlowFile);

    // Then
    Assertions.assertTrue(result);
    Mockito.verify(orgSilServiceServiceMock).createOrUpdateOrgSilService(orgSilServiceDTO);
    Assertions.assertTrue(errorList.isEmpty());
  }

  @Test
  void processOrgSilServiceWithErrors() {

    // Given
    String ipaCode = "IPA123";

    IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
    ingestionFlowFile.setOrganizationId(123L);
    OrgSilServiceIngestionFlowFileDTO dto = mock(OrgSilServiceIngestionFlowFileDTO.class);
    Mockito.when(dto.getIpaCode()).thenReturn(ipaCode);

    OrgSilServiceDTO orgSilServiceDTO = new OrgSilServiceDTO();

    Mockito.when(mapperMock.map(dto, 123L)).thenReturn(orgSilServiceDTO);

    Organization organization = new Organization();
    organization.setIpaCode(ipaCode);
    organization.setOrganizationId(123L);
    Mockito.when(organizationServiceMock.getOrganizationByIpaCode("IPA123")).thenReturn(Optional.of(organization));

    doThrow(new RuntimeException("Processing error"))
            .when(orgSilServiceServiceMock).createOrUpdateOrgSilService(orgSilServiceDTO);

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

    Mockito.verify(orgSilServiceServiceMock).createOrUpdateOrgSilService(orgSilServiceDTO);
    Mockito.verify(errorsArchiverServiceMock).archiveErrorFiles(workingDirectory, ingestionFlowFile);
  }
}