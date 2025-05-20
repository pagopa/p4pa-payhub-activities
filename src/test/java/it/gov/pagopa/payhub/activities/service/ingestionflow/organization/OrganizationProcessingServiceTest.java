package it.gov.pagopa.payhub.activities.service.ingestionflow.organization;

import static it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker.buildIngestionFlowFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.organization.OrganizationErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.organization.OrganizationIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.organization.OrganizationIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.organization.OrganizationMapper;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationRequestBody;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationStatus;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrganizationProcessingServiceTest {


  @Mock
  private OrganizationErrorsArchiverService errorsArchiverServiceMock;

  @Mock
  private Path workingDirectory;

  @Mock
  private OrganizationMapper mapperMock;

  @Mock
  private OrganizationService organizationServiceMock;

  @Mock

  private OrganizationProcessingService service;

  @BeforeEach
  void setUp() {
    service = new OrganizationProcessingService(mapperMock, errorsArchiverServiceMock,
        organizationServiceMock);
  }

  @AfterEach
  void verifyNoMoreInteractions(){
    Mockito.verifyNoMoreInteractions(
        mapperMock,
        errorsArchiverServiceMock,
        organizationServiceMock);
  }

  @Test
  void processOrganizationWithNoErrors() {
    // Given
    IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
    OrganizationIngestionFlowFileDTO dto = mock(OrganizationIngestionFlowFileDTO.class);
    Mockito.when(dto.getBrokerCf()).thenReturn("brokerFC");

    Organization orgFromService = Organization.builder()
        .brokerId(1L)
        .orgFiscalCode("brokerFC")
        .ipaCode("ipaCode")
        .orgName("orgName")
        .status(OrganizationStatus.ACTIVE)
        .flagNotifyIo(true)
        .flagPaymentNotification(true)
        .flagNotifyOutcomePush(true)
        .build();

    OrganizationRequestBody mappedOrg = OrganizationRequestBody.builder()
        .ipaCode("ipaCode")
        .orgFiscalCode("orgFiscalCode")
        .orgName("orgName")
        .status(OrganizationStatus.ACTIVE)
        .flagNotifyIo(false)
        .flagNotifyOutcomePush(false)
        .flagPaymentNotification(false)
        .build();

    Organization createdOrg = Organization.builder()
        .ipaCode("ipaCode1")
        .orgFiscalCode("orgFiscalCode1")
        .orgName("orgName1")
        .status(OrganizationStatus.ACTIVE)
        .flagNotifyIo(false)
        .flagNotifyOutcomePush(false)
        .flagPaymentNotification(false)
        .build();

    Mockito.when(organizationServiceMock.getOrganizationById(ingestionFlowFile.getOrganizationId()))
        .thenReturn(Optional.of(orgFromService));
    Mockito.when(mapperMock.map(dto, orgFromService.getBrokerId())).thenReturn(mappedOrg);
    Mockito.when(organizationServiceMock.createOrganization(mappedOrg)).thenReturn(createdOrg);

    // When
    OrganizationIngestionFlowFileResult result = service.processOrganization(
        Stream.of(dto).iterator(), List.of(),
        ingestionFlowFile, workingDirectory);

    // Then
    Assertions.assertEquals(1L, result.getProcessedRows());
    Assertions.assertEquals(1L, result.getTotalRows());
    Assertions.assertNotNull(result.getOrganizationIpaCodeList());
    Assertions.assertEquals(1, result.getOrganizationIpaCodeList().size());
    Mockito.verify(organizationServiceMock).getOrganizationById(ingestionFlowFile.getOrganizationId());
    Mockito.verify(mapperMock).map(dto, orgFromService.getBrokerId());
    Mockito.verify(organizationServiceMock).createOrganization(mappedOrg);
    Mockito.verifyNoInteractions(errorsArchiverServiceMock);
  }

  @Test
  void givenThrowExceptionWhenProcessOrganizationThenAddError() throws URISyntaxException {
    // Given
    OrganizationIngestionFlowFileDTO organizationIngestionFlowFileDTO = TestUtils.getPodamFactory().manufacturePojo(OrganizationIngestionFlowFileDTO.class);
    IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
    workingDirectory = Path.of(new URI("file:///tmp"));

    OrganizationRequestBody mappedOrg = mock(OrganizationRequestBody.class);

    Organization organization = Organization.builder()
        .brokerId(1L)
        .orgFiscalCode(organizationIngestionFlowFileDTO.getBrokerCf())
        .brokerId(1L)
        .ipaCode("ipaCode")
        .orgName("orgName")
        .status(OrganizationStatus.ACTIVE)
        .flagNotifyIo(true)
        .flagPaymentNotification(true)
        .flagNotifyOutcomePush(true)
        .build();
    Mockito.when(organizationServiceMock.getOrganizationById(ingestionFlowFile.getOrganizationId()))
        .thenReturn(Optional.of(organization));
    Mockito.when(mapperMock.map(organizationIngestionFlowFileDTO, 1L)).thenReturn(mappedOrg);


    Mockito.when(organizationServiceMock.createOrganization(mappedOrg))
        .thenThrow(new RuntimeException("Processing error"));

    Mockito.when(errorsArchiverServiceMock.archiveErrorFiles(workingDirectory, ingestionFlowFile))
        .thenReturn("zipFileName.csv");

    // When
    OrganizationIngestionFlowFileResult result = service.processOrganization(
        Stream.of(organizationIngestionFlowFileDTO).iterator(), List.of(new CsvException("DUMMYERROR")),
        ingestionFlowFile,
        workingDirectory
    );

    // Then
    assertEquals(2, result.getTotalRows());
    assertEquals(0, result.getProcessedRows());
    assertEquals("Some rows have failed", result.getErrorDescription());
    assertEquals("zipFileName.csv", result.getDiscardedFileName());
    Assertions.assertNotNull(result.getOrganizationIpaCodeList());
    Assertions.assertEquals(0, result.getOrganizationIpaCodeList().size());

    verify(organizationServiceMock).createOrganization(mappedOrg);
    verify(errorsArchiverServiceMock).writeErrors(same(workingDirectory), same(ingestionFlowFile), eq(List.of(
        new OrganizationErrorDTO(ingestionFlowFile.getFileName(), null, -1L, "READER_EXCEPTION", "DUMMYERROR"),
        new OrganizationErrorDTO(ingestionFlowFile.getFileName(), organizationIngestionFlowFileDTO.getIpaCode(), 2L, "PROCESS_EXCEPTION", "Processing error")
    )));
  }
}