package it.gov.pagopa.payhub.activities.service.treasury;

import it.gov.pagopa.payhub.activities.connector.classification.TreasuryService;
import it.gov.pagopa.payhub.activities.exception.treasury.TreasuryOpiInvalidFileException;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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

        IngestionFlowFile ingestionFlowFileDTO = IngestionFlowFile.builder()
            .organizationId(1L)
            .build();
        TreasuryVersionHandlerService handler = mock(TreasuryVersionHandlerService.class);
        versionHandlerServices.add(handler);

        Treasury treasuryDTO = Treasury.builder()
            .treasuryId("treasury123")
            .iuf("Flow123")
            .build();
        List<Treasury> handlerResult = List.of(treasuryDTO);

        when(handler.handle(file, ingestionFlowFileDTO, 1)).thenReturn(handlerResult);
        when(treasuryService.insert(treasuryDTO)).thenReturn(treasuryDTO);

        // When
        Map<String, String> result = treasuryOpiParserService.parseData(filePath, ingestionFlowFileDTO, 1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("treasury123", result.get("Flow123"));
        verify(treasuryService, times(1)).insert(treasuryDTO);
    }

    @Test
    void testParseData_whenAllHandlersFail_thenThrowsException() {
        // Given
        Path filePath = mock(Path.class);
        File file = mock(File.class);
        when(filePath.toFile()).thenReturn(file);

        IngestionFlowFile ingestionFlowFileDTO = new IngestionFlowFile();

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

        IngestionFlowFile ingestionFlowFileDTO = IngestionFlowFile.builder()
            .organizationId(1L)
            .build();

        TreasuryVersionHandlerService handler1 = mock(TreasuryVersionHandlerService.class);
        TreasuryVersionHandlerService handler2 = mock(TreasuryVersionHandlerService.class);
        versionHandlerServices.addAll(List.of(handler1, handler2));

        when(handler1.handle(file, ingestionFlowFileDTO, 1)).thenReturn(Collections.emptyList());

        Treasury treasuryDTO = Treasury.builder()
            .treasuryId("treasury123")
            .iuf("Flow456")
            .build();
        List<Treasury> handlerResult = List.of(treasuryDTO);

        when(handler2.handle(file, ingestionFlowFileDTO, 1)).thenReturn(handlerResult);
        when(treasuryService.insert(treasuryDTO)).thenReturn(treasuryDTO);

        // When
        Map<String, String> result = treasuryOpiParserService.parseData(filePath, ingestionFlowFileDTO, 1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("treasury123", result.get("Flow456"));
        verify(treasuryService, times(1)).insert(treasuryDTO);
    }
}
