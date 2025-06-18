package it.gov.pagopa.payhub.activities.service.ingestionflow.assessmentsregistry;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.classification.AssessmentsRegistryService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.assessmentsregistry.AssessmentsRegistryIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.assessmentsregistry.AssessmentsRegistryIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.assessmentsregistry.AssessmentsRegistryMapper;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsRegistry;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.List;
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
    private Path workingDirectory;

    @Mock
    private AssessmentsRegistryMapper mapperMock;

    @Mock
    private AssessmentsRegistryService assessmentsRegistryServiceMock;

    @Mock
    private AssessmentsRegistryProcessingService service;

    @Mock
    private OrganizationService organizationServiceMock;

    @BeforeEach
    void setUp() {
        service = new AssessmentsRegistryProcessingService(mapperMock, errorsArchiverServiceMock,
                assessmentsRegistryServiceMock, organizationServiceMock);
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

        Mockito.when(organizationServiceMock.getIpaCodeByOrganizationId(any())).thenReturn(ipaWrong);

        // When
        AssessmentsRegistryIngestionFlowFileResult result = service.processAssessmentsRegistry(
                Stream.of(dto).iterator(), List.of(),
                ingestionFlowFile, workingDirectory);

        // Then
        Assertions.assertSame(ingestionFlowFile.getOrganizationId(), result.getOrganizationId());
        Assertions.assertEquals(0L, result.getProcessedRows());
        Assertions.assertEquals(1L, result.getTotalRows());
    }

    @Test
    void processAssessmentsRegistryWithNoErrors() {
        // Given

        String ipaCode = "IPA123";

        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        ingestionFlowFile.setOrganizationId(123L);
        AssessmentsRegistryIngestionFlowFileDTO dto = mock(AssessmentsRegistryIngestionFlowFileDTO.class);
        Mockito.when(dto.getOrganizationIpaCode()).thenReturn(ipaCode);

        AssessmentsRegistry assessmentsRegistry = buildAssessmentsRegistry();
        Mockito.when(mapperMock.map(dto, 123L)).thenReturn(assessmentsRegistry);

        Mockito.when(organizationServiceMock.getIpaCodeByOrganizationId(any())).thenReturn(ipaCode);

        // When
        AssessmentsRegistryIngestionFlowFileResult result = service.processAssessmentsRegistry(
                Stream.of(dto).iterator(), List.of(),
                ingestionFlowFile, workingDirectory);

        // Then
        Assertions.assertEquals(1L, result.getProcessedRows());
        Assertions.assertEquals(1L, result.getTotalRows());

        Mockito.verify(mapperMock).map(dto, 123L);
        Mockito.verify(assessmentsRegistryServiceMock).createAssessmentsRegistry(assessmentsRegistry);

        Mockito.verifyNoMoreInteractions(
                mapperMock,
                assessmentsRegistryServiceMock,
                errorsArchiverServiceMock);
    }

    @Test
    void processAssessmentsRegistryWithErrors() {

        // Given
        String ipaCode = "IPA123";

        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        ingestionFlowFile.setOrganizationId(123L);
        AssessmentsRegistryIngestionFlowFileDTO dto = mock(AssessmentsRegistryIngestionFlowFileDTO.class);
        Mockito.when(dto.getOrganizationIpaCode()).thenReturn(ipaCode);

        AssessmentsRegistry assessmentsRegistry = buildAssessmentsRegistry();

        Mockito.when(mapperMock.map(dto, 123L)).thenReturn(assessmentsRegistry);

        Mockito.when(organizationServiceMock.getIpaCodeByOrganizationId(any())).thenReturn(ipaCode);

        doThrow(new RuntimeException("Processing error"))
                .when(assessmentsRegistryServiceMock).createAssessmentsRegistry(assessmentsRegistry);

        Mockito.when(errorsArchiverServiceMock.archiveErrorFiles(workingDirectory, ingestionFlowFile))
                .thenReturn("zipFileName.csv");

        // When
        AssessmentsRegistryIngestionFlowFileResult result = service.processAssessmentsRegistry(
                Stream.of(dto).iterator(), List.of(new CsvException("DUMMYERROR")),
                ingestionFlowFile, workingDirectory);

        // Then
        assertEquals(2, result.getTotalRows());
        assertEquals(0, result.getProcessedRows());
        assertEquals("Some rows have failed", result.getErrorDescription());
        assertEquals("zipFileName.csv", result.getDiscardedFileName());

        Mockito.verify(assessmentsRegistryServiceMock).createAssessmentsRegistry(assessmentsRegistry);
        Mockito.verify(errorsArchiverServiceMock).archiveErrorFiles(workingDirectory, ingestionFlowFile);
    }
}
