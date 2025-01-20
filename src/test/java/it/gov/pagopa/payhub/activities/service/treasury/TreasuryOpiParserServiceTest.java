package it.gov.pagopa.payhub.activities.service.treasury;

import it.gov.pagopa.payhub.activities.connector.classification.TreasuryService;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIufResult;
import it.gov.pagopa.payhub.activities.exception.TreasuryOpiInvalidFileException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TreasuryOpiParserServiceTest {

    private TreasuryOpiParserService treasuryOpiParserService;
    private List<TreasuryVersionHandlerService> versionHandlerServices;
    private TreasuryService treasuryService;

    @BeforeEach
    void setUp() {
        versionHandlerServices = new ArrayList<>();
        treasuryService = mock(TreasuryService.class);
        treasuryOpiParserService = new TreasuryOpiParserService(versionHandlerServices, treasuryService);
    }

    @Test
    void testParseData_whenValidFile_thenProcessesSuccessfully() {
        // Given
        Path filePath = mock(Path.class);
        File file = mock(File.class);
        when(filePath.toFile()).thenReturn(file);

        IngestionFlowFileDTO ingestionFlowFileDTO = new IngestionFlowFileDTO();
        TreasuryVersionHandlerService handler = mock(TreasuryVersionHandlerService.class);
        versionHandlerServices.add(handler);

        Treasury treasuryDTO = Treasury.builder()
            .iuf("Flow123")
            .organizationId(123L)
            .build();
        List<Treasury> handlerResult = List.of(treasuryDTO);

        when(handler.handle(file, ingestionFlowFileDTO, 1)).thenReturn(handlerResult);

        // When
        TreasuryIufResult result = treasuryOpiParserService.parseData(filePath, ingestionFlowFileDTO, 1);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(1, result.getIufs().size());
        assertEquals("Flow123", result.getIufs().getFirst());
        verify(treasuryService, times(1)).insert(treasuryDTO);
    }

    @Test
    void testParseData_whenAllHandlersFail_thenThrowsException() {
        // Given
        Path filePath = mock(Path.class);
        File file = mock(File.class);
        when(filePath.toFile()).thenReturn(file);

        IngestionFlowFileDTO ingestionFlowFileDTO = new IngestionFlowFileDTO();

        TreasuryVersionHandlerService handler1 = mock(TreasuryVersionHandlerService.class);
        TreasuryVersionHandlerService handler2 = mock(TreasuryVersionHandlerService.class);
        versionHandlerServices.addAll(List.of(handler1, handler2));

        when(handler1.handle(file, ingestionFlowFileDTO, 1)).thenReturn(Collections.emptyList());
        when(handler2.handle(file, ingestionFlowFileDTO, 1)).thenReturn(Collections.emptyList());

        // When & Then
        assertThrows(TreasuryOpiInvalidFileException.class, () ->
                treasuryOpiParserService.parseData(filePath, ingestionFlowFileDTO, 1));
    }

    @Test
    void testParseData_whenMultipleHandlers_thenUsesFirstValid() {
        // Given
        Path filePath = mock(Path.class);
        File file = mock(File.class);
        when(filePath.toFile()).thenReturn(file);

        IngestionFlowFileDTO ingestionFlowFileDTO = new IngestionFlowFileDTO();

        TreasuryVersionHandlerService handler1 = mock(TreasuryVersionHandlerService.class);
        TreasuryVersionHandlerService handler2 = mock(TreasuryVersionHandlerService.class);
        versionHandlerServices.addAll(List.of(handler1, handler2));

        when(handler1.handle(file, ingestionFlowFileDTO, 1)).thenReturn(Collections.emptyList());

        Treasury treasuryDTO = Treasury.builder()
            .iuf("Flow123")
            .organizationId(123L)
            .build();
        List<Treasury> handlerResult = List.of(treasuryDTO);

        when(handler2.handle(file, ingestionFlowFileDTO, 1)).thenReturn(handlerResult);

        // When
        TreasuryIufResult result = treasuryOpiParserService.parseData(filePath, ingestionFlowFileDTO, 1);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(1, result.getIufs().size());
        assertEquals("Flow123", result.getIufs().getFirst());
        verify(treasuryService, times(1)).insert(treasuryDTO);
    }
}
