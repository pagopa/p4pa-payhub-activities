package it.gov.pagopa.payhub.activities.activity.ingestionflow.assessmentsregistry;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.ingestion.assessmentsregistry.AssessmentsRegistryIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.assessmentsregistry.AssessmentsRegistryIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.InvalidIngestionFileException;
import it.gov.pagopa.payhub.activities.service.files.CsvService;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.assessmentsregistry.AssessmentsRegistryProcessingService;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile.IngestionFlowFileTypeEnum;
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

import static it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker.buildIngestionFlowFile;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class AssessmentsRegistryIngestionActivityTest {


    @Mock
    private CsvService csvServiceMock;
    @Mock
    private AssessmentsRegistryProcessingService debtPositionTypeProcessingServiceMock;
    @Mock
    private IngestionFlowFileService ingestionFlowFileServiceMock;
    @Mock
    private IngestionFlowFileRetrieverService ingestionFlowFileRetrieverServiceMock;
    @Mock
    private FileArchiverService fileArchiverServiceMock;

    private AssessmentsRegistryIngestionActivityImpl activity;

    @TempDir
    private Path workingDir;

    @BeforeEach
    void setUp() {
        activity = new AssessmentsRegistryIngestionActivityImpl(
                ingestionFlowFileServiceMock,
                ingestionFlowFileRetrieverServiceMock,
                fileArchiverServiceMock,
                csvServiceMock,
                debtPositionTypeProcessingServiceMock
        );
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                ingestionFlowFileServiceMock,
                ingestionFlowFileRetrieverServiceMock,
                fileArchiverServiceMock,
                csvServiceMock,
                debtPositionTypeProcessingServiceMock
        );
    }

    @Test
    void handleRetrievedFilesSuccessfully() throws Exception {
        Long ingestionFlowFileId = 1L;
        Long organizationId = 10L;
        IngestionFlowFile ingestionFlowFileDTO = buildIngestionFlowFile();
        ingestionFlowFileDTO.setOrganizationId(organizationId);
        ingestionFlowFileDTO.setFilePathName(workingDir.toString());
        ingestionFlowFileDTO.setIngestionFlowFileType(IngestionFlowFileTypeEnum.ASSESSMENTS_REGISTRY);
        Iterator<AssessmentsRegistryIngestionFlowFileDTO> iterator = buildAssessmentsRegistryIngestionFlowFileDTO();
        List<CsvException> readerExceptions = List.of();

        Path filePath = Files.createFile(Path.of(ingestionFlowFileDTO.getFilePathName()).resolve(ingestionFlowFileDTO.getFileName()));
        List<Path> mockedListPath = List.of(filePath);

        Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId))
                .thenReturn(Optional.of(ingestionFlowFileDTO));

        doReturn(mockedListPath).when(ingestionFlowFileRetrieverServiceMock)
                .retrieveAndUnzipFile(ingestionFlowFileDTO.getOrganizationId(), Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());

        Mockito.when(csvServiceMock.readCsv(eq(filePath), eq(AssessmentsRegistryIngestionFlowFileDTO.class), any(), isNull()))
                .thenAnswer(invocation -> {
                    BiFunction<Iterator<AssessmentsRegistryIngestionFlowFileDTO>, List<CsvException>, AssessmentsRegistryIngestionFlowFileResult> rowProcessor = invocation.getArgument(2);
                    return rowProcessor.apply(iterator, readerExceptions);
                });

        Mockito.when(debtPositionTypeProcessingServiceMock.processAssessmentsRegistry(same(iterator), same(readerExceptions), eq(ingestionFlowFileDTO), eq(filePath.getParent())))
                .thenReturn(buildAssessmentsRegistryIngestionFlowFileResult());

        // When
        AssessmentsRegistryIngestionFlowFileResult result = activity.processFile(ingestionFlowFileId);

        // Then
        Assertions.assertEquals(
                buildAssessmentsRegistryIngestionFlowFileResult(),
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
        ingestionFlowFileDTO.setIngestionFlowFileType(IngestionFlowFileTypeEnum.ASSESSMENTS_REGISTRY);
        Iterator<AssessmentsRegistryIngestionFlowFileDTO> iterator = buildAssessmentsRegistryIngestionFlowFileDTO();
        List<CsvException> readerExceptions = List.of();

        Path filePath = Files.createFile(Path.of(ingestionFlowFileDTO.getFilePathName()).resolve(ingestionFlowFileDTO.getFileName()));
        List<Path> mockedListPath = List.of(filePath);

        Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId))
                .thenReturn(Optional.of(ingestionFlowFileDTO));

        doReturn(mockedListPath).when(ingestionFlowFileRetrieverServiceMock)
                .retrieveAndUnzipFile(ingestionFlowFileDTO.getOrganizationId(), Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());

        Mockito.when(csvServiceMock.readCsv(eq(filePath), eq(AssessmentsRegistryIngestionFlowFileDTO.class), any(), isNull()))
                .thenAnswer(invocation -> {
                    BiFunction<Iterator<AssessmentsRegistryIngestionFlowFileDTO>, List<CsvException>, AssessmentsRegistryIngestionFlowFileResult> rowProcessor = invocation.getArgument(2);
                    return rowProcessor.apply(iterator, readerExceptions);
                });

        Mockito.when(debtPositionTypeProcessingServiceMock.processAssessmentsRegistry(same(iterator), same(readerExceptions), eq(ingestionFlowFileDTO), eq(filePath.getParent())))
                .thenThrow(new RestClientException("Error"));

        // When & Then
        assertThrows(InvalidIngestionFileException.class, () -> activity.processFile(ingestionFlowFileId));
    }

    private AssessmentsRegistryIngestionFlowFileResult buildAssessmentsRegistryIngestionFlowFileResult() {
        return AssessmentsRegistryIngestionFlowFileResult.builder()
                .organizationId(10L)
                .processedRows(20L)
                .totalRows(30L)
                .discardedFileName("dicardedFileName")
                .errorDescription("errorDescription")
                .build();
    }

    private Iterator<AssessmentsRegistryIngestionFlowFileDTO> buildAssessmentsRegistryIngestionFlowFileDTO() {
        List<AssessmentsRegistryIngestionFlowFileDTO> assessmentsRegistryIngestionFlowFileDTOS = List.of(
                AssessmentsRegistryIngestionFlowFileDTO.builder()
                        .assessmentCode("assessmentCode")
                        .assessmentDescription("des")
                        .debtPositionTypeOrgCode("debtPositionTypeOrgCode")
                        .operatingYear("2025")
                        .officeCode("officeCode")
                        .officeDescription("officeDescription")
                        .organizationIpaCode(123L)
                        .sectionCode("sectionCode")
                        .sectionDescription("sectionDescription")
                        .status("status")
                        .build(),
                AssessmentsRegistryIngestionFlowFileDTO.builder()
                        .assessmentCode("assessmentCode")
                        .assessmentDescription("des")
                        .debtPositionTypeOrgCode("debtPositionTypeOrgCode")
                        .operatingYear("2025")
                        .officeCode("officeCode")
                        .officeDescription("officeDescription")
                        .organizationIpaCode(123L)
                        .sectionCode("sectionCode")
                        .sectionDescription("sectionDescription")
                        .status("status")
                        .build()
        );

        return assessmentsRegistryIngestionFlowFileDTOS.iterator();
    }
}
