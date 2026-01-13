package it.gov.pagopa.payhub.activities.service.ingestionflow.organization;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.organization.OrganizationErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.organization.OrganizationIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.organization.OrganizationIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.enums.FileErrorCode;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.organization.OrganizationMapper;
import it.gov.pagopa.payhub.activities.service.files.FileExceptionHandlerService;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationCreateDTO;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationStatus;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker.buildIngestionFlowFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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
    private FileExceptionHandlerService fileExceptionHandlerServiceMock;

    @Mock
    private OrganizationProcessingService service;

    private final FileExceptionHandlerService.CsvErrorDetails csvErrorDetails =
            new FileExceptionHandlerService.CsvErrorDetails(FileErrorCode.CSV_GENERIC_ERROR.name(), "Errore");

    @BeforeEach
    void setUp() {
        service = new OrganizationProcessingService(mapperMock, errorsArchiverServiceMock,
                organizationServiceMock, fileExceptionHandlerServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                mapperMock,
                organizationServiceMock,
                errorsArchiverServiceMock);
    }

    @Test
    void processOrganizationWithNoErrors() {
        // Given
        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        OrganizationIngestionFlowFileDTO dto = new OrganizationIngestionFlowFileDTO();
        dto.setBrokerCf("brokerFC");

        Organization brokerOrganization = Organization.builder()
                .brokerId(1L)
                .orgFiscalCode("brokerFC")
                .ipaCode("ipaCode")
                .orgName("orgName")
                .status(OrganizationStatus.ACTIVE)
                .flagNotifyIo(true)
                .flagPaymentNotification(true)
                .flagNotifyOutcomePush(true)
                .pdndEnabled(false)
                .flagTreasury(false)
                .build();

        OrganizationCreateDTO mappedOrg = OrganizationCreateDTO.builder()
                .ipaCode("ipaCode")
                .orgFiscalCode("orgFiscalCode")
                .orgName("orgName")
                .status(OrganizationStatus.ACTIVE)
                .flagNotifyIo(false)
                .flagNotifyOutcomePush(false)
                .flagPaymentNotification(false)
                .pdndEnabled(false)
                .flagTreasury(false)
                .build();

        Organization createdOrg = Organization.builder()
                .organizationId(1L)
                .ipaCode("ipaCode1")
                .orgFiscalCode("orgFiscalCode1")
                .orgName("orgName1")
                .status(OrganizationStatus.ACTIVE)
                .flagNotifyIo(false)
                .flagNotifyOutcomePush(false)
                .flagPaymentNotification(false)
                .pdndEnabled(false)
                .flagTreasury(false)
                .build();

        Mockito.when(organizationServiceMock.getOrganizationById(ingestionFlowFile.getOrganizationId()))
                .thenReturn(Optional.of(brokerOrganization));
        Mockito.when(organizationServiceMock.getOrganizationByFiscalCode(Mockito.any()))
                .thenReturn(Optional.empty());
        Mockito.when(mapperMock.map(dto, brokerOrganization.getBrokerId())).thenReturn(mappedOrg);
        Mockito.when(organizationServiceMock.createOrganization(mappedOrg)).thenReturn(createdOrg);

        // When
        OrganizationIngestionFlowFileResult result = service.processOrganization(
                Stream.of(dto).iterator(), List.of(),
                ingestionFlowFile, workingDirectory);

        // Then
        Assertions.assertEquals(1L, result.getProcessedRows());
        Assertions.assertEquals(1L, result.getTotalRows());
        Mockito.verify(organizationServiceMock).getOrganizationByFiscalCode(Mockito.any());
    }

    @Test
    void givenThrowExceptionWhenProcessOrganizationThenAddError() throws URISyntaxException {
        // Given
        OrganizationIngestionFlowFileDTO organizationIngestionFlowFileDTO = TestUtils.getPodamFactory().manufacturePojo(OrganizationIngestionFlowFileDTO.class);
        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        workingDirectory = Path.of(new URI("file:///tmp"));

        OrganizationCreateDTO mappedOrg = mock(OrganizationCreateDTO.class);

        Organization organization = Organization.builder()
                .brokerId(1L)
                .orgFiscalCode(organizationIngestionFlowFileDTO.getBrokerCf())
                .ipaCode("ipaCode")
                .orgName("orgName")
                .status(OrganizationStatus.ACTIVE)
                .flagNotifyIo(true)
                .flagPaymentNotification(true)
                .flagNotifyOutcomePush(true)
                .pdndEnabled(false)
                .flagTreasury(false)
                .build();
        Mockito.when(organizationServiceMock.getOrganizationById(ingestionFlowFile.getOrganizationId()))
                .thenReturn(Optional.of(organization));
        Mockito.when(mapperMock.map(organizationIngestionFlowFileDTO, 1L)).thenReturn(mappedOrg);


        Mockito.when(organizationServiceMock.createOrganization(mappedOrg))
                .thenThrow(new RuntimeException("Processing error"));

        Mockito.when(errorsArchiverServiceMock.archiveErrorFiles(workingDirectory, ingestionFlowFile))
                .thenReturn("zipFileName.csv");

        CsvException exception = new CsvException("DUMMYERROR");
        Mockito.when(fileExceptionHandlerServiceMock.mapCsvExceptionToErrorCodeAndMessage(exception))
                .thenReturn(csvErrorDetails);

        // When
        OrganizationIngestionFlowFileResult result = service.processOrganization(
                Stream.of(organizationIngestionFlowFileDTO).iterator(), List.of(exception),
                ingestionFlowFile,
                workingDirectory
        );

        // Then
        assertEquals(2, result.getTotalRows());
        assertEquals(0, result.getProcessedRows());
        assertEquals("Some rows have failed", result.getErrorDescription());
        assertEquals("zipFileName.csv", result.getDiscardedFileName());
        Mockito.verify(organizationServiceMock).getOrganizationByFiscalCode(Mockito.anyString());
        verify(errorsArchiverServiceMock).writeErrors(same(workingDirectory), same(ingestionFlowFile), eq(List.of(
                new OrganizationErrorDTO(ingestionFlowFile.getFileName(), null, -1L, "CSV_GENERIC_ERROR", "Errore"),
                new OrganizationErrorDTO(ingestionFlowFile.getFileName(), organizationIngestionFlowFileDTO.getIpaCode(), 2L, "PROCESS_EXCEPTION", "Processing error")
        )));
    }

    @Test
    void processOrganizationWhenBrokerIdNotFound() {
        // Given
        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        OrganizationIngestionFlowFileDTO dto = mock(OrganizationIngestionFlowFileDTO.class);

        Mockito.when(organizationServiceMock.getOrganizationById(ingestionFlowFile.getOrganizationId()))
                .thenReturn(Optional.empty());

        // When
        OrganizationIngestionFlowFileResult result = service.processOrganization(
                Stream.of(dto).iterator(), List.of(),
                ingestionFlowFile, workingDirectory);

        // Then
        Assertions.assertNull(result.getBrokerId());
        Assertions.assertEquals("Broker not found", result.getErrorDescription());
        Assertions.assertEquals(0, result.getProcessedRows());
        Mockito.verify(organizationServiceMock).getOrganizationById(ingestionFlowFile.getOrganizationId());
        Mockito.verifyNoInteractions(mapperMock, errorsArchiverServiceMock);
    }

    @Test
    void processOrganizationWhenBrokerFiscalCodeNotMatched() {
        // Given
        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        OrganizationIngestionFlowFileDTO dto = mock(OrganizationIngestionFlowFileDTO.class);
        Mockito.when(dto.getBrokerCf()).thenReturn("DIFFERENT_BROKER_CF");
        Mockito.when(dto.getOrgFiscalCode()).thenReturn("ORG_FISCAL_CODE");
        Mockito.when(dto.getIpaCode()).thenReturn("IPA_CODE");

        Organization orgFromService = Organization.builder()
                .brokerId(1L)
                .orgFiscalCode("BROKER_CF_FROM_ORG")
                .ipaCode("ipaCode")
                .orgName("orgName")
                .status(OrganizationStatus.ACTIVE)
                .flagNotifyIo(true)
                .flagPaymentNotification(true)
                .flagNotifyOutcomePush(true)
                .pdndEnabled(false)
                .flagTreasury(false)
                .build();

        Mockito.when(organizationServiceMock.getOrganizationById(ingestionFlowFile.getOrganizationId()))
                .thenReturn(Optional.of(orgFromService));
        Mockito.when(errorsArchiverServiceMock.archiveErrorFiles(workingDirectory, ingestionFlowFile))
                .thenReturn("zipFileName.csv");

        // When
        OrganizationIngestionFlowFileResult result = service.processOrganization(
                Stream.of(dto).iterator(), List.of(),
                ingestionFlowFile, workingDirectory);

        // Then
        Assertions.assertEquals(0, result.getProcessedRows());
        Assertions.assertEquals(1, result.getTotalRows());
        Assertions.assertEquals("Some rows have failed", result.getErrorDescription());
        Assertions.assertEquals("zipFileName.csv", result.getDiscardedFileName());
        Mockito.verify(organizationServiceMock).getOrganizationById(ingestionFlowFile.getOrganizationId());
        Mockito.verify(dto).getOrgFiscalCode();
        Mockito.verify(dto).getIpaCode();
        Mockito.verifyNoInteractions(mapperMock);
        Mockito.verify(errorsArchiverServiceMock).archiveErrorFiles(workingDirectory, ingestionFlowFile);
        Mockito.verify(errorsArchiverServiceMock).writeErrors(Mockito.eq(workingDirectory), Mockito.eq(ingestionFlowFile), Mockito.anyList());
    }


    @Test
    void processOrganizationWhenOrganizationDoesNotExist() {
        // Given
        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        OrganizationIngestionFlowFileDTO dto = mock(OrganizationIngestionFlowFileDTO.class);

        Mockito.when(organizationServiceMock.getOrganizationById(ingestionFlowFile.getOrganizationId()))
                .thenReturn(Optional.empty());

        // When
        OrganizationIngestionFlowFileResult result = service.processOrganization(
                Stream.of(dto).iterator(), List.of(),
                ingestionFlowFile, workingDirectory);

        // Then
        Assertions.assertNull(result.getBrokerId());
        Assertions.assertEquals("Broker not found", result.getErrorDescription());
        Assertions.assertEquals(0, result.getProcessedRows());
        Mockito.verify(organizationServiceMock).getOrganizationById(ingestionFlowFile.getOrganizationId());
    }

    @Test
    void processOrganizationWithUnexpectedBrokerCfInCsvRow() {
        // Given
        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        OrganizationIngestionFlowFileDTO dto = mock(OrganizationIngestionFlowFileDTO.class);
        Mockito.when(dto.getBrokerCf()).thenReturn("UNEXPECTED_BROKER_CF");
        Mockito.when(dto.getOrgFiscalCode()).thenReturn("ORG_FISCAL_CODE");
        Mockito.when(dto.getIpaCode()).thenReturn("IPA_CODE");

        Organization orgFromService = Organization.builder()
                .brokerId(1L)
                .orgFiscalCode("EXPECTED_BROKER_CF")
                .ipaCode("ipaCode")
                .orgName("orgName")
                .status(OrganizationStatus.ACTIVE)
                .flagNotifyIo(true)
                .flagPaymentNotification(true)
                .flagNotifyOutcomePush(true)
                .pdndEnabled(false)
                .flagTreasury(false)
                .build();

        Mockito.when(organizationServiceMock.getOrganizationById(ingestionFlowFile.getOrganizationId()))
                .thenReturn(Optional.of(orgFromService));
        Mockito.when(errorsArchiverServiceMock.archiveErrorFiles(workingDirectory, ingestionFlowFile))
                .thenReturn("zipFileName.csv");

        // When
        OrganizationIngestionFlowFileResult result = service.processOrganization(
                Stream.of(dto).iterator(), List.of(),
                ingestionFlowFile, workingDirectory);

        // Then
        Assertions.assertEquals(0, result.getProcessedRows());
        Assertions.assertEquals(1, result.getTotalRows());
        Assertions.assertEquals("Some rows have failed", result.getErrorDescription());
        Assertions.assertEquals("zipFileName.csv", result.getDiscardedFileName());
        Mockito.verify(organizationServiceMock).getOrganizationById(ingestionFlowFile.getOrganizationId());
        Mockito.verify(dto).getOrgFiscalCode();
        Mockito.verify(dto).getIpaCode();
        Mockito.verify(errorsArchiverServiceMock).archiveErrorFiles(workingDirectory, ingestionFlowFile);
        Mockito.verify(errorsArchiverServiceMock).writeErrors(Mockito.eq(workingDirectory), Mockito.eq(ingestionFlowFile), Mockito.anyList());
    }

    @Test
    void processOrganizationWhenOrganizationAlreadyExists() {
        // Given
        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        OrganizationIngestionFlowFileDTO dto = mock(OrganizationIngestionFlowFileDTO.class);
        Mockito.when(dto.getBrokerCf()).thenReturn("brokerFC");
        Mockito.when(dto.getOrgFiscalCode()).thenReturn("ORG_FISCAL_CODE");
        Mockito.when(dto.getIpaCode()).thenReturn("IPA_CODE");

        Organization orgFromService = Organization.builder()
                .brokerId(1L)
                .orgFiscalCode("brokerFC")
                .ipaCode("ipaCode")
                .orgName("orgName")
                .status(OrganizationStatus.ACTIVE)
                .flagNotifyIo(true)
                .flagPaymentNotification(true)
                .flagNotifyOutcomePush(true)
                .pdndEnabled(false)
                .flagTreasury(false)
                .build();

        Organization existingOrg = Organization.builder()
                .orgFiscalCode("ORG_FISCAL_CODE")
                .ipaCode("IPA_CODE")
                .brokerId(1L)
                .orgName("orgName2")
                .status(OrganizationStatus.ACTIVE)
                .flagNotifyIo(true)
                .flagPaymentNotification(true)
                .flagNotifyOutcomePush(true)
                .pdndEnabled(false)
                .flagTreasury(false)
                .build();

        Mockito.when(organizationServiceMock.getOrganizationById(ingestionFlowFile.getOrganizationId()))
                .thenReturn(Optional.of(orgFromService));
        Mockito.when(organizationServiceMock.getOrganizationByFiscalCode("ORG_FISCAL_CODE"))
                .thenReturn(Optional.of(existingOrg));
        Mockito.when(errorsArchiverServiceMock.archiveErrorFiles(workingDirectory, ingestionFlowFile))
                .thenReturn("zipFileName.csv");

        // When
        OrganizationIngestionFlowFileResult result = service.processOrganization(
                Stream.of(dto).iterator(), List.of(),
                ingestionFlowFile, workingDirectory);

        // Then
        Assertions.assertEquals(0, result.getProcessedRows());
        Assertions.assertEquals(1, result.getTotalRows());
        Assertions.assertEquals("Some rows have failed", result.getErrorDescription());
        Assertions.assertEquals("zipFileName.csv", result.getDiscardedFileName());
        Mockito.verify(organizationServiceMock).getOrganizationById(ingestionFlowFile.getOrganizationId());
        Mockito.verify(organizationServiceMock).getOrganizationByFiscalCode("ORG_FISCAL_CODE");
        Mockito.verify(dto).getOrgFiscalCode();
        Mockito.verify(dto).getIpaCode();
        Mockito.verify(errorsArchiverServiceMock).archiveErrorFiles(workingDirectory, ingestionFlowFile);
        Mockito.verify(errorsArchiverServiceMock).writeErrors(Mockito.eq(workingDirectory), Mockito.eq(ingestionFlowFile), Mockito.anyList());
        Mockito.verifyNoInteractions(mapperMock);
    }

}