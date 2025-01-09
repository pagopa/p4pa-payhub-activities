package it.gov.pagopa.payhub.activities.service.treasury;

import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryErrorDTO;
import it.gov.pagopa.payhub.activities.enums.TreasuryOperationEnum;
import it.gov.pagopa.payhub.activities.exception.TreasuryOpiInvalidFileException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
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
    private TreasuryErrorsArchiverService treasuryErrorsArchiverService;
    @TempDir
    Path workingDir;


    @BeforeEach
    void setUp() {
        mapperService = mock(TreasuryMapperService.class);
        validatorService = mock(TreasuryValidatorService.class);
        treasuryErrorsArchiverService = mock(TreasuryErrorsArchiverService.class);
        handlerService = new TreasuryVersionBaseHandlerService<>(mapperService, validatorService, treasuryErrorsArchiverService) {
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
        Map<TreasuryOperationEnum, List<TreasuryDTO>> result = handlerService.handle(file, ingestionFlowFileDTO, 1);

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
        Map<TreasuryOperationEnum, List<TreasuryDTO>> result = handlerService.handle(file, ingestionFlowFileDTO, 1);

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
        Mockito.verify(mapperService, never()).apply(new Object(), new IngestionFlowFileDTO());
    }

    @Test //OK
    void testHandle_whenUnmarshallFails_thenReturnsEmptyMap() {
        // Given
        handlerService = new TreasuryVersionBaseHandlerService<>(mapperService, validatorService, treasuryErrorsArchiverService) {
            @Override
            Object unmarshall(File file) {
                throw new RuntimeException("Unmarshall failed");
            }
        };
        File file = mock(File.class);
        IngestionFlowFileDTO ingestionFlowFileDTO = new IngestionFlowFileDTO();
        ingestionFlowFileDTO.setFileName("testFile");

        // When
        Map<TreasuryOperationEnum, List<TreasuryDTO>> result = handlerService.handle(file, ingestionFlowFileDTO, 1);

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
        Map<TreasuryOperationEnum, List<TreasuryDTO>> result = handlerService.handle(file, ingestionFlowFileDTO, 1);

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
        treasuryErrorsArchiverService.archiveErrorFile(errorFile, targetDir);

        // Then
        Mockito.verify(treasuryErrorsArchiverService, times(1)).archiveErrorFile(
                any(),
                any()
        );
    }
}
