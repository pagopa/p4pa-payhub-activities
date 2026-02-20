package it.gov.pagopa.payhub.activities.service.ingestionflow.debtpositiontype;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionTypeService;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontype.DebtPositionTypeErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontype.DebtPositionTypeIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontype.DebtPositionTypeIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.enums.FileErrorCode;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.debtpositiontype.DebtPositionTypeMapper;
import it.gov.pagopa.payhub.activities.service.files.ErrorArchiverService;
import it.gov.pagopa.payhub.activities.service.files.FileExceptionHandlerService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.BaseIngestionFlowProcessingServiceTest;
import it.gov.pagopa.pu.debtposition.dto.generated.CollectionModelDebtPositionType;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionType;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionTypeRequestBody;
import it.gov.pagopa.pu.debtposition.dto.generated.PagedModelDebtPositionTypeEmbedded;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
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

import java.nio.file.Path;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class DebtPositionTypeProcessingServiceTest extends BaseIngestionFlowProcessingServiceTest<DebtPositionTypeIngestionFlowFileDTO, DebtPositionTypeIngestionFlowFileResult, DebtPositionTypeErrorDTO> {

    @Mock
    private DebtPositionTypeErrorsArchiverService errorsArchiverServiceMock;
    @Mock
    private DebtPositionTypeMapper mapperMock;
    @Mock
    private DebtPositionTypeService debtPositionTypeServiceMock;

    private DebtPositionTypeProcessingService serviceSpy;

    protected DebtPositionTypeProcessingServiceTest() {
        super(true);
    }

    @BeforeEach
    void init() {
        FileExceptionHandlerService fileExceptionHandlerService = new FileExceptionHandlerService();
        serviceSpy = Mockito.spy(new DebtPositionTypeProcessingService(
                MAX_CONCURRENT_PROCESSING_ROWS,
                mapperMock,
                errorsArchiverServiceMock,
                debtPositionTypeServiceMock,
                organizationServiceMock,
                fileExceptionHandlerService
        ));
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                mapperMock,
                errorsArchiverServiceMock,
                debtPositionTypeServiceMock,
                organizationServiceMock);
    }

    @Override
    protected DebtPositionTypeProcessingService getServiceSpy() {
        return serviceSpy;
    }

    @Override
    protected ErrorArchiverService<DebtPositionTypeErrorDTO, DebtPositionTypeIngestionFlowFileResult> getErrorsArchiverServiceMock() {
        return errorsArchiverServiceMock;
    }

    @Override
    protected DebtPositionTypeIngestionFlowFileResult startProcess(Iterator<DebtPositionTypeIngestionFlowFileDTO> rowIterator, List<CsvException> readerExceptions, IngestionFlowFile ingestionFlowFile, Path workingDirectory) {
        return serviceSpy.processDebtPositionType(rowIterator, readerExceptions, ingestionFlowFile, workingDirectory, new DebtPositionTypeIngestionFlowFileResult());
    }

    @Override
    protected DebtPositionTypeIngestionFlowFileDTO buildAndConfigureHappyUseCase(IngestionFlowFile ingestionFlowFile, int sequencingId, boolean sequencingIdAlreadySent, long rowNumber) {
        DebtPositionTypeIngestionFlowFileDTO dto = podamFactory.manufacturePojo(DebtPositionTypeIngestionFlowFileDTO.class);
        dto.setDebtPositionTypeCode("DPTCODE" + sequencingId);

        DebtPositionTypeRequestBody mappedDebtPosType = podamFactory.manufacturePojo(DebtPositionTypeRequestBody.class);
        DebtPositionType createdDebtPosType = podamFactory.manufacturePojo(DebtPositionType.class);

        Mockito.doReturn(mappedDebtPosType)
                .when(mapperMock)
                .map(dto, organization.getBrokerId());
        Mockito.doReturn(createdDebtPosType)
                .when(debtPositionTypeServiceMock)
                .createDebtPositionType(mappedDebtPosType);
        Mockito.doReturn(
                        CollectionModelDebtPositionType.builder()
                                .embedded(PagedModelDebtPositionTypeEmbedded.builder()
                                        .debtPositionTypes(Collections.emptyList())
                                        .build())
                                .build())
                .when(debtPositionTypeServiceMock)
                .getByMainFields(
                        dto.getDebtPositionTypeCode(),
                        organization.getBrokerId(),
                        dto.getOrgType(),
                        dto.getMacroArea(),
                        dto.getServiceType(),
                        dto.getCollectingReason(),
                        dto.getTaxonomyCode()
                );

        return dto;
    }

    @Override
    protected List<Pair<DebtPositionTypeIngestionFlowFileDTO, List<DebtPositionTypeErrorDTO>>> buildAndConfigureUnhappyUseCases(IngestionFlowFile ingestionFlowFile, long previousRowNumber) {
        return List.of(
                configureUnhappyUseCaseAlreadyExists(ingestionFlowFile, ++previousRowNumber)
        );
    }

    private Pair<DebtPositionTypeIngestionFlowFileDTO, List<DebtPositionTypeErrorDTO>> configureUnhappyUseCaseAlreadyExists(IngestionFlowFile ingestionFlowFile, long rowNumber) {
        DebtPositionTypeIngestionFlowFileDTO dto = podamFactory.manufacturePojo(DebtPositionTypeIngestionFlowFileDTO.class);

        DebtPositionType existingDebtPosType = podamFactory.manufacturePojo(DebtPositionType.class);
        CollectionModelDebtPositionType existingCollectionModel = CollectionModelDebtPositionType.builder()
                .embedded(PagedModelDebtPositionTypeEmbedded.builder()
                        .debtPositionTypes(List.of(existingDebtPosType))
                        .build())
                .build();
        Mockito.when(debtPositionTypeServiceMock.getByMainFields(
                dto.getDebtPositionTypeCode(),
                organization.getBrokerId(),
                dto.getOrgType(),
                dto.getMacroArea(),
                dto.getServiceType(),
                dto.getCollectingReason(),
                dto.getTaxonomyCode()
        )).thenReturn(existingCollectionModel);

        List<DebtPositionTypeErrorDTO> expectedErrors = List.of(
                DebtPositionTypeErrorDTO.builder()
                        .fileName(ingestionFlowFile.getFileName())
                        .rowNumber(rowNumber)
                        .errorCode(FileErrorCode.DEBT_POSITION_TYPE_ALREADY_EXISTS.name())
                        .errorMessage(FileErrorCode.DEBT_POSITION_TYPE_ALREADY_EXISTS.getMessage())
                        .debtPositionTypeCode(dto.getDebtPositionTypeCode())
                        .brokerCf(dto.getBrokerCf())
                        .build()
        );

        return Pair.of(dto, expectedErrors);
    }

    @Test
    void givenBrokerOrganizationNotFoundWhenProcessDebtPositionTypeThenSetErrorDescription() {
        // Given
        DebtPositionTypeIngestionFlowFileDTO dto = podamFactory.manufacturePojo(DebtPositionTypeIngestionFlowFileDTO.class);
        DebtPositionTypeIngestionFlowFileResult expectedResult = new DebtPositionTypeIngestionFlowFileResult();

        Mockito.reset(organizationServiceMock);
        Mockito.when(organizationServiceMock.getOrganizationById(ingestionFlowFile.getOrganizationId()))
                .thenReturn(Optional.of(new Organization()));

        // When
        DebtPositionTypeIngestionFlowFileResult result = serviceSpy.processDebtPositionType(
                Stream.of(dto).iterator(), List.of(),
                ingestionFlowFile, workingDirectory,
                expectedResult
                );

        // Then
        Assertions.assertNull(result.getBrokerId());
        Assertions.assertEquals("L'intermediario non e' stato trovato", result.getErrorDescription());
        Assertions.assertEquals(0, result.getProcessedRows());
    }

}
