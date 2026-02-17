package it.gov.pagopa.payhub.activities.service.ingestionflow.debtpositiontypeorg;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionTypeOrgService;
import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionTypeService;
import it.gov.pagopa.payhub.activities.connector.debtposition.SpontaneousFormService;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontypeorg.DebtPositionTypeOrgErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontypeorg.DebtPositionTypeOrgIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontypeorg.DebtPositionTypeOrgIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.enums.FileErrorCode;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.debtpositiontypeorg.DebtPositionTypeOrgMapper;
import it.gov.pagopa.payhub.activities.service.files.ErrorArchiverService;
import it.gov.pagopa.payhub.activities.service.files.FileExceptionHandlerService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.BaseIngestionFlowProcessingServiceTest;
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
import tools.jackson.databind.json.JsonMapper;

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
    private SpontaneousFormService spontaneousFormServiceMock;

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
                spontaneousFormServiceMock,
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
                spontaneousFormServiceMock,
                organizationServiceMock
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

        SpontaneousFormStructure spontaneousFormStructure = podamFactory.manufacturePojo(SpontaneousFormStructure.class);
        DebtPositionTypeOrgIngestionFlowFileDTO dto = podamFactory.manufacturePojo(DebtPositionTypeOrgIngestionFlowFileDTO.class);
        dto.setIpaCode(organization.getIpaCode());
        dto.setCode("CODE" + sequencingId);
        dto.setSpontaneousFormCode("SF_CODE" + sequencingId);
        dto.setSpontaneousFormStructure(new JsonMapper().writeValueAsString(spontaneousFormStructure));

        DebtPositionTypeOrgRequestBody mappedDebtPosType = podamFactory.manufacturePojo(DebtPositionTypeOrgRequestBody.class);
        DebtPositionTypeOrg createdDebtPosType = podamFactory.manufacturePojo(DebtPositionTypeOrg.class);

        long spontaneousFormId;
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

            spontaneousFormId = configureSpontaneousFormMocks(dto, rowNumber % 2 == 0);
        } else {
            // Reuse the spontaneousFormId from the first occurrence of this sequencingId
            // The actual value doesn't matter as the mapper mock will be configured with it
            spontaneousFormId = 1L;
        }

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
                configureUnhappyUseCaseJsonParsingException(ingestionFlowFile, ++previousRowNumber)
        );
    }

    private long configureSpontaneousFormMocks(DebtPositionTypeOrgIngestionFlowFileDTO dto, boolean useExistingForm) {
        SpontaneousForm form;

        if (useExistingForm) {
            // Configure existing form scenario
            form = podamFactory.manufacturePojo(SpontaneousForm.class);
            form.setSpontaneousFormId(1L); // Ensure ID is not null
            Mockito.doReturn(form)
                .when(spontaneousFormServiceMock)
                .findByOrganizationIdAndCode(ingestionFlowFile.getOrganizationId(), dto.getSpontaneousFormCode());
        } else {
            // Configure new form creation scenario
            Mockito.doReturn(null)
                .when(spontaneousFormServiceMock)
                .findByOrganizationIdAndCode(ingestionFlowFile.getOrganizationId(), dto.getSpontaneousFormCode());

            form = podamFactory.manufacturePojo(SpontaneousForm.class);
            form.setSpontaneousFormId(2L); // Ensure ID is not null
            Mockito.doReturn(form)
                .when(spontaneousFormServiceMock)
                .createSpontaneousForm(Mockito.any());
        }
        Long spontaneousFormId = form.getSpontaneousFormId();
        Assertions.assertNotNull(spontaneousFormId, "SpontaneousFormId should not be null in this scenario");
        return spontaneousFormId;
    }

    private Pair<DebtPositionTypeOrgIngestionFlowFileDTO, List<DebtPositionTypeOrgErrorDTO>> configureUnhappyUseCaseJsonParsingException(IngestionFlowFile ingestionFlowFile, long rowNumber) {
        DebtPositionTypeOrgIngestionFlowFileDTO dto = podamFactory.manufacturePojo(DebtPositionTypeOrgIngestionFlowFileDTO.class);
        dto.setIpaCode(organization.getIpaCode());
        dto.setSpontaneousFormStructure("INVALID_STRUCTURE");

        // Mock that DebtPositionTypeOrg doesn't exist yet
        Mockito.doReturn(null)
                .when(debtPositionTypeOrgServiceMock)
                .getDebtPositionTypeOrgByOrganizationIdAndCode(ingestionFlowFile.getOrganizationId(), dto.getCode());

        // Mock that DebtPositionType exists (so we can reach the JSON parsing code)
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
