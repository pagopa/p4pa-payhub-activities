package it.gov.pagopa.payhub.activities.service.ingestionflow.organization;

import static it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker.buildIngestionFlowFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.organization.BrokerService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.organization.OrganizationErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.organization.OrganizationIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.organization.OrganizationIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.organization.OrganizationMapper;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.organization.dto.generated.Broker;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationRequestBody;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationStatus;
import it.gov.pagopa.pu.organization.dto.generated.PagoPaInteractionModel;
import it.gov.pagopa.pu.organization.dto.generated.PersonalisationFe;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
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
  private BrokerService brokerServiceMock;

  private OrganizationProcessingService service;

  @BeforeEach
  void setUp() {
    service = new OrganizationProcessingService(mapperMock, errorsArchiverServiceMock,
        organizationServiceMock, brokerServiceMock);
  }

  @AfterEach
  void verifyNoMoreInteractions(){
    Mockito.verifyNoMoreInteractions(
        mapperMock,
        errorsArchiverServiceMock,
        organizationServiceMock,
        brokerServiceMock);
  }

  @Test
  void processOrganizationWithNoErrors() {
    IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
    OrganizationIngestionFlowFileDTO dto = mock(OrganizationIngestionFlowFileDTO.class);
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
        .ipaCode("ipaCode")
        .orgFiscalCode("orgFiscalCode")
        .orgName("orgName")
        .status(OrganizationStatus.ACTIVE)
        .flagNotifyIo(false)
        .flagNotifyOutcomePush(false)
        .flagPaymentNotification(false)
        .build();

    Broker broker = Broker.builder()
        .brokerId(1L)
        .brokerFiscalCode("brokerFC")
        .organizationId(1L)
        .brokerName("brokerName")
        .pagoPaInteractionModel(PagoPaInteractionModel.SYNC)
        .personalisationFe(new PersonalisationFe())
        .build();

    Mockito.when(brokerServiceMock.getBrokerByFiscalCode(dto.getBrokerCf()))
        .thenReturn(broker);
    Mockito.when(mapperMock.map(dto, broker.getBrokerId())).thenReturn(mappedOrg);
    Mockito.when(organizationServiceMock.createOrganization(mappedOrg)).thenReturn(createdOrg);

    OrganizationIngestionFlowFileResult result = service.processOrganization(
        Stream.of(dto).iterator(), List.of(),
        ingestionFlowFile, workingDirectory);

    Assertions.assertEquals(1L, result.getProcessedRows());
    Assertions.assertEquals(1L, result.getTotalRows());
    Assertions.assertNotNull(result.getOrganizationIpaCodeList());
    Assertions.assertEquals(1, result.getOrganizationIpaCodeList().size());
    Mockito.verify(mapperMock).map(dto, broker.getBrokerId());
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
    Broker broker = Broker.builder()
        .brokerId(1L)
        .brokerFiscalCode("brokerFC")
        .organizationId(1L)
        .brokerName("brokerName")
        .pagoPaInteractionModel(PagoPaInteractionModel.SYNC)
        .personalisationFe(new PersonalisationFe())
        .build();

    Mockito.when(brokerServiceMock.getBrokerByFiscalCode(organizationIngestionFlowFileDTO.getBrokerCf()))
        .thenReturn(broker);

    Mockito.when(mapperMock.map(organizationIngestionFlowFileDTO, broker.getBrokerId())).thenReturn(mappedOrg);
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

    verify(mapperMock).map(organizationIngestionFlowFileDTO, broker.getBrokerId());
    verify(organizationServiceMock).createOrganization(mappedOrg);
    verify(errorsArchiverServiceMock).writeErrors(same(workingDirectory), same(ingestionFlowFile), eq(List.of(
        new OrganizationErrorDTO(ingestionFlowFile.getFileName(), null, -1L, "READER_EXCEPTION", "DUMMYERROR"),
        new OrganizationErrorDTO(ingestionFlowFile.getFileName(), organizationIngestionFlowFileDTO.getIpaCode(), 2L, "PROCESS_EXCEPTION", "Processing error")
    )));
  }
}