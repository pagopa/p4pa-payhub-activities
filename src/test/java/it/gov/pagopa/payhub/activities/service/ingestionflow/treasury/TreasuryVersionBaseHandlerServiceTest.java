package it.gov.pagopa.payhub.activities.service.ingestionflow.treasury;

import it.gov.pagopa.payhub.activities.connector.classification.TreasuryService;
import it.gov.pagopa.payhub.activities.dto.ingestion.IngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.TreasuryErrorDTO;
import it.gov.pagopa.payhub.activities.enums.TreasuryOperationEnum;
import it.gov.pagopa.payhub.activities.exception.treasury.TreasuryOpiInvalidFileException;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public abstract class TreasuryVersionBaseHandlerServiceTest<T> {

    @Mock
    protected TreasuryErrorsArchiverService treasuryErrorsArchiverServiceMock;
    @Mock
    protected TreasuryService treasuryServiceMock;
    @Mock
    protected TreasuryUnmarshallerService treasuryUnmarshallerServiceMock;

    private T unmarshalledObject;

    private TreasuryVersionBaseHandlerService<T> handlerService;

    @BeforeEach
    void setUp() {
        unmarshalledObject = mockFlussoGiornaleDiCassa();

        handlerService = buildVersionHandlerService();
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(
                treasuryErrorsArchiverServiceMock,
                treasuryServiceMock,
                treasuryUnmarshallerServiceMock,
                getMapperServiceMock(),
                getValidatorServiceMock()
        );
    }

    protected abstract T mockFlussoGiornaleDiCassa();

    protected abstract TreasuryVersionBaseHandlerService<T> buildVersionHandlerService();
    protected abstract String getExpectedFileVersion();
    protected abstract OngoingStubbing<T> getUnmarshallerMockitOngoingStubbing(File xmlFile);
    protected abstract TreasuryValidatorService<T> getValidatorServiceMock();

    protected abstract TreasuryMapperService<T> getMapperServiceMock();

    @Test
    void testHandle_whenValidFile_thenReturnsResult() {
        // Given
        Path fileFolder = Path.of("build");
        File file = fileFolder.resolve("prova.txt").toFile();
        IngestionFlowFile ingestionFlowFileDTO = new IngestionFlowFile();
        ingestionFlowFileDTO.setFileName("testFile");
        ArrayList<TreasuryErrorDTO> errorDTOS = new ArrayList<>();

        Treasury newTreasury = new Treasury();
        newTreasury.setTreasuryId("TRID");
        Map<TreasuryOperationEnum, List<Treasury>> expectedMap = Map.of(TreasuryOperationEnum.INSERT, List.of(newTreasury));
        Pair<IngestionFlowFileResult, List<Treasury>> expectedResult = Pair.of(
                IngestionFlowFileResult.builder()
                        .fileVersion(getExpectedFileVersion())
                        .totalRows(1)
                        .processedRows(1)
                        .build(),
                List.of(newTreasury));

        getUnmarshallerMockitOngoingStubbing(file).thenReturn(unmarshalledObject);
        Mockito.when(getValidatorServiceMock().validatePageSize(unmarshalledObject, 1)).thenReturn(true);
        Mockito.when(getValidatorServiceMock().validateData(unmarshalledObject, ingestionFlowFileDTO.getFileName())).thenReturn(errorDTOS);
        Mockito.when(getMapperServiceMock().apply(unmarshalledObject, ingestionFlowFileDTO)).thenReturn(expectedMap);

        // When
        Pair<IngestionFlowFileResult, List<Treasury>> result = handlerService.handle(file, ingestionFlowFileDTO, 1);

        // Then
        assertNotNull(result);
        Assertions.assertEquals(expectedResult, result);
        Mockito.verify(treasuryErrorsArchiverServiceMock)
                .writeErrors(Mockito.eq(fileFolder), Mockito.same(ingestionFlowFileDTO), Mockito.same(errorDTOS));
    }

    @Test
    void testHandle_whenValidationFails_thenThrowError() {
        // Given
        Path fileFolder = Path.of("build");
        File file = fileFolder.resolve("prova.txt").toFile();
        IngestionFlowFile ingestionFlowFileDTO = new IngestionFlowFile();
        ingestionFlowFileDTO.setFileName("testFile");

        getUnmarshallerMockitOngoingStubbing(file).thenReturn(unmarshalledObject);
        Mockito.when(getValidatorServiceMock().validatePageSize(unmarshalledObject, 1)).thenReturn(false);

        // When
        Assertions.assertThrows(TreasuryOpiInvalidFileException.class, () -> handlerService.handle(file, ingestionFlowFileDTO, 1));
    }

    @Test
    void testHandle_whenUnmarshallFails_thenReturnsNull() {
        // Given
        File file = mock(File.class);
        IngestionFlowFile ingestionFlowFileDTO = new IngestionFlowFile();
        ingestionFlowFileDTO.setFileName("testFile");

        getUnmarshallerMockitOngoingStubbing(file).thenThrow(new RuntimeException("Unmarshall failed"));

        // When
        Pair<IngestionFlowFileResult, List<Treasury>> result = handlerService.handle(file, ingestionFlowFileDTO, 1);

        // Then
        assertNotNull(result);
        Assertions.assertEquals(new IngestionFlowFileResult(), result.getLeft());
        Assertions.assertNull(result.getRight());
        Mockito.verify(getMapperServiceMock(), never()).apply(any(), any());
    }

    @Test
    void testValidate_whenPageSizeInvalid_thenThrowsException() {
        // Given
        IngestionFlowFile ingestionFlowFileDTO = new IngestionFlowFile();
        ingestionFlowFileDTO.setFileName("testFile");

        Mockito.when(getValidatorServiceMock().validatePageSize(unmarshalledObject, 1)).thenReturn(false);

        // When & Then
        Assertions.assertThrows(TreasuryOpiInvalidFileException.class, () ->
                handlerService.validate(ingestionFlowFileDTO, 1, unmarshalledObject));
    }

    @Test
    void testValidate_whenValidationSucceeds_thenReturnsErrors() {
        // Given
        IngestionFlowFile ingestionFlowFileDTO = new IngestionFlowFile();
        ingestionFlowFileDTO.setFileName("testFile");
        List<TreasuryErrorDTO> expectedErrors = List.of(new TreasuryErrorDTO("file", "2023", "B123", "ERR01", "Invalid data"));

        Mockito.when(getValidatorServiceMock().validatePageSize(unmarshalledObject, 1)).thenReturn(true);
        Mockito.when(getValidatorServiceMock().validateData(unmarshalledObject, ingestionFlowFileDTO.getFileName())).thenReturn(expectedErrors);

        // When
        List<TreasuryErrorDTO> result = handlerService.validate(ingestionFlowFileDTO, 1, unmarshalledObject);

        // Then
        Assertions.assertEquals(expectedErrors, result);
    }


    @Test
    void testHandle_whenDeleteTreasury_thenCallsDeleteByOrganizationIdAndBillCodeAndBillYear() {
        // Given
        Path fileFolder = Path.of("build");
        File file = fileFolder.resolve("prova.txt").toFile();
        IngestionFlowFile ingestionFlowFileDTO = new IngestionFlowFile();
        ingestionFlowFileDTO.setFileName("testFile");
        ArrayList<TreasuryErrorDTO> errorDTOS = new ArrayList<>();

        Treasury treasuryDTO = new Treasury();
        treasuryDTO.setOrganizationId(98765L);
        treasuryDTO.setBillCode("BILL123");
        treasuryDTO.setBillYear("2025");

        Map<TreasuryOperationEnum, List<Treasury>> resultMap = Map.of(
                TreasuryOperationEnum.DELETE, List.of(treasuryDTO)
        );

        getUnmarshallerMockitOngoingStubbing(file).thenReturn(unmarshalledObject);
        when(getValidatorServiceMock().validatePageSize(unmarshalledObject, 1)).thenReturn(true);
        when(getValidatorServiceMock().validateData(unmarshalledObject, ingestionFlowFileDTO.getFileName())).thenReturn(errorDTOS);
        when(getMapperServiceMock().apply(unmarshalledObject, ingestionFlowFileDTO)).thenReturn(resultMap);
        when(treasuryServiceMock.deleteByOrganizationIdAndBillCodeAndBillYear(treasuryDTO.getOrganizationId(), treasuryDTO.getBillCode(), treasuryDTO.getBillYear())).thenReturn(1L);

        // When
        Pair<IngestionFlowFileResult, List<Treasury>> result = handlerService.handle(file, ingestionFlowFileDTO, 1);

        // Then
        assertNotNull(result);
        Assertions.assertEquals(
                IngestionFlowFileResult.builder()
                        .fileVersion(getExpectedFileVersion())
                        .totalRows(1)
                        .processedRows(1)
                        .build(),
                result.getLeft());
        Assertions.assertTrue(result.getRight().isEmpty());
        verify(treasuryServiceMock, times(1)).deleteByOrganizationIdAndBillCodeAndBillYear(treasuryDTO.getOrganizationId(), treasuryDTO.getBillCode(), treasuryDTO.getBillYear());
        Mockito.verify(treasuryErrorsArchiverServiceMock)
                .writeErrors(Mockito.eq(fileFolder), Mockito.same(ingestionFlowFileDTO), Mockito.same(errorDTOS));
    }

    @Test
    void testHandle_whenDeleteTreasury_thenCallsDeleteByOrganizationIdAndBillCodeAndBillYearAndWriteError() {
        // Given
        Path fileFolder = Path.of("build");
        File file = fileFolder.resolve("prova.txt").toFile();
        IngestionFlowFile ingestionFlowFileDTO = new IngestionFlowFile();
        ingestionFlowFileDTO.setFileName("testFile");
        ArrayList<TreasuryErrorDTO> errorDTOS = new ArrayList<>();

        Treasury treasuryDTO = new Treasury();
        treasuryDTO.setOrganizationId(98765L);
        treasuryDTO.setBillCode("BILL123");
        treasuryDTO.setBillYear("2025");

        Map<TreasuryOperationEnum, List<Treasury>> resultMap = Map.of(
                TreasuryOperationEnum.DELETE, List.of(treasuryDTO)
        );

        getUnmarshallerMockitOngoingStubbing(file).thenReturn(unmarshalledObject);
        when(getValidatorServiceMock().validatePageSize(unmarshalledObject, 1)).thenReturn(true);
        when(getValidatorServiceMock().validateData(unmarshalledObject, ingestionFlowFileDTO.getFileName())).thenReturn(errorDTOS);
        when(getMapperServiceMock().apply(unmarshalledObject, ingestionFlowFileDTO)).thenReturn(resultMap);
        when(treasuryServiceMock.deleteByOrganizationIdAndBillCodeAndBillYear(treasuryDTO.getOrganizationId(), treasuryDTO.getBillCode(), treasuryDTO.getBillYear())).thenReturn(0L);

        // When
        Pair<IngestionFlowFileResult, List<Treasury>> result = handlerService.handle(file, ingestionFlowFileDTO, 1);

        // Then
        assertNotNull(result);
        Assertions.assertEquals(
                IngestionFlowFileResult.builder()
                        .fileVersion(getExpectedFileVersion())
                        .totalRows(1)
                        .processedRows(1)
                        .build(),
                result.getLeft());
        Assertions.assertTrue(result.getRight().isEmpty());
        verify(treasuryServiceMock, times(1)).deleteByOrganizationIdAndBillCodeAndBillYear(treasuryDTO.getOrganizationId(), treasuryDTO.getBillCode(), treasuryDTO.getBillYear());

        Mockito.verify(treasuryErrorsArchiverServiceMock)
                .writeErrors(Mockito.eq(fileFolder), Mockito.same(ingestionFlowFileDTO), Mockito.same(errorDTOS));
    }
}
