package it.gov.pagopa.payhub.activities.service.ingestionflow.treasury;

import it.gov.pagopa.payhub.activities.connector.classification.TreasuryService;
import it.gov.pagopa.payhub.activities.xsd.treasury.opi14.FlussoGiornaleDiCassa;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryErrorDTO;
import it.gov.pagopa.payhub.activities.enums.TreasuryOperationEnum;
import it.gov.pagopa.payhub.activities.exception.treasury.TreasuryOpiInvalidFileException;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TreasuryVersionBaseHandlerServiceTest {

    @Mock
    private TreasuryMapperService<Object> mapperServiceMock;
    @Mock
    private TreasuryValidatorService<Object> validatorServiceMock;
    @Mock
    private TreasuryErrorsArchiverService treasuryErrorsArchiverServiceMock;
    @Mock
    private TreasuryService treasuryServiceMock;

    private FlussoGiornaleDiCassa unmarshalledObject;

    private TreasuryVersionBaseHandlerService<Object> handlerService;

    @BeforeEach
    void setUp() {
        unmarshalledObject = mock(FlussoGiornaleDiCassa.class);

        handlerService = new TreasuryVersionBaseHandlerService<>(mapperServiceMock, validatorServiceMock, treasuryErrorsArchiverServiceMock, treasuryServiceMock) {
            @Override
            protected Object unmarshall(File file) {
                return unmarshalledObject;
            }
        };
    }

    @Test
    void testHandle_whenValidFile_thenReturnsResult() {
        // Given
        File file = mock(File.class);
        IngestionFlowFile ingestionFlowFileDTO = new IngestionFlowFile();
        ingestionFlowFileDTO.setFileName("testFile");

        Map<TreasuryOperationEnum, List<Treasury>> expectedMap = Map.of();
        List<Treasury> expectedResult = List.of();


        Mockito.when(validatorServiceMock.validatePageSize(unmarshalledObject, 1)).thenReturn(true);
        Mockito.when(validatorServiceMock.validateData(unmarshalledObject, ingestionFlowFileDTO.getFileName())).thenReturn(new ArrayList<>());
        Mockito.when(mapperServiceMock.apply(unmarshalledObject, ingestionFlowFileDTO)).thenReturn(expectedMap);

        // When
        List<Treasury> result = handlerService.handle(file, ingestionFlowFileDTO, 1);

        // Then
        assertNotNull(result);
        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    void testHandle_whenValidationFails_thenReturnsEmptyMap() {
        // Given
        File file = mock(File.class);
        IngestionFlowFile ingestionFlowFileDTO = new IngestionFlowFile();
        ingestionFlowFileDTO.setFileName("testFile");

        Mockito.when(validatorServiceMock.validatePageSize(unmarshalledObject, 1)).thenReturn(true);

        // When
        List<Treasury> result = handlerService.handle(file, ingestionFlowFileDTO, 1);

        // Then
        assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
        Mockito.verify(mapperServiceMock, never()).apply(new Object(), new IngestionFlowFile());
    }

    @Test
    void testHandle_whenUnmarshallFails_thenReturnsEmptyMap() {
        // Given
        handlerService = new TreasuryVersionBaseHandlerService<>(mapperServiceMock, validatorServiceMock, treasuryErrorsArchiverServiceMock, treasuryServiceMock) {
            @Override
            protected Object unmarshall(File file) {
                throw new RuntimeException("Unmarshall failed");
            }
        };
        File file = mock(File.class);
        IngestionFlowFile ingestionFlowFileDTO = new IngestionFlowFile();
        ingestionFlowFileDTO.setFileName("testFile");

        // When
        List<Treasury> result = handlerService.handle(file, ingestionFlowFileDTO, 1);

        // Then
        assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
        Mockito.verify(mapperServiceMock, never()).apply(any(), any());
    }

    @Test
    void testHandle_whenMapperFails_thenReturnsEmptyMap() {
        // Given
        File file = mock(File.class);
        IngestionFlowFile ingestionFlowFileDTO = new IngestionFlowFile();
        ingestionFlowFileDTO.setFileName("testFile");


        Mockito.when(validatorServiceMock.validatePageSize(unmarshalledObject, 1)).thenReturn(false);

        // When
        List<Treasury> result = handlerService.handle(file, ingestionFlowFileDTO, 1);

        // Then
        assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void testValidate_whenPageSizeInvalid_thenThrowsException() {
        // Given
        IngestionFlowFile ingestionFlowFileDTO = new IngestionFlowFile();
        ingestionFlowFileDTO.setFileName("testFile");

        Mockito.when(validatorServiceMock.validatePageSize(unmarshalledObject, 1)).thenReturn(false);

        // When & Then
        Assertions.assertThrows(TreasuryOpiInvalidFileException.class, () ->
                handlerService.validate(ingestionFlowFileDTO, 1, unmarshalledObject));
    }

    @Test
    void testValidate_whenValidationSucceeds_thenReturnsErrors() {
        // Given
        IngestionFlowFile ingestionFlowFileDTO = new IngestionFlowFile();
        ingestionFlowFileDTO.setFileName("testFile");

        Mockito.when(validatorServiceMock.validatePageSize(unmarshalledObject, 1)).thenReturn(true);
        List<TreasuryErrorDTO> expectedErrors = List.of(new TreasuryErrorDTO("file", "2023", "B123", "ERR01", "Invalid data"));
        Mockito.when(validatorServiceMock.validateData(unmarshalledObject, ingestionFlowFileDTO.getFileName())).thenReturn(expectedErrors);

        // When
        List<TreasuryErrorDTO> result = handlerService.validate(ingestionFlowFileDTO, 1, unmarshalledObject);

        // Then
        Assertions.assertEquals(expectedErrors, result);
    }


    @Test
    void testHandle_whenDeleteTreasury_thenCallsDeleteByOrganizationIdAndBillCodeAndBillYear() {
        // Given
        File file = mock(File.class);
        IngestionFlowFile ingestionFlowFileDTO = new IngestionFlowFile();
        ingestionFlowFileDTO.setFileName("testFile");

        Treasury treasuryDTO = new Treasury();
        treasuryDTO.setOrganizationId(98765L);
        treasuryDTO.setBillCode("BILL123");
        treasuryDTO.setBillYear("2025");

        Map<TreasuryOperationEnum, List<Treasury>> resultMap = Map.of(
                TreasuryOperationEnum.DELETE, List.of(treasuryDTO)
        );

        when(validatorServiceMock.validatePageSize(unmarshalledObject, 1)).thenReturn(true);
        when(validatorServiceMock.validateData(unmarshalledObject, ingestionFlowFileDTO.getFileName())).thenReturn(new ArrayList<>());
        when(mapperServiceMock.apply(unmarshalledObject, ingestionFlowFileDTO)).thenReturn(resultMap);
        when(treasuryServiceMock.deleteByOrganizationIdAndBillCodeAndBillYear(treasuryDTO.getOrganizationId(), treasuryDTO.getBillCode(), treasuryDTO.getBillYear())).thenReturn(1L);

        // When
        List<Treasury> result = handlerService.handle(file, ingestionFlowFileDTO, 1);

        // Then
        assertNotNull(result);
        verify(treasuryServiceMock, times(1)).deleteByOrganizationIdAndBillCodeAndBillYear(treasuryDTO.getOrganizationId(), treasuryDTO.getBillCode(), treasuryDTO.getBillYear());
    }

    @Test
    void testHandle_whenDeleteTreasury_thenCallsDeleteByOrganizationIdAndBillCodeAndBillYearAndWriteError() {
        // Given
        File file = mock(File.class);
        IngestionFlowFile ingestionFlowFileDTO = new IngestionFlowFile();
        ingestionFlowFileDTO.setFileName("testFile");

        Treasury treasuryDTO = new Treasury();
        treasuryDTO.setOrganizationId(98765L);
        treasuryDTO.setBillCode("BILL123");
        treasuryDTO.setBillYear("2025");

        Map<TreasuryOperationEnum, List<Treasury>> resultMap = Map.of(
                TreasuryOperationEnum.DELETE, List.of(treasuryDTO)
        );

        when(validatorServiceMock.validatePageSize(unmarshalledObject, 1)).thenReturn(true);
        when(validatorServiceMock.validateData(unmarshalledObject, ingestionFlowFileDTO.getFileName())).thenReturn(new ArrayList<>());
        when(mapperServiceMock.apply(unmarshalledObject, ingestionFlowFileDTO)).thenReturn(resultMap);
        when(treasuryServiceMock.deleteByOrganizationIdAndBillCodeAndBillYear(treasuryDTO.getOrganizationId(), treasuryDTO.getBillCode(), treasuryDTO.getBillYear())).thenReturn(0L);

        // When
        List<Treasury> result = handlerService.handle(file, ingestionFlowFileDTO, 1);

        // Then
        assertNotNull(result);
        verify(treasuryServiceMock, times(1)).deleteByOrganizationIdAndBillCodeAndBillYear(treasuryDTO.getOrganizationId(), treasuryDTO.getBillCode(), treasuryDTO.getBillYear());
    }
}
