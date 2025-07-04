package it.gov.pagopa.payhub.activities.activity.ingestionflow.assessments;


import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.ingestion.assessments.AssessmentsIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.assessments.AssessmentsIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.InvalidIngestionFileException;
import it.gov.pagopa.payhub.activities.service.files.CsvService;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.assessments.AssessmentsProcessingService;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
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
class AssessmentsIngestionActivityTest {

    @Mock
    private CsvService csvServiceMock;
    @Mock
    private AssessmentsProcessingService assessmentsProcessingServiceMock;
    @Mock
    private IngestionFlowFileService ingestionFlowFileServiceMock;
    @Mock
    private IngestionFlowFileRetrieverService ingestionFlowFileRetrieverServiceMock;
    @Mock
    private FileArchiverService fileArchiverServiceMock;

    private AssessmentsIngestionActivityImpl activity;

    @TempDir
    private Path workingDir;

    @BeforeEach
    void setUp() {
        activity = new AssessmentsIngestionActivityImpl(
                ingestionFlowFileServiceMock,
                ingestionFlowFileRetrieverServiceMock,
                fileArchiverServiceMock,
                csvServiceMock,
                assessmentsProcessingServiceMock
        );
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                ingestionFlowFileServiceMock,
                ingestionFlowFileRetrieverServiceMock,
                fileArchiverServiceMock,
                csvServiceMock,
                assessmentsProcessingServiceMock
        );
    }

    @Test
    void handleRetrievedFilesSuccessfully() throws Exception {
        Long ingestionFlowFileId = 1L;
        Long organizationId = 10L;
        IngestionFlowFile ingestionFlowFileDTO = buildIngestionFlowFile();
        ingestionFlowFileDTO.setOrganizationId(organizationId);
        ingestionFlowFileDTO.setFilePathName(workingDir.toString());
        ingestionFlowFileDTO.setIngestionFlowFileType(IngestionFlowFile.IngestionFlowFileTypeEnum.ASSESSMENTS);
        Iterator<AssessmentsIngestionFlowFileDTO> iterator = buildAssessmentsIngestionFlowFileDTO();
        List<CsvException> readerExceptions = List.of();

        Path filePath = Files.createFile(Path.of(ingestionFlowFileDTO.getFilePathName()).resolve(ingestionFlowFileDTO.getFileName()));
        List<Path> mockedListPath = List.of(filePath);

        Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId))
                .thenReturn(Optional.of(ingestionFlowFileDTO));

        doReturn(mockedListPath).when(ingestionFlowFileRetrieverServiceMock)
                .retrieveAndUnzipFile(ingestionFlowFileDTO.getOrganizationId(), Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());

        Mockito.when(csvServiceMock.readCsv(eq(filePath), eq(AssessmentsIngestionFlowFileDTO.class), any(), isNull()))
                .thenAnswer(invocation -> {
                    BiFunction<Iterator<AssessmentsIngestionFlowFileDTO>, List<CsvException>, AssessmentsIngestionFlowFileResult> rowProcessor = invocation.getArgument(2);
                    return rowProcessor.apply(iterator, readerExceptions);
                });

        Mockito.when(assessmentsProcessingServiceMock.processAssessments(same(iterator), same(readerExceptions), eq(ingestionFlowFileDTO), eq(filePath.getParent())))
                .thenReturn(buildAssessmentsIngestionFlowFileResult());

        // When
        AssessmentsIngestionFlowFileResult result = activity.processFile(ingestionFlowFileId);

        // Then
        Assertions.assertEquals(
                buildAssessmentsIngestionFlowFileResult(),
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
        ingestionFlowFileDTO.setIngestionFlowFileType(IngestionFlowFile.IngestionFlowFileTypeEnum.ASSESSMENTS);
        Iterator<AssessmentsIngestionFlowFileDTO> iterator = buildAssessmentsIngestionFlowFileDTO();
        List<CsvException> readerExceptions = List.of();

        Path filePath = Files.createFile(Path.of(ingestionFlowFileDTO.getFilePathName()).resolve(ingestionFlowFileDTO.getFileName()));
        List<Path> mockedListPath = List.of(filePath);

        Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId))
                .thenReturn(Optional.of(ingestionFlowFileDTO));

        doReturn(mockedListPath).when(ingestionFlowFileRetrieverServiceMock)
                .retrieveAndUnzipFile(ingestionFlowFileDTO.getOrganizationId(), Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());

        Mockito.when(csvServiceMock.readCsv(eq(filePath), eq(AssessmentsIngestionFlowFileDTO.class), any(), isNull()))
                .thenAnswer(invocation -> {
                    BiFunction<Iterator<AssessmentsIngestionFlowFileDTO>, List<CsvException>, AssessmentsIngestionFlowFileResult> rowProcessor = invocation.getArgument(2);
                    return rowProcessor.apply(iterator, readerExceptions);
                });

        Mockito.when(assessmentsProcessingServiceMock.processAssessments(same(iterator), same(readerExceptions), eq(ingestionFlowFileDTO), eq(filePath.getParent())))
                .thenThrow(new RestClientException("Error"));

        // When & Then
        assertThrows(InvalidIngestionFileException.class, () -> activity.processFile(ingestionFlowFileId));
    }

    private AssessmentsIngestionFlowFileResult buildAssessmentsIngestionFlowFileResult() {
        return AssessmentsIngestionFlowFileResult.builder()
                .organizationId(10L)
                .processedRows(20L)
                .totalRows(30L)
                .discardedFileName("dicardedFileName")
                .errorDescription("errorDescription")
                .build();
    }

    private Iterator<AssessmentsIngestionFlowFileDTO> buildAssessmentsIngestionFlowFileDTO() {
        List<AssessmentsIngestionFlowFileDTO> assessmentsIngestionFlowFileDTOS = List.of(
                AssessmentsIngestionFlowFileDTO.builder()
                        .assessmentCode("ACC001")
                        .organizationIpaCode("IPA001")
                        .debtPositionTypeOrgCode("DPT001")
                        .iuv("IUV001")
                        .iud("IUD001")
                        .iur("IUR001")
                        .officeCode("OFF001")
                        .sectionCode("SEC001")
                        .debtorFiscalCode("CFD001")
                        .amountCents(1000L)
                        .amountSubmitted(true)
                        .build(),
                AssessmentsIngestionFlowFileDTO.builder()
                        .assessmentCode("ACC002")
                        .organizationIpaCode("IPA002")
                        .debtPositionTypeOrgCode("DPT002")
                        .iuv("IUV002")
                        .iud("IUD002")
                        .iur("IUR002")
                        .officeCode("OFF002")
                        .sectionCode("SEC002")
                        .debtorFiscalCode("CFD002")
                        .amountCents(2000L)
                        .amountSubmitted(false)
                        .build()
        );

        return assessmentsIngestionFlowFileDTOS.iterator();
    }
}
