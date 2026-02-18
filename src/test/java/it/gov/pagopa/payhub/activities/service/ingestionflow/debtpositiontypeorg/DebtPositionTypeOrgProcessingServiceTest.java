package it.gov.pagopa.payhub.activities.service.ingestionflow.debtpositiontypeorg;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionTypeOrgService;
import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionTypeService;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontypeorg.DebtPositionTypeOrgErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontypeorg.DebtPositionTypeOrgIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontypeorg.DebtPositionTypeOrgIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.enums.FileErrorCode;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.debtpositiontypeorg.DebtPositionTypeOrgMapper;
import it.gov.pagopa.payhub.activities.service.files.ErrorArchiverService;
import it.gov.pagopa.payhub.activities.service.files.FileExceptionHandlerService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.BaseIngestionFlowProcessingServiceTest;
import it.gov.pagopa.payhub.activities.service.ingestionflow.spontaneousform.SpontaneousFormHandlerService;
import it.gov.pagopa.pu.debtposition.dto.generated.*;
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
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class DebtPositionTypeOrgProcessingServiceTest extends BaseIngestionFlowProcessingServiceTest<DebtPositionTypeOrgIngestionFlowFileDTO, DebtPositionTypeOrgIngestionFlowFileResult, DebtPositionTypeOrgErrorDTO> {

    @Mock
    private DebtPositionTypeOrgErrorsArchiverService errorsArchiverServiceMock;
    @Mock
    private DebtPositionTypeOrgMapper mapperMock;
    @Mock
    private DebtPositionTypeOrgService debtPositionTypeOrgServiceMock;
    @Mock
    private DebtPositionTypeService debtPositionTypeServiceMock;
    @Mock
    private SpontaneousFormHandlerService spontaneousFormHandlerServiceMock;

    private DebtPositionTypeOrgProcessingService serviceSpy;

    protected DebtPositionTypeOrgProcessingServiceTest() {
        super(true);
    }

    @BeforeEach
    void init() {
        FileExceptionHandlerService fileExceptionHandlerService = new FileExceptionHandlerService();
        serviceSpy = Mockito.spy(new DebtPositionTypeOrgProcessingService(
                MAX_CONCURRENT_PROCESSING_ROWS,
                mapperMock,
                errorsArchiverServiceMock,
                debtPositionTypeOrgServiceMock,
                debtPositionTypeServiceMock,
                organizationServiceMock,
                spontaneousFormHandlerServiceMock,
                fileExceptionHandlerService
        ));
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                mapperMock,
                errorsArchiverServiceMock,
                debtPositionTypeOrgServiceMock,
                debtPositionTypeServiceMock,
                organizationServiceMock,
                spontaneousFormHandlerServiceMock
        );
    }

    @Override
    protected DebtPositionTypeOrgProcessingService getServiceSpy() {
        return serviceSpy;
    }

    @Override
    protected ErrorArchiverService<DebtPositionTypeOrgErrorDTO> getErrorsArchiverServiceMock() {
        return errorsArchiverServiceMock;
    }

    @Override
    protected DebtPositionTypeOrgIngestionFlowFileResult startProcess(Iterator<DebtPositionTypeOrgIngestionFlowFileDTO> rowIterator, List<CsvException> readerExceptions, IngestionFlowFile ingestionFlowFile, Path workingDirectory) {
        return serviceSpy.processDebtPositionTypeOrg(rowIterator, readerExceptions, ingestionFlowFile, workingDirectory);
    }

    @Override
    protected DebtPositionTypeOrgIngestionFlowFileDTO buildAndConfigureHappyUseCase(IngestionFlowFile ingestionFlowFile, int sequencingId, boolean sequencingIdAlreadySent, long rowNumber) {
        long dpTypeId = 10L * sequencingId;
        Long spontaneousFormId = 100L * sequencingId;

        DebtPositionTypeOrgIngestionFlowFileDTO dto = podamFactory.manufacturePojo(DebtPositionTypeOrgIngestionFlowFileDTO.class);
        dto.setIpaCode(organization.getIpaCode());
        dto.setCode("CODE" + sequencingId);

        DebtPositionTypeOrgRequestBody mappedDebtPosType = podamFactory.manufacturePojo(DebtPositionTypeOrgRequestBody.class);
        DebtPositionTypeOrg createdDebtPosType = podamFactory.manufacturePojo(DebtPositionTypeOrg.class);

        if (!sequencingIdAlreadySent) {
            DebtPositionType dpType = podamFactory.manufacturePojo(DebtPositionType.class);
            dpType.setDebtPositionTypeId(dpTypeId);

            CollectionModelDebtPositionType existingCollectionModel = CollectionModelDebtPositionType.builder()
                    .embedded(PagedModelDebtPositionTypeEmbedded.builder()
                            .debtPositionTypes(List.of(dpType))
                            .build())
                    .build();
            Mockito.doReturn(existingCollectionModel)
                    .when(debtPositionTypeServiceMock)
                    .getByBrokerIdAndCode(organization.getBrokerId(), dto.getCode());

            Mockito.doReturn(null)
                    .when(debtPositionTypeOrgServiceMock)
                    .getDebtPositionTypeOrgByOrganizationIdAndCode(ingestionFlowFile.getOrganizationId(), dto.getCode());
        }

        Mockito.doReturn(spontaneousFormId)
                .when(spontaneousFormHandlerServiceMock)
                .handleSpontaneousForm(ingestionFlowFile.getOrganizationId(), dto);

        Mockito.doReturn(mappedDebtPosType)
                .when(mapperMock)
                .map(dto, dpTypeId, ingestionFlowFile.getOrganizationId(), spontaneousFormId);
        Mockito.doReturn(createdDebtPosType)
                .when(debtPositionTypeOrgServiceMock)
                .createDebtPositionTypeOrg(mappedDebtPosType);

        return dto;
    }

    @Override
    protected List<Pair<DebtPositionTypeOrgIngestionFlowFileDTO, List<DebtPositionTypeOrgErrorDTO>>> buildAndConfigureUnhappyUseCases(IngestionFlowFile ingestionFlowFile, long previousRowNumber) {
        return List.of(
                configureUnhappyUseCaseIpaMissmatch(ingestionFlowFile, ++previousRowNumber),
                configureUnhappyUseCaseAlreadyExists(ingestionFlowFile, ++previousRowNumber),
                configureUnhappyUseCaseDPTypeNotFound(ingestionFlowFile, ++previousRowNumber),
                configureUnhappyUseCaseSpontaneousFormParsingError(ingestionFlowFile, ++previousRowNumber)
        );
    }

    private Pair<DebtPositionTypeOrgIngestionFlowFileDTO, List<DebtPositionTypeOrgErrorDTO>> configureUnhappyUseCaseAlreadyExists(IngestionFlowFile ingestionFlowFile, long rowNumber) {
        DebtPositionTypeOrgIngestionFlowFileDTO dto = podamFactory.manufacturePojo(DebtPositionTypeOrgIngestionFlowFileDTO.class);
        dto.setIpaCode(organization.getIpaCode());

        DebtPositionTypeOrg alreadyExistingDPTypeOrg = podamFactory.manufacturePojo(DebtPositionTypeOrg.class);
        Mockito.doReturn(alreadyExistingDPTypeOrg)
                .when(debtPositionTypeOrgServiceMock)
                .getDebtPositionTypeOrgByOrganizationIdAndCode(ingestionFlowFile.getOrganizationId(), dto.getCode());

        List<DebtPositionTypeOrgErrorDTO> expectedErrors = List.of(
                DebtPositionTypeOrgErrorDTO.builder()
                        .fileName(ingestionFlowFile.getFileName())
                        .rowNumber(rowNumber)
                        .errorCode(FileErrorCode.DEBT_POSITION_TYPE_ORG_ALREADY_EXISTS.name())
                        .errorMessage(FileErrorCode.DEBT_POSITION_TYPE_ORG_ALREADY_EXISTS.getMessage())
                        .debtPositionTypeCode(dto.getCode())
                        .organizationId(ingestionFlowFile.getOrganizationId())
                        .build()
        );

        return Pair.of(dto, expectedErrors);
    }

    private Pair<DebtPositionTypeOrgIngestionFlowFileDTO, List<DebtPositionTypeOrgErrorDTO>> configureUnhappyUseCaseDPTypeNotFound(IngestionFlowFile ingestionFlowFile, long rowNumber) {
        DebtPositionTypeOrgIngestionFlowFileDTO dto = podamFactory.manufacturePojo(DebtPositionTypeOrgIngestionFlowFileDTO.class);
        dto.setIpaCode(organization.getIpaCode());

        Mockito.doReturn(null)
                .when(debtPositionTypeOrgServiceMock)
                .getDebtPositionTypeOrgByOrganizationIdAndCode(ingestionFlowFile.getOrganizationId(), dto.getCode());

        Mockito.doReturn(CollectionModelDebtPositionType.builder().embedded(new PagedModelDebtPositionTypeEmbedded()).build())
                .when(debtPositionTypeServiceMock)
                .getByBrokerIdAndCode(organization.getBrokerId(), dto.getCode());

        List<DebtPositionTypeOrgErrorDTO> expectedErrors = List.of(
                DebtPositionTypeOrgErrorDTO.builder()
                        .fileName(ingestionFlowFile.getFileName())
                        .rowNumber(rowNumber)
                        .errorCode(FileErrorCode.DEBT_POSITION_TYPE_BY_CODE_NOT_FOUND.name())
                        .errorMessage(FileErrorCode.DEBT_POSITION_TYPE_BY_CODE_NOT_FOUND.getMessage().formatted(dto.getCode()))
                        .debtPositionTypeCode(dto.getCode())
                        .organizationId(ingestionFlowFile.getOrganizationId())
                        .build()
        );

        return Pair.of(dto, expectedErrors);
    }

    private Pair<DebtPositionTypeOrgIngestionFlowFileDTO, List<DebtPositionTypeOrgErrorDTO>> configureUnhappyUseCaseIpaMissmatch(IngestionFlowFile ingestionFlowFile, long rowNumber) {
        DebtPositionTypeOrgIngestionFlowFileDTO dto = podamFactory.manufacturePojo(DebtPositionTypeOrgIngestionFlowFileDTO.class);
        dto.setIpaCode("WRONGIPACODE");

        List<DebtPositionTypeOrgErrorDTO> expectedErrors = List.of(
                DebtPositionTypeOrgErrorDTO.builder()
                        .fileName(ingestionFlowFile.getFileName())
                        .rowNumber(rowNumber)
                        .errorCode(FileErrorCode.ORGANIZATION_IPA_MISMATCH.name())
                        .errorMessage(FileErrorCode.ORGANIZATION_IPA_MISMATCH.getMessage().formatted(dto.getIpaCode(), organization.getIpaCode()))
                        .debtPositionTypeCode(dto.getCode())
                        .organizationId(ingestionFlowFile.getOrganizationId())
                        .build()
        );

        return Pair.of(dto, expectedErrors);
    }

    private Pair<DebtPositionTypeOrgIngestionFlowFileDTO, List<DebtPositionTypeOrgErrorDTO>> configureUnhappyUseCaseSpontaneousFormParsingError(IngestionFlowFile ingestionFlowFile, long rowNumber) {
        DebtPositionTypeOrgIngestionFlowFileDTO dto = podamFactory.manufacturePojo(DebtPositionTypeOrgIngestionFlowFileDTO.class);
        dto.setIpaCode(organization.getIpaCode());

        Mockito.doReturn(null)
                .when(debtPositionTypeOrgServiceMock)
                .getDebtPositionTypeOrgByOrganizationIdAndCode(ingestionFlowFile.getOrganizationId(), dto.getCode());

        DebtPositionType dpType = podamFactory.manufacturePojo(DebtPositionType.class);
        dpType.setDebtPositionTypeId(999L);

        CollectionModelDebtPositionType existingCollectionModel = CollectionModelDebtPositionType.builder()
                .embedded(PagedModelDebtPositionTypeEmbedded.builder()
                        .debtPositionTypes(List.of(dpType))
                        .build())
                .build();
        Mockito.doReturn(existingCollectionModel)
                .when(debtPositionTypeServiceMock)
                .getByBrokerIdAndCode(organization.getBrokerId(), dto.getCode());

        Mockito.doThrow(new RuntimeException("Error parsing JSON for spontaneous form"))
                .when(spontaneousFormHandlerServiceMock)
                .handleSpontaneousForm(ingestionFlowFile.getOrganizationId(), dto);

        List<DebtPositionTypeOrgErrorDTO> expectedErrors = List.of(
                DebtPositionTypeOrgErrorDTO.builder()
                        .fileName(ingestionFlowFile.getFileName())
                        .rowNumber(rowNumber)
                        .errorCode(FileErrorCode.SPONTANEOUS_FORM_PARSING_ERROR.name())
                        .errorMessage(FileErrorCode.SPONTANEOUS_FORM_PARSING_ERROR.getMessage().formatted(dto.getSpontaneousFormCode()))
                        .debtPositionTypeCode(dto.getCode())
                        .organizationId(ingestionFlowFile.getOrganizationId())
                        .build()
        );

        return Pair.of(dto, expectedErrors);
    }

    @Test
    void givenBrokerOrganizationNotFoundWhenProcessDebtPositionTypeOrgThenSetErrorDescription() {
        // Given
        DebtPositionTypeOrgIngestionFlowFileDTO dto = mock(DebtPositionTypeOrgIngestionFlowFileDTO.class);

        Mockito.reset(organizationServiceMock);
        Mockito.when(organizationServiceMock.getOrganizationById(ingestionFlowFile.getOrganizationId()))
                .thenReturn(Optional.of(new Organization()));

        // When
        DebtPositionTypeOrgIngestionFlowFileResult result = serviceSpy.processDebtPositionTypeOrg(
                Stream.of(dto).iterator(), List.of(),
                ingestionFlowFile, workingDirectory);

        // Then
        Assertions.assertNull(result.getBrokerId());
        Assertions.assertEquals("L'intermediario non e' stato trovato", result.getErrorDescription());
        Assertions.assertEquals(0, result.getProcessedRows());
    }

}
