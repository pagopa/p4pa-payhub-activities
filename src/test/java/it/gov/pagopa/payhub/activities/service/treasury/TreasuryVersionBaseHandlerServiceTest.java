package it.gov.pagopa.payhub.activities.service.treasury;

import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryErrorDTO;
import it.gov.pagopa.payhub.activities.enums.TreasuryOperationEnum;
import it.gov.pagopa.payhub.activities.exception.TreasuryOpiInvalidFileException;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileArchiverService;
import it.gov.pagopa.payhub.activities.util.CsvUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import static org.mockito.Mockito.*;

class TreasuryVersionBaseHandlerServiceTest {

    private TreasuryMapperService<Object> mapperService;
    private TreasuryValidatorService<Object> validatorService;
    private TreasuryVersionBaseHandlerService<Object> handlerService;
    private IngestionFlowFileArchiverService ingestionFlowFileArchiverService;
    @TempDir
    Path workingDir;


    @BeforeEach
    void setUp() {
        mapperService = mock(TreasuryMapperService.class);
        validatorService = mock(TreasuryValidatorService.class);
        ingestionFlowFileArchiverService = mock(IngestionFlowFileArchiverService.class);
        handlerService = new TreasuryVersionBaseHandlerService<>(mapperService, validatorService, ingestionFlowFileArchiverService) {
            @Override
            Object unmarshall(File file) {
                return new Object();
            }
        };
    }

