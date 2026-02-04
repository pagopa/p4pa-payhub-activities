package it.gov.pagopa.payhub.activities.service.ingestionflow.orgsilservice;

import it.gov.pagopa.payhub.activities.connector.organization.OrgSilServiceService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.orgsilservice.OrgSilServiceErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.orgsilservice.OrgSilServiceIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.orgsilservice.OrgSilServiceIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.enums.FileErrorCode;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.orgsilservice.OrgSilServiceMapper;
import it.gov.pagopa.payhub.activities.service.files.FileExceptionHandlerService;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.organization.dto.generated.OrgSilService;
import it.gov.pagopa.pu.organization.dto.generated.OrgSilServiceDTO;
import it.gov.pagopa.pu.organization.dto.generated.OrgSilServiceType;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker.buildIngestionFlowFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    @BeforeEach
    void setUp() {
        FileExceptionHandlerService fileExceptionHandlerService = new FileExceptionHandlerService();
        service = new OrgSilServiceProcessingService(1, mapperMock, errorsArchiverServiceMock,
                organizationServiceMock, orgSilServiceServiceMock, fileExceptionHandlerService);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                mapperMock,
                errorsArchiverServiceMock,
                organizationServiceMock,
                orgSilServiceServiceMock);
    }

    @Test
    void whenGetSequencingIdThenReturnExpectedValue() {
        // Given
        OrgSilServiceIngestionFlowFileDTO row = podamFactory.manufacturePojo(OrgSilServiceIngestionFlowFileDTO.class);

        // When
        String result = service.getSequencingId(row);

        // Then
        assertEquals(
                row.getServiceType() + "-" + row.getApplicationName(),
                result);
    }

    @Test
    void processOrgSilServiceWithIpaErrors() {
        // Given
        long organizationId = 123L;
        String ipaCode = "IPA123";
        String ipaWrong = "IPA123_WRONG";

        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        ingestionFlowFile.setOrganizationId(organizationId);
        OrgSilServiceIngestionFlowFileDTO dto = mock(OrgSilServiceIngestionFlowFileDTO.class);
        Mockito.when(dto.getIpaCode()).thenReturn(ipaCode);

        Organization organization = new Organization();
        organization.setIpaCode(ipaWrong);

        Mockito.when(organizationServiceMock.getOrganizationById(organizationId)).thenReturn(Optional.of(organization));
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

        Mockito.verify(errorsArchiverServiceMock).writeErrors(workingDirectory, ingestionFlowFile, List.of(
                OrgSilServiceErrorDTO.builder()
                        .fileName(ingestionFlowFile.getFileName())
                        .rowNumber(1L)
                        .errorCode("ORGANIZATION_IPA_MISMATCH")
                        .errorMessage("Il codice IPA IPA123 dell'ente non corrisponde a quello del file IPA123_WRONG")
                        .ipaCode(ipaCode)
                        .build()
        ));
    }

    @Test
    void consumeRowWithMatchingIpaCodeProcessesSuccessfully() {
        // Given
        long lineNumber = 1L;
        long organizationId = 123L;
        String orgIpaCode = "IPA123";
        OrgSilServiceIngestionFlowFileDTO row = OrgSilServiceIngestionFlowFileDTO.builder()
                .ipaCode(orgIpaCode)
                .applicationName("TestApp")
                .serviceUrl("http://test.url")
                .serviceType("ACTUALIZATION")
                .flagLegacy(true)
                .build();
        OrgSilServiceDTO orgSilServiceDTOMapped = OrgSilServiceDTO.builder()
                .organizationId(organizationId)
                .applicationName("TestApp")
                .serviceUrl("http://test.url")
                .serviceType(OrgSilServiceType.ACTUALIZATION)
                .flagLegacy(true)
                .authConfig(null)
                .build();

        Mockito.when(mapperMock.map(row, organizationId)).thenReturn(orgSilServiceDTOMapped);

        OrgSilServiceIngestionFlowFileResult ingestionFlowFileResult = new OrgSilServiceIngestionFlowFileResult();
        ingestionFlowFileResult.setOrganizationId(organizationId);
        ingestionFlowFileResult.setIpaCode(orgIpaCode);

        IngestionFlowFile ingestionFlowFile = new IngestionFlowFile();
        ingestionFlowFile.setOrganizationId(organizationId);

        Mockito.when(orgSilServiceServiceMock.getAllByOrganizationIdAndServiceType(ingestionFlowFile.getOrganizationId(), orgSilServiceDTOMapped.getServiceType()))
                .thenReturn(List.of(podamFactory.manufacturePojo(OrgSilService.class)));

        // When
        List<OrgSilServiceErrorDTO> result = service.consumeRow(lineNumber, row, ingestionFlowFileResult, ingestionFlowFile);

        // Then
        Assertions.assertTrue(result.isEmpty());
        Mockito.verify(orgSilServiceServiceMock).createOrUpdateOrgSilService(orgSilServiceDTOMapped);
    }

    @Test
    void consumeRowWithMissingOrganizationAddsError() {
        // Given
        long lineNumber = 2L;
        OrgSilServiceIngestionFlowFileDTO row = podamFactory.manufacturePojo(OrgSilServiceIngestionFlowFileDTO.class);
        row.setIpaCode("IPA_NOT_FOUND");
        row.setApplicationName("TestApp");

        OrgSilServiceIngestionFlowFileResult ingestionFlowFileResult = new OrgSilServiceIngestionFlowFileResult();
        ingestionFlowFileResult.setOrganizationId(456L);
        ingestionFlowFileResult.setIpaCode("IPA_CODE");
        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        ingestionFlowFile.setFileName("testfile.csv");

        // When
        List<OrgSilServiceErrorDTO> result = service.consumeRow(lineNumber, row, ingestionFlowFileResult, ingestionFlowFile);

        // Then
        OrgSilServiceErrorDTO error = result.getFirst();
        Assertions.assertEquals("testfile.csv", error.getFileName());
        Assertions.assertEquals("IPA_NOT_FOUND", error.getIpaCode());
        Assertions.assertEquals("TestApp", error.getApplicationName());
        Assertions.assertEquals(lineNumber, error.getRowNumber());
        Assertions.assertEquals(FileErrorCode.ORGANIZATION_IPA_MISMATCH.name(), error.getErrorCode());
        Assertions.assertEquals("Il codice IPA IPA_NOT_FOUND dell'ente non corrisponde a quello del file IPA_CODE", error.getErrorMessage());
    }

    @Test
    void consumeRowWithExistingServiceUpdatesOrgSilServiceId() {
        // Given
        long lineNumber = 1L;
        long organizationId = 123L;
        String orgIpaCode = "IPA123";
        OrgSilServiceIngestionFlowFileDTO row = OrgSilServiceIngestionFlowFileDTO.builder()
                .ipaCode(orgIpaCode)
                .applicationName("TestApp")
                .serviceUrl("http://test.url")
                .serviceType("ACTUALIZATION")
                .flagLegacy(true)
                .build();

        OrgSilServiceDTO orgSilServiceDTOMapped = OrgSilServiceDTO.builder()
                .organizationId(organizationId)
                .applicationName("TestApp")
                .serviceUrl("http://test.url")
                .serviceType(OrgSilServiceType.ACTUALIZATION)
                .flagLegacy(true)
                .authConfig(null)
                .build();

        Mockito.when(mapperMock.map(row, organizationId)).thenReturn(orgSilServiceDTOMapped);

        OrgSilServiceIngestionFlowFileResult ingestionFlowFileResult = new OrgSilServiceIngestionFlowFileResult();
        ingestionFlowFileResult.setOrganizationId(organizationId);
        ingestionFlowFileResult.setIpaCode(orgIpaCode);

        IngestionFlowFile ingestionFlowFile = new IngestionFlowFile();
        ingestionFlowFile.setOrganizationId(organizationId);

        OrgSilService existingService = new OrgSilService();
        existingService.setApplicationName("TestApp");
        existingService.setOrgSilServiceId(999L);
        List<OrgSilService> existingServices = List.of(existingService);
        Mockito.when(orgSilServiceServiceMock.getAllByOrganizationIdAndServiceType(organizationId, OrgSilServiceType.ACTUALIZATION)).thenReturn(existingServices);

        // When
        List<OrgSilServiceErrorDTO> result = service.consumeRow(lineNumber, row, ingestionFlowFileResult, ingestionFlowFile);

        // Then
        Assertions.assertTrue(result.isEmpty());
        Assertions.assertEquals(999L, orgSilServiceDTOMapped.getOrgSilServiceId());
        Mockito.verify(orgSilServiceServiceMock).createOrUpdateOrgSilService(orgSilServiceDTOMapped);
    }
}