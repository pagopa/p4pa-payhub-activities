package it.gov.pagopa.payhub.activities.service.treasury;

import it.gov.pagopa.payhub.activities.connector.classification.TreasuryService;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryErrorDTO;
import it.gov.pagopa.payhub.activities.enums.TreasuryOperationEnum;
import it.gov.pagopa.payhub.activities.exception.TreasuryOpiInvalidFileException;
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

    private TreasuryVersionBaseHandlerService<Object> handlerService;

    @BeforeEach
    void setUp() {
        handlerService = new TreasuryVersionBaseHandlerService<>(mapperServiceMock, validatorServiceMock, treasuryErrorsArchiverServiceMock, treasuryServiceMock) {
            @Override
            protected Object unmarshall(File file) {
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
        Map<TreasuryOperationEnum, List<Treasury>> expectedMap = Map.of();
        List<Treasury> expectedResult = List.of();


        Mockito.when(validatorServiceMock.validatePageSize(unmarshalledObject, 1)).thenReturn(true);
        Mockito.when(validatorServiceMock.validateData(unmarshalledObject, ingestionFlowFileDTO.getFileName())).thenReturn(new ArrayList<>());
        Mockito.when(mapperServiceMock.apply(unmarshalledObject, ingestionFlowFileDTO)).thenReturn(expectedMap);

        // When
        List<Treasury> result = handlerService.handle(file, ingestionFlowFileDTO, 1);

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

        Mockito.when(validatorServiceMock.validatePageSize(unmarshalledObject, 1)).thenReturn(true);

        // When
        List<Treasury> result = handlerService.handle(file, ingestionFlowFileDTO, 1);

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
        Mockito.verify(mapperServiceMock, never()).apply(new Object(), new IngestionFlowFileDTO());
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
        IngestionFlowFileDTO ingestionFlowFileDTO = new IngestionFlowFileDTO();
        ingestionFlowFileDTO.setFileName("testFile");

        // When
        List<Treasury> result = handlerService.handle(file, ingestionFlowFileDTO, 1);

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
        Mockito.verify(mapperServiceMock, never()).apply(any(), any());
    }

    @Test
    void testHandle_whenMapperFails_thenReturnsEmptyMap() {
        // Given
        File file = mock(File.class);
        IngestionFlowFileDTO ingestionFlowFileDTO = new IngestionFlowFileDTO();
        ingestionFlowFileDTO.setFileName("testFile");

        Object unmarshalledObject = new Object();

        Mockito.when(validatorServiceMock.validatePageSize(unmarshalledObject, 1)).thenReturn(false);
        Mockito.when(validatorServiceMock.validateData(unmarshalledObject, ingestionFlowFileDTO.getFileName())).thenReturn(new ArrayList<>());
        Mockito.when(mapperServiceMock.apply(unmarshalledObject, ingestionFlowFileDTO)).thenThrow(new RuntimeException("Mapper failed"));

        // When
        List<Treasury> result = handlerService.handle(file, ingestionFlowFileDTO, 1);

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

        Mockito.when(validatorServiceMock.validatePageSize(unmarshalledObject, 1)).thenReturn(false);

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

        Mockito.when(validatorServiceMock.validatePageSize(unmarshalledObject, 1)).thenReturn(true);
        List<TreasuryErrorDTO> expectedErrors = List.of(new TreasuryErrorDTO("file", "2023", "B123", "ERR01", "Invalid data"));
        Mockito.when(validatorServiceMock.validateData(unmarshalledObject, ingestionFlowFileDTO.getFileName())).thenReturn(expectedErrors);

        // When
        List<TreasuryErrorDTO> result = handlerService.validate(ingestionFlowFileDTO, 1, unmarshalledObject);

        // Then
        Assertions.assertEquals(expectedErrors, result);
    }
}
