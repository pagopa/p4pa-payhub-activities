package it.gov.pagopa.payhub.activities.service.treasury;

import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.enums.TreasuryOperationEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TreasuryVersionBaseHandlerServiceTest {

    private TreasuryMapperService<Object> mapperService;
    private TreasuryValidatorService<Object> validatorService;
    private TreasuryVersionBaseHandlerService<Object> handlerService;

    @BeforeEach
    void setUp() {
        mapperService = mock(TreasuryMapperService.class);
        validatorService = mock(TreasuryValidatorService.class);

        handlerService = new TreasuryVersionBaseHandlerService<>(mapperService, validatorService) {
            @Override
            Object unmarshall(File file) {
                return new Object();
            }
        };
    }

    @Test
    void testHandle_whenValidFile_thenReturnsResult() {
        // Given
        File file = mock(File.class);
        IngestionFlowFileDTO ingestionFlowFileDTO = new IngestionFlowFileDTO();
        ingestionFlowFileDTO.setFileName("testFile");

        Object unmarshalledObject = new Object();
        Map<TreasuryOperationEnum, List<TreasuryDTO>> expectedResult = Map.of();


        when(validatorService.validatePageSize(unmarshalledObject, 1)).thenReturn(true);
        when(validatorService.validateData(unmarshalledObject, ingestionFlowFileDTO.getFileName())).thenReturn(new ArrayList<>());
        when(mapperService.apply(unmarshalledObject, ingestionFlowFileDTO)).thenReturn(expectedResult);

        // When
        Map<TreasuryOperationEnum, List<TreasuryDTO>> result = handlerService.handle(file, ingestionFlowFileDTO, 1);

        // Then
        assertNotNull(result);
        assertEquals(expectedResult, result);
    }

    @Test
    void testHandle_whenValidationFails_thenReturnsEmptyMap() {
        // Given
        File file = mock(File.class);
        IngestionFlowFileDTO ingestionFlowFileDTO = new IngestionFlowFileDTO();
        ingestionFlowFileDTO.setFileName("testFile");

        Object unmarshalledObject = new Object();

        when(validatorService.validatePageSize(unmarshalledObject, 1)).thenReturn(true);

        // When
        Map<TreasuryOperationEnum, List<TreasuryDTO>> result = handlerService.handle(file, ingestionFlowFileDTO, 1);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mapperService, never()).apply(new Object(), new IngestionFlowFileDTO());
    }

    @Test
    void testHandle_whenUnmarshallFails_thenReturnsEmptyMap() {
        // Given
        handlerService = new TreasuryVersionBaseHandlerService<>(mapperService, validatorService) {
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
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mapperService, never()).apply(any(), any());
    }

    @Test
    void testHandle_whenMapperFails_thenReturnsEmptyMap() {
        // Given
        File file = mock(File.class);
        IngestionFlowFileDTO ingestionFlowFileDTO = new IngestionFlowFileDTO();
        ingestionFlowFileDTO.setFileName("testFile");

        Object unmarshalledObject = new Object();

        when(validatorService.validatePageSize(unmarshalledObject, 1)).thenReturn(false);
        when(validatorService.validateData(unmarshalledObject, ingestionFlowFileDTO.getFileName())).thenReturn(new ArrayList<>());
        when(mapperService.apply(unmarshalledObject, ingestionFlowFileDTO)).thenThrow(new RuntimeException("Mapper failed"));

        // When
        Map<TreasuryOperationEnum, List<TreasuryDTO>> result = handlerService.handle(file, ingestionFlowFileDTO, 1);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
