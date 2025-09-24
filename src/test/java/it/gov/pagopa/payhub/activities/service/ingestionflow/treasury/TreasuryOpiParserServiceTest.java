package it.gov.pagopa.payhub.activities.service.ingestionflow.treasury;

import it.gov.pagopa.payhub.activities.connector.classification.TreasuryService;
import it.gov.pagopa.payhub.activities.dto.ingestion.IngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.exception.treasury.TreasuryOpiInvalidFileException;
import it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker;
import it.gov.pagopa.payhub.activities.util.faker.TreasuryFaker;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void testParseData_whenValidFile_thenProcessesSuccessfully(boolean isIufPresent) {
        // Given
        Path filePath = mock(Path.class);
        File file = mock(File.class);
        when(filePath.toFile()).thenReturn(file);

        IngestionFlowFile ingestionFlowFileDTO = IngestionFlowFileFaker.buildIngestionFlowFile()
                .organizationId(1L);
        TreasuryVersionHandlerService handler = mock(TreasuryVersionHandlerService.class);
        versionHandlerServices.add(handler);

        Treasury treasuryDTO = TreasuryFaker.buildTreasuryDTO()
                .treasuryId("treasury123")
                .iuf(isIufPresent ? "Flow123" : null);
        Pair<IngestionFlowFileResult, List<Treasury>> handlerResult = Pair.of(new IngestionFlowFileResult(), List.of(treasuryDTO));

        when(handler.handle(file, ingestionFlowFileDTO, 1)).thenReturn(handlerResult);
        when(treasuryService.insert(treasuryDTO)).thenReturn(treasuryDTO);

        // When
        Pair<IngestionFlowFileResult, Map<String, String>> result = treasuryOpiParserService.parseData(filePath, ingestionFlowFileDTO, 1);

        // Then
        assertNotNull(result);
        assertSame(handlerResult.getLeft(), result.getLeft());
        assertEquals(isIufPresent ? 1 : 0, result.getRight().size());
        verify(treasuryService, times(isIufPresent ? 1 : 0)).insert(treasuryDTO);
        if (isIufPresent) {
            assertEquals("treasury123", result.getRight().get("Flow123"));
        }
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

        when(handler1.handle(file, ingestionFlowFileDTO, 1)).thenReturn(Pair.of(new IngestionFlowFileResult(), null));
        when(handler2.handle(file, ingestionFlowFileDTO, 1)).thenReturn(Pair.of(new IngestionFlowFileResult(), null));

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

        IngestionFlowFile ingestionFlowFileDTO = IngestionFlowFileFaker.buildIngestionFlowFile()
                .organizationId(1L);

        TreasuryVersionHandlerService handler1 = mock(TreasuryVersionHandlerService.class);
        TreasuryVersionHandlerService handler2 = mock(TreasuryVersionHandlerService.class);
        versionHandlerServices.addAll(List.of(handler1, handler2));

        Treasury treasuryDTO = TreasuryFaker.buildTreasuryDTO()
                .treasuryId("treasury123")
                .iuf("Flow456");

        Pair<IngestionFlowFileResult, List<Treasury>> handler1Result = Pair.of(new IngestionFlowFileResult(), List.of(treasuryDTO));
        when(handler1.handle(file, ingestionFlowFileDTO, 1)).thenReturn(handler1Result);

        when(handler2.handle(file, ingestionFlowFileDTO, 1)).thenReturn(Pair.of(new IngestionFlowFileResult(), Collections.emptyList()));
        when(treasuryService.insert(treasuryDTO)).thenReturn(treasuryDTO);

        // When
        Pair<IngestionFlowFileResult, Map<String, String>> result = treasuryOpiParserService.parseData(filePath, ingestionFlowFileDTO, 1);

        // Then
        assertNotNull(result);
        assertSame(handler1Result.getLeft(), result.getLeft());
        assertEquals(1, result.getRight().size());
        assertEquals("treasury123", result.getRight().get("Flow456"));
        verify(treasuryService, times(1)).insert(treasuryDTO);
    }
}
