package it.gov.pagopa.payhub.activities.service.ingestionflow.debtpositiontype;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionTypeService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontype.DebtPositionTypeErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontype.DebtPositionTypeIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontype.DebtPositionTypeIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.enums.FileErrorCode;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.debtpositiontype.DebtPositionTypeMapper;
import it.gov.pagopa.payhub.activities.service.files.FileExceptionHandlerService;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.debtposition.dto.generated.CollectionModelDebtPositionType;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionType;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionTypeRequestBody;
import it.gov.pagopa.pu.debtposition.dto.generated.PagedModelDebtPositionTypeEmbedded;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationStatus;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker.buildIngestionFlowFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DebtPositionTypeProcessingServiceTest {

    @Mock
    private DebtPositionTypeErrorsArchiverService errorsArchiverServiceMock;
    @Mock
    private Path workingDirectory;
    @Mock
    private DebtPositionTypeMapper mapperMock;
    @Mock
    private DebtPositionTypeService debtPositionTypeServiceMock;
    @Mock
    private OrganizationService organizationServiceMock;

    private DebtPositionTypeProcessingService service;

    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    @BeforeEach
    void setUp() {
        FileExceptionHandlerService fileExceptionHandlerService = new FileExceptionHandlerService();
        service = new DebtPositionTypeProcessingService(1, mapperMock, errorsArchiverServiceMock,
                debtPositionTypeServiceMock, organizationServiceMock, fileExceptionHandlerService);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                mapperMock,
                debtPositionTypeServiceMock,
                errorsArchiverServiceMock,
                organizationServiceMock);
    }

    @Test
    void whenGetSequencingIdThenReturnExpectedValue() {
        // Given
        DebtPositionTypeIngestionFlowFileDTO row = podamFactory.manufacturePojo(DebtPositionTypeIngestionFlowFileDTO.class);

        // When
        String result = service.getSequencingId(row);

        // Then
        assertEquals(row.getDebtPositionTypeCode(), result);
    }

    @Test
    void processDebtPositionTypeWithNoErrors() {
        // Given
        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        DebtPositionTypeIngestionFlowFileDTO dto = mock(DebtPositionTypeIngestionFlowFileDTO.class);
        Organization orgFromService = Organization.builder()
                .brokerId(1L)
                .orgFiscalCode("brokerFC")
                .ipaCode("ipaCode")
                .orgName("orgName")
                .status(OrganizationStatus.ACTIVE)
                .flagNotifyIo(true)
                .flagPaymentNotification(true)
                .flagNotifyOutcomePush(true)
                .pdndEnabled(false)
                .flagTreasury(false)
                .build();
        DebtPositionTypeRequestBody mappedDebtPosType = DebtPositionTypeRequestBody.builder()
                .brokerId(1L)
                .code("CODE")
                .description("DESCRIPTION")
                .orgType("TYPE")
                .macroArea("AREA")
                .serviceType("SERVICE")
                .collectingReason("REASON")
                .taxonomyCode("TAX")
                .flagNotifyIo(false)
                .flagAnonymousFiscalCode(false)
                .flagMandatoryDueDate(false)
                .ioTemplateSubject("SUBJECT")
                .ioTemplateMessage("MESSAGE")
                .build();
        DebtPositionType createdDebtPosType = DebtPositionType.builder()
                .brokerId(mappedDebtPosType.getBrokerId())
                .code(mappedDebtPosType.getCode())
                .description(mappedDebtPosType.getDescription())
                .orgType(mappedDebtPosType.getOrgType())
                .macroArea(mappedDebtPosType.getMacroArea())
                .serviceType(mappedDebtPosType.getServiceType())
                .collectingReason(mappedDebtPosType.getCollectingReason())
                .taxonomyCode(mappedDebtPosType.getTaxonomyCode())
                .flagNotifyIo(mappedDebtPosType.getFlagNotifyIo())
                .flagAnonymousFiscalCode(mappedDebtPosType.getFlagAnonymousFiscalCode())
                .flagMandatoryDueDate(mappedDebtPosType.getFlagMandatoryDueDate())
                .ioTemplateSubject(mappedDebtPosType.getIoTemplateSubject())
                .ioTemplateMessage(mappedDebtPosType.getIoTemplateMessage())
                .build();
        Mockito.when(organizationServiceMock.getOrganizationById(ingestionFlowFile.getOrganizationId()))
                .thenReturn(Optional.of(orgFromService));
        Mockito.when(mapperMock.map(dto, orgFromService.getBrokerId())).thenReturn(mappedDebtPosType);
        Mockito.when(debtPositionTypeServiceMock.createDebtPositionType(mappedDebtPosType)).thenReturn(createdDebtPosType);
        Mockito.when(debtPositionTypeServiceMock.getByMainFields(
                        dto.getDebtPositionTypeCode(),
                        orgFromService.getBrokerId(),
                        dto.getOrgType(),
                        dto.getMacroArea(),
                        dto.getServiceType(),
                        dto.getCollectingReason(),
                        dto.getTaxonomyCode()
                ))
                .thenReturn(CollectionModelDebtPositionType.builder()
                        .embedded(PagedModelDebtPositionTypeEmbedded.builder()
                                .debtPositionTypes(Collections.emptyList())
                                .build())
                        .build());

        // When
        DebtPositionTypeIngestionFlowFileResult result = service.processDebtPositionType(
                Stream.of(dto).iterator(), List.of(),
                ingestionFlowFile, workingDirectory);

        // Then
        Assertions.assertEquals(1L, result.getProcessedRows());
        Assertions.assertEquals(1L, result.getTotalRows());
    }

    @Test
    void givenThrowExceptionWhenProcessDebtPositionTypeThenAddError() throws URISyntaxException {
        // Given
        DebtPositionTypeIngestionFlowFileDTO dto = TestUtils.getPodamFactory().manufacturePojo(DebtPositionTypeIngestionFlowFileDTO.class);
        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        workingDirectory = Path.of(new URI("file:///tmp"));

        DebtPositionTypeRequestBody mappedDebtPosType = mock(DebtPositionTypeRequestBody.class);

        Organization organization = Organization.builder()
                .brokerId(1L)
                .orgFiscalCode(dto.getBrokerCf())
                .ipaCode("ipaCode")
                .orgName("orgName")
                .status(OrganizationStatus.ACTIVE)
                .flagNotifyIo(true)
                .flagPaymentNotification(true)
                .flagNotifyOutcomePush(true)
                .pdndEnabled(false)
                .flagTreasury(false)
                .build();
        Mockito.when(organizationServiceMock.getOrganizationById(ingestionFlowFile.getOrganizationId()))
                .thenReturn(Optional.of(organization));
        Mockito.when(mapperMock.map(dto, 1L)).thenReturn(mappedDebtPosType);

        Mockito.when(debtPositionTypeServiceMock.createDebtPositionType(mappedDebtPosType))
                .thenThrow(new RuntimeException("[DEBT_POSITION_TYPE_ALREADY_EXISTS] debt position type already exists"));

        Mockito.when(debtPositionTypeServiceMock.getByMainFields(
                        dto.getDebtPositionTypeCode(),
                        organization.getBrokerId(),
                        dto.getOrgType(),
                        dto.getMacroArea(),
                        dto.getServiceType(),
                        dto.getCollectingReason(),
                        dto.getTaxonomyCode()
                ))
                .thenReturn(null);

        Mockito.when(errorsArchiverServiceMock.archiveErrorFiles(workingDirectory, ingestionFlowFile))
                .thenReturn("zipFileName.csv");

        // When
        DebtPositionTypeIngestionFlowFileResult result = service.processDebtPositionType(
                Stream.of(dto).iterator(), List.of(new CsvException("DUMMYERROR")),
                ingestionFlowFile,
                workingDirectory
        );

        // Then
        assertEquals(2, result.getTotalRows());
        assertEquals(0, result.getProcessedRows());
        assertEquals("Some rows have failed", result.getErrorDescription());
        assertEquals("zipFileName.csv", result.getDiscardedFileName());
        verify(errorsArchiverServiceMock).writeErrors(same(workingDirectory), same(ingestionFlowFile), eq(List.of(
                new DebtPositionTypeErrorDTO(ingestionFlowFile.getFileName(), null, null, -1L, FileErrorCode.CSV_GENERIC_ERROR.name(), "Errore generico nella lettura del file: DUMMYERROR"),
                new DebtPositionTypeErrorDTO(ingestionFlowFile.getFileName(), dto.getDebtPositionTypeCode(), dto.getBrokerCf(), 2L,
                        FileErrorCode.DEBT_POSITION_TYPE_ALREADY_EXISTS.name(), FileErrorCode.DEBT_POSITION_TYPE_ALREADY_EXISTS.getMessage())
        )));
    }

    @Test
    void processDebtPositionTypeWhenBrokerIdNotFound() {
        // Given
        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        DebtPositionTypeIngestionFlowFileDTO dto = mock(DebtPositionTypeIngestionFlowFileDTO.class);

        Mockito.when(organizationServiceMock.getOrganizationById(ingestionFlowFile.getOrganizationId()))
                .thenReturn(Optional.empty());

        // When
        DebtPositionTypeIngestionFlowFileResult result = service.processDebtPositionType(
                Stream.of(dto).iterator(), List.of(),
                ingestionFlowFile, workingDirectory);

        // Then
        Assertions.assertNull(result.getBrokerId());
        Assertions.assertEquals("Broker not found", result.getErrorDescription());
        Assertions.assertEquals(0, result.getProcessedRows());
    }

    @Test
    void processDebtPositionTypeWhenDebtPositionTypeAlreadyExists() {
        // Given
        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        DebtPositionTypeIngestionFlowFileDTO dto = mock(DebtPositionTypeIngestionFlowFileDTO.class);
        Mockito.when(dto.getBrokerCf()).thenReturn("brokerFC");

        Organization orgFromService = Organization.builder()
                .brokerId(1L)
                .orgFiscalCode("brokerFC")
                .ipaCode("ipaCode")
                .orgName("orgName")
                .status(OrganizationStatus.ACTIVE)
                .flagNotifyIo(true)
                .flagPaymentNotification(true)
                .flagNotifyOutcomePush(true)
                .pdndEnabled(false)
                .flagTreasury(false)
                .build();

        DebtPositionType existingDebtPosType = DebtPositionType.builder()
                .brokerId(1L)
                .code("CODE")
                .description("DESCRIPTION")
                .orgType("TYPE")
                .macroArea("AREA")
                .serviceType("SERVICE")
                .collectingReason("REASON")
                .taxonomyCode("TAX")
                .flagNotifyIo(false)
                .flagAnonymousFiscalCode(false)
                .flagMandatoryDueDate(false)
                .ioTemplateSubject("SUBJECT")
                .ioTemplateMessage("MESSAGE")
                .build();

        CollectionModelDebtPositionType existingCollectionModel = CollectionModelDebtPositionType.builder()
                .embedded(PagedModelDebtPositionTypeEmbedded.builder()
                        .debtPositionTypes(List.of(existingDebtPosType))
                        .build())
                .build();

        Mockito.when(organizationServiceMock.getOrganizationById(ingestionFlowFile.getOrganizationId()))
                .thenReturn(Optional.of(orgFromService));
        Mockito.when(debtPositionTypeServiceMock.getByMainFields(
                dto.getDebtPositionTypeCode(),
                orgFromService.getBrokerId(),
                dto.getOrgType(),
                dto.getMacroArea(),
                dto.getServiceType(),
                dto.getCollectingReason(),
                dto.getTaxonomyCode()
        )).thenReturn(existingCollectionModel);
        Mockito.when(errorsArchiverServiceMock.archiveErrorFiles(workingDirectory, ingestionFlowFile))
                .thenReturn("zipFileName.csv");

        // When
        DebtPositionTypeIngestionFlowFileResult result = service.processDebtPositionType(
                Stream.of(dto).iterator(), List.of(),
                ingestionFlowFile, workingDirectory);

        // Then
        Assertions.assertEquals(0, result.getProcessedRows());
        Assertions.assertEquals(1, result.getTotalRows());
        Assertions.assertEquals("Some rows have failed", result.getErrorDescription());
        Assertions.assertEquals("zipFileName.csv", result.getDiscardedFileName());

        Mockito.verify(errorsArchiverServiceMock).writeErrors(workingDirectory, ingestionFlowFile, List.of(
                DebtPositionTypeErrorDTO.builder()
                        .fileName(ingestionFlowFile.getFileName())
                        .rowNumber(1L)
                        .errorCode(FileErrorCode.DEBT_POSITION_TYPE_ALREADY_EXISTS.name())
                        .errorMessage(FileErrorCode.DEBT_POSITION_TYPE_ALREADY_EXISTS.getMessage())
                        .debtPositionTypeCode(dto.getDebtPositionTypeCode())
                        .brokerCf(dto.getBrokerCf())
                        .build()
        ));
    }
}
