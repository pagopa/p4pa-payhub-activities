package it.gov.pagopa.payhub.activities.service.treasury;

import it.gov.pagopa.payhub.activities.exception.TreasuryOpiInvalidFileException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.nio.file.Path;


class TreasuryOpiParserServiceTest {

    private TreasuryUnmarshallerService treasuryUnmarshallerService;
    private TreasuryOpiParserService treasuryOpiParserService;

    @BeforeEach
    void setUp() {
        treasuryUnmarshallerService = Mockito.mock(TreasuryUnmarshallerService.class);
        treasuryOpiParserService = new TreasuryOpiParserService(treasuryUnmarshallerService);
    }

    @Test
    void testParseData_whenValidOpi161File_thenProcessSuccessfully() {
        // Given
        Path filePath = Mockito.mock(Path.class);
        File file = Mockito.mock(File.class);
        Mockito.when(filePath.toFile()).thenReturn(file);

        var flussoGiornaleDiCassa161 = Mockito.mock(it.gov.pagopa.payhub.activities.xsd.treasury.opi161.FlussoGiornaleDiCassa.class);
        Mockito.when(treasuryUnmarshallerService.unmarshalOpi161(file)).thenReturn(flussoGiornaleDiCassa161);


        // When
        var result = treasuryOpiParserService.parseData(filePath);

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty()); // Since the method currently returns an empty list
        Mockito.verify(treasuryUnmarshallerService, Mockito.times(1)).unmarshalOpi161(file);
        Mockito.verify(treasuryUnmarshallerService, Mockito.never()).unmarshalOpi14(file);
    }

    @Test
    void testParseData_whenValidOpi14File_thenProcessSuccessfully() {
        // Given
        Path filePath = Mockito.mock(Path.class);
        File file = Mockito.mock(File.class);
        Mockito.when(filePath.toFile()).thenReturn(file);

        Mockito.when(treasuryUnmarshallerService.unmarshalOpi161(file)).thenThrow(new RuntimeException("Error parsing OPI 1.6.1"));

        var flussoGiornaleDiCassa14 = Mockito.mock(it.gov.pagopa.payhub.activities.xsd.treasury.opi14.FlussoGiornaleDiCassa.class);
        Mockito.when(treasuryUnmarshallerService.unmarshalOpi14(file)).thenReturn(flussoGiornaleDiCassa14);


        // When
        var result = treasuryOpiParserService.parseData(filePath);

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty()); // Since the method currently returns an empty list
        Mockito.verify(treasuryUnmarshallerService, Mockito.times(1)).unmarshalOpi161(file);
        Mockito.verify(treasuryUnmarshallerService, Mockito.times(1)).unmarshalOpi14(file);
    }

    @Test
    void testParseData_whenInvalidFile_thenThrowException() {
        // Given
        Path filePath = Mockito.mock(Path.class);
        File file = Mockito.mock(File.class);
        Mockito.when(filePath.toFile()).thenReturn(file);

        Mockito.when(treasuryUnmarshallerService.unmarshalOpi161(file)).thenThrow(new RuntimeException("Error parsing OPI 1.6.1"));
        Mockito.when(treasuryUnmarshallerService.unmarshalOpi14(file)).thenThrow(new RuntimeException("Error parsing OPI 1.4"));


        // When & Then
        Assertions.assertThrows(TreasuryOpiInvalidFileException.class, () ->
                treasuryOpiParserService.parseData(filePath));
        Mockito.verify(treasuryUnmarshallerService, Mockito.times(1)).unmarshalOpi161(file);
        Mockito.verify(treasuryUnmarshallerService, Mockito.times(1)).unmarshalOpi14(file);
    }
}
