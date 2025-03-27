package it.gov.pagopa.payhub.activities.activity.ingestionflow.debtposition;

import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtposition.InstallmentIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtposition.InstallmentIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.InvalidIngestionFileException;
import it.gov.pagopa.payhub.activities.service.CsvService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.debtposition.InstallmentProcessingService;
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
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker.buildIngestionFlowFile;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class InstallmentIngestionFlowFileActivityTest {

    @Mock
    private CsvService csvServiceMock;
    @Mock
    private InstallmentProcessingService installmentProcessingServiceMock;
    @Mock
    private IngestionFlowFileService ingestionFlowFileServiceMock;
    @Mock
    private IngestionFlowFileRetrieverService ingestionFlowFileRetrieverServiceMock;
    @Mock
    private IngestionFlowFileArchiverService ingestionFlowFileArchiverServiceMock;

    private InstallmentIngestionFlowFileActivity activity;

    @BeforeEach
    void setUp() {
        activity = new InstallmentIngestionFlowFileActivityImpl(
                ingestionFlowFileServiceMock,
                ingestionFlowFileRetrieverServiceMock,
                ingestionFlowFileArchiverServiceMock,
                csvServiceMock,
                installmentProcessingServiceMock
        );
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                ingestionFlowFileServiceMock,
                ingestionFlowFileRetrieverServiceMock,
                ingestionFlowFileArchiverServiceMock,
                csvServiceMock,
                installmentProcessingServiceMock
        );
    }

    @TempDir
    Path workingDir;

    @Test
    void givenValidIngestionFlowWhenProcessFileThenOk() throws IOException {
        // Given
        Long ingestionFlowFileId = 1L;
        IngestionFlowFile ingestionFlowFileDTO = buildIngestionFlowFile();
        ingestionFlowFileDTO.setFilePathName(workingDir.toString());
        ingestionFlowFileDTO.setIngestionFlowFileType(IngestionFlowFile.IngestionFlowFileTypeEnum.DP_INSTALLMENTS);
        Iterator<InstallmentIngestionFlowFileDTO> iterator = buildInstallmentIngestionFlowFileDTO();

        Path filePath = Files.createFile(Path.of(ingestionFlowFileDTO.getFilePathName()).resolve(ingestionFlowFileDTO.getFileName()));
        List<Path> mockedListPath = List.of(filePath);

        Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId))
                .thenReturn(Optional.of(ingestionFlowFileDTO));

        doReturn(mockedListPath).when(ingestionFlowFileRetrieverServiceMock)
                .retrieveAndUnzipFile(ingestionFlowFileDTO.getOrganizationId(), Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());

        Mockito.when(csvServiceMock.readCsv(eq(filePath), eq(InstallmentIngestionFlowFileDTO.class), any()))
                .thenAnswer(invocation -> {
                    Function<Iterator<InstallmentIngestionFlowFileDTO>, InstallmentIngestionFlowFileResult> rowProcessor = invocation.getArgument(2);
                    return rowProcessor.apply(iterator);
                });

        Mockito.when(installmentProcessingServiceMock.processInstallments(any(), eq(ingestionFlowFileDTO), eq(filePath.getParent())))
                .thenReturn(buildInstallmentIngestionFlowFileResult());

        // When
        InstallmentIngestionFlowFileResult result = activity.processFile(ingestionFlowFileId);

        // Then
        Assertions.assertNotNull(result);
        Mockito.verify(ingestionFlowFileArchiverServiceMock, Mockito.times(1)).archive(ingestionFlowFileDTO);
        Assertions.assertFalse(filePath.toFile().exists());
    }

    @Test
    void givenValidIngestionFlowWhenExceptionThenThrowInvalidIngestionFileException() throws IOException {
        // Given
        Long ingestionFlowFileId = 1L;
        IngestionFlowFile ingestionFlowFileDTO = buildIngestionFlowFile();
        ingestionFlowFileDTO.setFilePathName(workingDir.toString());
        ingestionFlowFileDTO.setIngestionFlowFileType(IngestionFlowFile.IngestionFlowFileTypeEnum.DP_INSTALLMENTS);
        Iterator<InstallmentIngestionFlowFileDTO> iterator = buildInstallmentIngestionFlowFileDTO();

        Path filePath = Files.createFile(Path.of(ingestionFlowFileDTO.getFilePathName()).resolve(ingestionFlowFileDTO.getFileName()));
        List<Path> mockedListPath = List.of(filePath);

        Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId))
                .thenReturn(Optional.of(ingestionFlowFileDTO));

        doReturn(mockedListPath).when(ingestionFlowFileRetrieverServiceMock)
                .retrieveAndUnzipFile(ingestionFlowFileDTO.getOrganizationId(), Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());

        Mockito.when(csvServiceMock.readCsv(eq(filePath), eq(InstallmentIngestionFlowFileDTO.class), any()))
                .thenAnswer(invocation -> {
                    Function<Iterator<InstallmentIngestionFlowFileDTO>, InstallmentIngestionFlowFileResult> rowProcessor = invocation.getArgument(2);
                    return rowProcessor.apply(iterator);
                });

        Mockito.when(installmentProcessingServiceMock.processInstallments(any(), eq(ingestionFlowFileDTO), eq(filePath.getParent())))
                .thenThrow(new RestClientException("Error"));

        // When & Then
        assertThrows(InvalidIngestionFileException.class, () -> activity.processFile(ingestionFlowFileId));
    }

    private InstallmentIngestionFlowFileResult buildInstallmentIngestionFlowFileResult() {
        return InstallmentIngestionFlowFileResult.builder()
                .totalRows(2L)
                .processedRows(2L)
                .errorDescription("errorDescription")
                .discardedFileName("discardedFileName")
                .build();
    }

    private Iterator<InstallmentIngestionFlowFileDTO> buildInstallmentIngestionFlowFileDTO() {
        List<InstallmentIngestionFlowFileDTO> installmentIngestionFlowFileDTOList = List.of(
                InstallmentIngestionFlowFileDTO.builder()
                        .iupdOrg("iupd1")
                        .iud("iud1")
                        .amount(BigDecimal.valueOf(1L))
                        .build(),
                InstallmentIngestionFlowFileDTO.builder()
                        .iupdOrg("iupd2")
                        .iud("iud2")
                        .amount(BigDecimal.valueOf(2L))
                        .build()
        );

        return installmentIngestionFlowFileDTOList.iterator();
    }
}
