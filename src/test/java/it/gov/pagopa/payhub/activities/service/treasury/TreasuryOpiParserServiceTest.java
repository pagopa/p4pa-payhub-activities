package it.gov.pagopa.payhub.activities.service.treasury;

import it.gov.pagopa.payhub.activities.dao.TreasuryDao;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIufResult;
import it.gov.pagopa.payhub.activities.enums.TreasuryOperationEnum;
import it.gov.pagopa.payhub.activities.exception.TreasuryOpiInvalidFileException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TreasuryOpiParserServiceTest {

    private TreasuryOpiParserService treasuryOpiParserService;
    private List<TreasuryVersionHandlerService> versionHandlerServices;
    private TreasuryDao treasuryDao;

    @BeforeEach
    void setUp() {
        versionHandlerServices = new ArrayList<>();
        treasuryDao = mock(TreasuryDao.class);
        treasuryOpiParserService = new TreasuryOpiParserService(versionHandlerServices, treasuryDao);
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

        TreasuryDTO treasuryDTO = TreasuryDTO.builder()
                .iuf("Flow123")
                .build();
        Map<TreasuryOperationEnum, List<TreasuryDTO>> handlerResult = Map.of(
                TreasuryOperationEnum.INSERT, List.of(treasuryDTO)
        );

        when(handler.handle(file, ingestionFlowFileDTO, 1, "errorDir")).thenReturn(handlerResult);

        // When
        TreasuryIufResult result = treasuryOpiParserService.parseData(filePath, ingestionFlowFileDTO, 1, "errorDir");

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(1, result.getIufs().size());
        assertEquals("Flow123", result.getIufs().getFirst());
        verify(treasuryDao, times(1)).insert(treasuryDTO);
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

        when(handler1.handle(file, ingestionFlowFileDTO, 1, "errorDir")).thenReturn(Collections.emptyMap());
        when(handler2.handle(file, ingestionFlowFileDTO, 1, "errorDir")).thenReturn(Collections.emptyMap());

        // When & Then
        assertThrows(TreasuryOpiInvalidFileException.class, () ->
                treasuryOpiParserService.parseData(filePath, ingestionFlowFileDTO, 1, "errorDir"));
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

        when(handler1.handle(file, ingestionFlowFileDTO, 1, "errorDir")).thenReturn(Collections.emptyMap());

        TreasuryDTO treasuryDTO = TreasuryDTO.builder()
                .iuf("Flow456")
                .build();
        Map<TreasuryOperationEnum, List<TreasuryDTO>> handlerResult = Map.of(
                TreasuryOperationEnum.INSERT, List.of(treasuryDTO)
        );

        when(handler2.handle(file, ingestionFlowFileDTO, 1, "errorDir")).thenReturn(handlerResult);

        // When
        TreasuryIufResult result = treasuryOpiParserService.parseData(filePath, ingestionFlowFileDTO, 1, "errorDir");

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(1, result.getIufs().size());
        assertEquals("Flow456", result.getIufs().get(0));
        verify(treasuryDao, times(1)).insert(treasuryDTO);
    }
}