    @Test //OK
    void testHandle_whenValidFile_thenReturnsResult() {
        // Given
        File file = mock(File.class);
        IngestionFlowFileDTO ingestionFlowFileDTO = new IngestionFlowFileDTO();
        ingestionFlowFileDTO.setFileName("testFile");

        Object unmarshalledObject = new Object();
        Map<TreasuryOperationEnum, List<TreasuryDTO>> expectedResult = Map.of();


        Mockito.when(validatorService.validatePageSize(unmarshalledObject, 1)).thenReturn(true);
        Mockito.when(validatorService.validateData(unmarshalledObject, ingestionFlowFileDTO.getFileName())).thenReturn(new ArrayList<>());
        Mockito.when(mapperService.apply(unmarshalledObject, ingestionFlowFileDTO)).thenReturn(expectedResult);

        // When
        Map<TreasuryOperationEnum, List<TreasuryDTO>> result = handlerService.handle(file, ingestionFlowFileDTO, 1, "errorDir");

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    void testHandle_whenValidationFails_thenReturnsEmptyMap() {
        // Given
        File file = mock(File.class);
        IngestionFlowFileDTO ingestionFlowFileDTO = new IngestionFlowFileDTO();
        ingestionFlowFileDTO.setFileName("testFile");

        Object unmarshalledObject = new Object();

        Mockito.when(validatorService.validatePageSize(unmarshalledObject, 1)).thenReturn(true);

        // When
        Map<TreasuryOperationEnum, List<TreasuryDTO>> result = handlerService.handle(file, ingestionFlowFileDTO, 1, "errorDir");

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
        Mockito.verify(mapperService, never()).apply(new Object(), new IngestionFlowFileDTO());
    }

    @Test //OK
    void testHandle_whenUnmarshallFails_thenReturnsEmptyMap() {
        // Given
        handlerService = new TreasuryVersionBaseHandlerService<>(mapperService, validatorService, ingestionFlowFileArchiverService) {
            @Override
            Object unmarshall(File file) {
                throw new RuntimeException("Unmarshall failed");
            }
        };
        File file = mock(File.class);
        IngestionFlowFileDTO ingestionFlowFileDTO = new IngestionFlowFileDTO();
        ingestionFlowFileDTO.setFileName("testFile");

        // When
        Map<TreasuryOperationEnum, List<TreasuryDTO>> result = handlerService.handle(file, ingestionFlowFileDTO, 1, "errorDir");

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
        Mockito.verify(mapperService, never()).apply(any(), any());
    }

    @Test //OK
    void testHandle_whenMapperFails_thenReturnsEmptyMap() {
        // Given
        File file = mock(File.class);
        IngestionFlowFileDTO ingestionFlowFileDTO = new IngestionFlowFileDTO();
        ingestionFlowFileDTO.setFileName("testFile");

        Object unmarshalledObject = new Object();

        Mockito.when(validatorService.validatePageSize(unmarshalledObject, 1)).thenReturn(false);
        Mockito.when(validatorService.validateData(unmarshalledObject, ingestionFlowFileDTO.getFileName())).thenReturn(new ArrayList<>());
        Mockito.when(mapperService.apply(unmarshalledObject, ingestionFlowFileDTO)).thenThrow(new RuntimeException("Mapper failed"));

        // When
        Map<TreasuryOperationEnum, List<TreasuryDTO>> result = handlerService.handle(file, ingestionFlowFileDTO, 1, "errorDir");

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void testValidate_whenPageSizeInvalid_thenThrowsException() {
        // Given
        IngestionFlowFileDTO ingestionFlowFileDTO = new IngestionFlowFileDTO();
        ingestionFlowFileDTO.setFileName("testFile");
        Object unmarshalledObject = new Object();

        Mockito.when(validatorService.validatePageSize(unmarshalledObject, 1)).thenReturn(true);

        // When & Then
        Assertions.assertThrows(TreasuryOpiInvalidFileException.class, () ->
                handlerService.validate(ingestionFlowFileDTO, 1, unmarshalledObject));
    }

    @Test
    void testValidate_whenValidationSucceeds_thenReturnsErrors() {
        // Given
        IngestionFlowFileDTO ingestionFlowFileDTO = new IngestionFlowFileDTO();
        ingestionFlowFileDTO.setFileName("testFile");
        Object unmarshalledObject = new Object();

        Mockito.when(validatorService.validatePageSize(unmarshalledObject, 1)).thenReturn(false);
        List<TreasuryErrorDTO> expectedErrors = List.of(new TreasuryErrorDTO("file", "2023", "B123", "ERR01", "Invalid data"));
        Mockito.when(validatorService.validateData(unmarshalledObject, ingestionFlowFileDTO.getFileName())).thenReturn(expectedErrors);

        // When
        List<TreasuryErrorDTO> result = handlerService.validate(ingestionFlowFileDTO, 1, unmarshalledObject);

        // Then
        Assertions.assertEquals(expectedErrors, result);
    }

    @Test
    void testArchiveErrorFile_whenCalled_thenArchivesFile() throws IOException {
        // Given
        File errorFile = mock(File.class);
        Mockito.when(errorFile.getParent()).thenReturn(workingDir.toString());
        Mockito.when(errorFile.getName()).thenReturn("errorFile.csv");

        String targetDir = "errorDir";

        // When
        handlerService.archiveErrorFile(errorFile, targetDir);

        // Then
        Mockito.verify(ingestionFlowFileArchiverService, times(1)).archive(
                List.of(Path.of("parentDir", "errorFile.csv")),
                Path.of("parentDir", targetDir)
        );
    }


    @Test
    void testWriteErrors_whenErrorsExist_thenProcessesAndArchives() throws IOException {
        // Given
        List<TreasuryErrorDTO> errors = List.of(
                new TreasuryErrorDTO("file1", "2025", "B123", "ERR01", "Invalid data"),
                new TreasuryErrorDTO("file2", "2025", "B124", "ERR02", "Missing field")
        );

        String fileName = "testFile.xml";
        String errorDirectory = "/errorDir/";
        String expectedErrorFilePath = "ERROR-" + fileName;

        try (MockedStatic<CsvUtils> csvUtilsMock = mockStatic(CsvUtils.class)) {
            doNothing().when(ingestionFlowFileArchiverService).archive(
                    List.of(Path.of(expectedErrorFilePath)),
                    Path.of(errorDirectory)
            );

            // When
            handlerService.writeErrors(errors, fileName, errorDirectory);

            // Then
            csvUtilsMock.verify(() -> CsvUtils.createCsv(eq(expectedErrorFilePath), any(), any()), times(1));
            verify(ingestionFlowFileArchiverService, times(1)).archive(
                    List.of(Path.of(expectedErrorFilePath)),
                    Path.of(errorDirectory)
            );
        }
    }





}
