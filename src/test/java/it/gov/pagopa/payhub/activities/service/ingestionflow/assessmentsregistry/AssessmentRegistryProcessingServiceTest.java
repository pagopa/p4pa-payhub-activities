package it.gov.pagopa.payhub.activities.service.ingestionflow.assessmentsregistry;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.classification.AssessmentsRegistryService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.assessments.AssessmentsRegistrySemanticKey;
import it.gov.pagopa.payhub.activities.dto.ingestion.assessmentsregistry.AssessmentsRegistryErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.assessmentsregistry.AssessmentsRegistryIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.assessmentsregistry.AssessmentsRegistryIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.enums.FileErrorCode;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.assessmentsregistry.AssessmentsRegistryMapper;
import it.gov.pagopa.payhub.activities.service.files.FileExceptionHandlerService;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsRegistry;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static it.gov.pagopa.payhub.activities.util.faker.AssessmentsRegistryFaker.buildAssessmentsRegistry;
import static it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker.buildIngestionFlowFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class AssessmentRegistryProcessingServiceTest {

    @Mock
    private AssessmentsRegistryErrorsArchiverService errorsArchiverServiceMock;
    @Mock
    private Path workingDirectoryMock;
    @Mock
    private AssessmentsRegistryMapper mapperMock;
    @Mock
    private AssessmentsRegistryService assessmentsRegistryServiceMock;
    @Mock
    private OrganizationService organizationServiceMock;
    @Mock
    private FileExceptionHandlerService fileExceptionHandlerServiceMock;

    private AssessmentsRegistryProcessingService service;

    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    private final FileExceptionHandlerService.CsvErrorDetails csvErrorDetails =
            new FileExceptionHandlerService.CsvErrorDetails(FileErrorCode.CSV_GENERIC_ERROR.name(), "Errore");

    @BeforeEach
    void setUp() {
        service = new AssessmentsRegistryProcessingService(mapperMock, errorsArchiverServiceMock,
                assessmentsRegistryServiceMock, organizationServiceMock, fileExceptionHandlerServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                errorsArchiverServiceMock,
                workingDirectoryMock,
                mapperMock,
                assessmentsRegistryServiceMock,
                organizationServiceMock);
    }

    @Test
    void processAssessmentsRegistryWithIpaErrors() {
        // Given
        String ipaCode = "IPA123";
        String ipaWrong = "IPA123_WRONG";

        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        ingestionFlowFile.setOrganizationId(123L);
        AssessmentsRegistryIngestionFlowFileDTO dto = mock(AssessmentsRegistryIngestionFlowFileDTO.class);
        Mockito.when(dto.getOrganizationIpaCode()).thenReturn(ipaCode);

        Organization organization = new Organization();
        organization.setIpaCode(ipaWrong);
        Optional<Organization> organizationOptional = Optional.of(organization);

        Mockito.when(organizationServiceMock.getOrganizationById(any())).thenReturn(organizationOptional);

        Mockito.when(errorsArchiverServiceMock.archiveErrorFiles(workingDirectoryMock, ingestionFlowFile))
                .thenReturn("zipFileName.csv");

        // When
        AssessmentsRegistryIngestionFlowFileResult result = service.processAssessmentsRegistry(
                Stream.of(dto).iterator(), List.of(),
                ingestionFlowFile, workingDirectoryMock);

        // Then
        Assertions.assertSame(ingestionFlowFile.getOrganizationId(), result.getOrganizationId());
        Assertions.assertEquals(0L, result.getProcessedRows());
        Assertions.assertEquals(1L, result.getTotalRows());

        Mockito.verify(errorsArchiverServiceMock)
                .writeErrors(workingDirectoryMock, ingestionFlowFile, List.of(
                        AssessmentsRegistryErrorDTO.builder()
                                .fileName(ingestionFlowFile.getFileName())
                                .errorCode("ORGANIZATION_IPA_DOES_NOT_MATCH")
                                .errorMessage("Organization IPA code IPA123 does not match with the one in the ingestion flow file IPA123_WRONG")
                                .rowNumber(1L)
                                .organizationIpaCode(ipaCode)
                                .build())
                );
    }

    @Test
    void consumeRowWithMatchingIpaCodeProcessesSuccessfully() {
        // Given
        long lineNumber = 1L;
        AssessmentsRegistryIngestionFlowFileDTO row = mock(AssessmentsRegistryIngestionFlowFileDTO.class);
        Mockito.when(row.getOrganizationIpaCode()).thenReturn("IPA123");

        AssessmentsRegistryIngestionFlowFileResult ingestionFlowFileResult = new AssessmentsRegistryIngestionFlowFileResult();
        ingestionFlowFileResult.setIpaCode("IPA123");
        ingestionFlowFileResult.setOrganizationId(123L);

        IngestionFlowFile ingestionFlowFile = mock(IngestionFlowFile.class);

        AssessmentsRegistry assessmentsRegistry = podamFactory.manufacturePojo(AssessmentsRegistry.class);
        AssessmentsRegistrySemanticKey registrySemanticKey = AssessmentsRegistrySemanticKey.builder()
                .organizationId(assessmentsRegistry.getOrganizationId())
                .debtPositionTypeOrgCode(assessmentsRegistry.getDebtPositionTypeOrgCode())
                .sectionCode(assessmentsRegistry.getSectionCode())
                .officeCode(assessmentsRegistry.getOfficeCode())
                .assessmentCode(assessmentsRegistry.getAssessmentCode())
                .operatingYear(assessmentsRegistry.getOperatingYear())
                .build();

        Mockito.when(mapperMock.map(row, 123L)).thenReturn(assessmentsRegistry);
        Mockito.when(assessmentsRegistryServiceMock.searchAssessmentsRegistryBySemanticKey(registrySemanticKey))
                .thenReturn(Optional.empty());

        List<AssessmentsRegistryErrorDTO> errorList = new ArrayList<>();

        // When
        boolean result = service.consumeRow(lineNumber, row, ingestionFlowFileResult, errorList, ingestionFlowFile);

        // Then
        Assertions.assertTrue(result);
        Mockito.verify(assessmentsRegistryServiceMock).createAssessmentsRegistry(assessmentsRegistry);
        Assertions.assertTrue(errorList.isEmpty());
    }

    @Test
    void processAssessmentsRegistryWithErrors() {
        // Given
        String ipaCode = "IPA123";

        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        ingestionFlowFile.setOrganizationId(123L);
        AssessmentsRegistryIngestionFlowFileDTO dto = podamFactory.manufacturePojo(AssessmentsRegistryIngestionFlowFileDTO.class);
        dto.setOrganizationIpaCode(ipaCode);

        AssessmentsRegistry assessmentsRegistry = buildAssessmentsRegistry();
        AssessmentsRegistrySemanticKey registrySemanticKey = AssessmentsRegistrySemanticKey.builder()
                .organizationId(assessmentsRegistry.getOrganizationId())
                .debtPositionTypeOrgCode(assessmentsRegistry.getDebtPositionTypeOrgCode())
                .sectionCode(assessmentsRegistry.getSectionCode())
                .officeCode(assessmentsRegistry.getOfficeCode())
                .assessmentCode(assessmentsRegistry.getAssessmentCode())
                .operatingYear(assessmentsRegistry.getOperatingYear())
                .build();

        Mockito.when(mapperMock.map(dto, 123L)).thenReturn(assessmentsRegistry);

        Organization organization = new Organization();
        organization.setIpaCode(ipaCode);
        Optional<Organization> organizationOptional = Optional.of(organization);

        Mockito.when(organizationServiceMock.getOrganizationById(any())).thenReturn(organizationOptional);

        CsvException exception = new CsvException("DUMMYERROR");
        Mockito.when(fileExceptionHandlerServiceMock.mapCsvExceptionToErrorCodeAndMessage(exception))
                .thenReturn(csvErrorDetails);

        Mockito.when(assessmentsRegistryServiceMock.searchAssessmentsRegistryBySemanticKey(registrySemanticKey))
                        .thenReturn(Optional.empty());

        doThrow(new RuntimeException("Processing error"))
                .when(assessmentsRegistryServiceMock).createAssessmentsRegistry(assessmentsRegistry);

        Mockito.when(errorsArchiverServiceMock.archiveErrorFiles(workingDirectoryMock, ingestionFlowFile))
                .thenReturn("zipFileName.csv");

        // When
        AssessmentsRegistryIngestionFlowFileResult result = service.processAssessmentsRegistry(
                Stream.of(dto).iterator(), List.of(exception),
                ingestionFlowFile, workingDirectoryMock);

        // Then
        assertEquals(2, result.getTotalRows());
        assertEquals(0, result.getProcessedRows());
        assertEquals("Some rows have failed", result.getErrorDescription());
        assertEquals("zipFileName.csv", result.getDiscardedFileName());

        Mockito.verify(errorsArchiverServiceMock)
                .writeErrors(workingDirectoryMock, ingestionFlowFile, List.of(
                        AssessmentsRegistryErrorDTO.builder()
                                .fileName(ingestionFlowFile.getFileName())
                                .errorCode("CSV_GENERIC_ERROR")
                                .errorMessage("Errore")
                                .rowNumber(-1L)
                                .build(),
                        AssessmentsRegistryErrorDTO.builder()
                                .fileName(ingestionFlowFile.getFileName())
                                .errorCode("PROCESS_EXCEPTION")
                                .errorMessage("Processing error")
                                .rowNumber(2L)
                                .organizationIpaCode(ipaCode)
                                .assessmentCode(dto.getAssessmentCode())
                                .build())
                );
    }
    @Test
    void processAssessmentsRegistryWithDuplicateErrors() {
        // Given
        String ipaCode = "IPA123";

        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        ingestionFlowFile.setOrganizationId(123L);
        AssessmentsRegistryIngestionFlowFileDTO dto = podamFactory.manufacturePojo(AssessmentsRegistryIngestionFlowFileDTO.class);
        dto.setOrganizationIpaCode(ipaCode);

        AssessmentsRegistry assessmentsRegistry = buildAssessmentsRegistry();
        AssessmentsRegistrySemanticKey registrySemanticKey = AssessmentsRegistrySemanticKey.builder()
                .organizationId(assessmentsRegistry.getOrganizationId())
                .debtPositionTypeOrgCode(assessmentsRegistry.getDebtPositionTypeOrgCode())
                .sectionCode(assessmentsRegistry.getSectionCode())
                .officeCode(assessmentsRegistry.getOfficeCode())
                .assessmentCode(assessmentsRegistry.getAssessmentCode())
                .operatingYear(assessmentsRegistry.getOperatingYear())
                .build();

        Mockito.when(mapperMock.map(dto, 123L)).thenReturn(assessmentsRegistry);

        Organization organization = new Organization();
        organization.setIpaCode(ipaCode);
        Optional<Organization> organizationOptional = Optional.of(organization);

        Mockito.when(organizationServiceMock.getOrganizationById(any())).thenReturn(organizationOptional);

        Mockito.when(assessmentsRegistryServiceMock.searchAssessmentsRegistryBySemanticKey(registrySemanticKey))
                .thenReturn(Optional.of(assessmentsRegistry));

        Mockito.when(errorsArchiverServiceMock.archiveErrorFiles(workingDirectoryMock, ingestionFlowFile))
                .thenReturn("zipFileName.csv");

        // When
        AssessmentsRegistryIngestionFlowFileResult result = service.processAssessmentsRegistry(
                Stream.of(dto).iterator(), List.of(),
                ingestionFlowFile, workingDirectoryMock);

        // Then
        assertEquals(1, result.getTotalRows());
        assertEquals(0, result.getProcessedRows());
        assertEquals("Some rows have failed", result.getErrorDescription());
        assertEquals("zipFileName.csv", result.getDiscardedFileName());

        Mockito.verify(errorsArchiverServiceMock)
                .writeErrors(workingDirectoryMock, ingestionFlowFile, List.of(
                        AssessmentsRegistryErrorDTO.builder()
                                .fileName(ingestionFlowFile.getFileName())
                                .errorCode("ASSESSMENTS_REGISTRY_ALREADY_EXISTS")
                                .errorMessage("AssessmentsRegistry already exists")
                                .rowNumber(1L)
                                .organizationIpaCode(ipaCode)
                                .assessmentCode(assessmentsRegistry.getAssessmentCode())
                                .build())
                );
    }
}
