package it.gov.pagopa.payhub.activities.service.ingestionflow.debtpositiontypeorg;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionTypeOrgService;
import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionTypeService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontypeorg.DebtPositionTypeOrgErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontypeorg.DebtPositionTypeOrgIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontypeorg.DebtPositionTypeOrgIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.debtpositiontypeorg.DebtPositionTypeOrgMapper;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.debtposition.dto.generated.*;
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

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
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
class DebtPositionTypeOrgProcessingServiceTest {

    @Mock
    private DebtPositionTypeOrgErrorsArchiverService errorsArchiverServiceMock;

    @Mock
    private Path workingDirectory;

    @Mock
    private DebtPositionTypeOrgMapper mapperMock;

    @Mock
    private DebtPositionTypeOrgService debtPositionTypeOrgServiceMock;

    @Mock
    private DebtPositionTypeService debtPositionTypeServiceMock;

    @Mock
    private OrganizationService organizationServiceMock;
    @Mock
    private OrganizationService organizationServiceSuperMock;

    @Mock
    private DebtPositionTypeOrgProcessingService service;

    @BeforeEach
    void setUp() {
        service = new DebtPositionTypeOrgProcessingService(
                mapperMock,
                errorsArchiverServiceMock,
                debtPositionTypeOrgServiceMock,
                debtPositionTypeServiceMock,
                organizationServiceMock,
                organizationServiceSuperMock
        );
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                mapperMock,
                errorsArchiverServiceMock,
                debtPositionTypeOrgServiceMock,
                debtPositionTypeServiceMock,
                organizationServiceMock,
                organizationServiceSuperMock
                );
    }


    @Test
    void processDebtPositionTypeOrgWithNoErrors() {
        // Given
        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        DebtPositionTypeOrgIngestionFlowFileDTO dto = mock(DebtPositionTypeOrgIngestionFlowFileDTO.class);
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
                .build();
        DebtPositionTypeOrgRequestBody mappedDebtPosType = DebtPositionTypeOrgRequestBody.builder()
                .code("CODE")
                .description("DESCRIPTION")
                .organizationId(2L)
                .debtPositionTypeId(1L)
                .flagNotifyIo(false)
                .flagAnonymousFiscalCode(false)
                .flagMandatoryDueDate(false)
                .ioTemplateSubject("SUBJECT")
                .ioTemplateMessage("MESSAGE")
                .build();

        DebtPositionTypeOrg createdDebtPosType = DebtPositionTypeOrg.builder()
                .code(mappedDebtPosType.getCode())
                .debtPositionTypeId(mappedDebtPosType.getDebtPositionTypeId())
                .organizationId(ingestionFlowFile.getOrganizationId())
                .description(mappedDebtPosType.getDescription())
                .flagNotifyIo(mappedDebtPosType.getFlagNotifyIo())
                .flagAnonymousFiscalCode(mappedDebtPosType.getFlagAnonymousFiscalCode())
                .flagMandatoryDueDate(mappedDebtPosType.getFlagMandatoryDueDate())
                .ioTemplateSubject(mappedDebtPosType.getIoTemplateSubject())
                .ioTemplateMessage(mappedDebtPosType.getIoTemplateMessage())
                .build();
        CollectionModelDebtPositionType existingCollectionModel = mock(CollectionModelDebtPositionType.class);

        PagedModelDebtPositionTypeEmbedded embedded = mock(PagedModelDebtPositionTypeEmbedded.class);
        List<DebtPositionType> debtPositionTypeList = List.of(mock(DebtPositionType.class));
        Mockito.when(existingCollectionModel.getEmbedded()).thenReturn(embedded);
        Mockito.when(embedded.getDebtPositionTypes()).thenReturn(debtPositionTypeList);

        Mockito.when(organizationServiceMock.getOrganizationById(ingestionFlowFile.getOrganizationId()))
                .thenReturn(Optional.of(orgFromService));
        Mockito.when(debtPositionTypeServiceMock.getByBrokerIdAndCode(orgFromService.getBrokerId(), dto.getCode()))
                .thenReturn(existingCollectionModel);
        Mockito.when(mapperMock.map(Mockito.eq(dto), Mockito.anyLong(), Mockito.anyLong())).thenReturn(mappedDebtPosType);
        Mockito.when(debtPositionTypeOrgServiceMock.createDebtPositionTypeOrg(mappedDebtPosType)).thenReturn(createdDebtPosType);

        // When
        DebtPositionTypeOrgIngestionFlowFileResult result = service.processDebtPositionTypeOrg(
                Stream.of(dto).iterator(), List.of(),
                ingestionFlowFile, workingDirectory);

        // Then
        Assertions.assertEquals(1L, result.getProcessedRows());
        Assertions.assertEquals(1L, result.getTotalRows());
        Mockito.verify(organizationServiceMock).getOrganizationById(ingestionFlowFile.getOrganizationId());
        Mockito.verify(mapperMock).map(Mockito.eq(dto), Mockito.anyLong(), Mockito.anyLong());
        Mockito.verify(debtPositionTypeOrgServiceMock).createDebtPositionTypeOrg(mappedDebtPosType);
        Mockito.verify(debtPositionTypeOrgServiceMock).getDebtPositionTypeOrgByOrganizationIdAndCode(Mockito.anyLong(), Mockito.any());
        Mockito.verify(debtPositionTypeServiceMock, Mockito.atLeastOnce()).getByBrokerIdAndCode(Mockito.anyLong(), Mockito.any());
    }

    @Test
    void givenThrowExceptionWhenProcessDebtPositionTypeOrgThenAddError() throws URISyntaxException {
        // Given
        DebtPositionTypeOrgIngestionFlowFileDTO debtPositionTypeOrgIngestionFlowFileDTO = TestUtils.getPodamFactory().manufacturePojo(DebtPositionTypeOrgIngestionFlowFileDTO.class);
        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        workingDirectory = Path.of(new URI("file:///tmp"));

        DebtPositionTypeOrgRequestBody mappedDebtPosTypeOrg = mock(DebtPositionTypeOrgRequestBody.class);

        Organization organization = Organization.builder()
                .brokerId(1L)
                .orgFiscalCode("brokerFC")
                .ipaCode("ipaCode")
                .orgName("orgName")
                .status(OrganizationStatus.ACTIVE)
                .flagNotifyIo(true)
                .flagPaymentNotification(true)
                .flagNotifyOutcomePush(true)
                .pdndEnabled(false)
                .build();
        Mockito.when(organizationServiceMock.getOrganizationById(ingestionFlowFile.getOrganizationId()))
                .thenReturn(Optional.of(organization));
        Mockito.when(mapperMock.map(Mockito.any(), Mockito.anyLong(), Mockito.anyLong())).thenReturn(mappedDebtPosTypeOrg);

        CollectionModelDebtPositionType collectionModel = mock(CollectionModelDebtPositionType.class);
        PagedModelDebtPositionTypeEmbedded embedded = mock(PagedModelDebtPositionTypeEmbedded.class);
        DebtPositionType debtPositionType = mock(DebtPositionType.class);
        Mockito.when(collectionModel.getEmbedded()).thenReturn(embedded);
        Mockito.when(embedded.getDebtPositionTypes()).thenReturn(List.of(debtPositionType));
        Mockito.when(debtPositionTypeServiceMock.getByBrokerIdAndCode(Mockito.anyLong(), Mockito.any()))
                .thenReturn(collectionModel);

        Mockito.when(debtPositionTypeOrgServiceMock.createDebtPositionTypeOrg(mappedDebtPosTypeOrg))
                .thenThrow(new RuntimeException("Processing error"));

        Mockito.when(errorsArchiverServiceMock.archiveErrorFiles(workingDirectory, ingestionFlowFile))
                .thenReturn("zipFileName.csv");

        // When
        DebtPositionTypeOrgIngestionFlowFileResult result = service.processDebtPositionTypeOrg(
                Stream.of(debtPositionTypeOrgIngestionFlowFileDTO).iterator(), List.of(new CsvException("DUMMYERROR")),
                ingestionFlowFile,
                workingDirectory
        );

        // Then
        assertEquals(2, result.getTotalRows());
        assertEquals(0, result.getProcessedRows());
        assertEquals("Some rows have failed", result.getErrorDescription());
        assertEquals("zipFileName.csv", result.getDiscardedFileName());
        verify(errorsArchiverServiceMock).writeErrors(same(workingDirectory), same(ingestionFlowFile), eq(List.of(
                new DebtPositionTypeOrgErrorDTO(ingestionFlowFile.getFileName(),null, null, -1L, "READER_EXCEPTION", "DUMMYERROR"),
                new DebtPositionTypeOrgErrorDTO(ingestionFlowFile.getFileName(), debtPositionTypeOrgIngestionFlowFileDTO.getCode(),1L, 2L, "PROCESS_EXCEPTION", "Processing error")
        )));
        Mockito.verify(organizationServiceMock).getOrganizationById(ingestionFlowFile.getOrganizationId());
        Mockito.verify(mapperMock).map(Mockito.any(), Mockito.anyLong(), Mockito.anyLong());
        Mockito.verify(debtPositionTypeOrgServiceMock).createDebtPositionTypeOrg(mappedDebtPosTypeOrg);
        Mockito.verify(errorsArchiverServiceMock).archiveErrorFiles(workingDirectory, ingestionFlowFile);
        Mockito.verify(debtPositionTypeOrgServiceMock).getDebtPositionTypeOrgByOrganizationIdAndCode(Mockito.anyLong(), Mockito.any());
    }

    @Test
    void processDebtPositionTypeOrgWhenBrokerIdNotFound() {
        // Given
        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        DebtPositionTypeOrgIngestionFlowFileDTO dto = mock(DebtPositionTypeOrgIngestionFlowFileDTO.class);

        Mockito.when(organizationServiceMock.getOrganizationById(ingestionFlowFile.getOrganizationId()))
                .thenReturn(Optional.empty());

        // When
        DebtPositionTypeOrgIngestionFlowFileResult result = service.processDebtPositionTypeOrg(
                Stream.of(dto).iterator(), List.of(),
                ingestionFlowFile, workingDirectory);

        // Then
        Assertions.assertNull(result.getBrokerId());
        Assertions.assertEquals("Broker not found", result.getErrorDescription());
        Assertions.assertEquals(0, result.getProcessedRows());
        Mockito.verify(organizationServiceMock).getOrganizationById(ingestionFlowFile.getOrganizationId());
        Mockito.verifyNoInteractions(mapperMock);
        Mockito.verifyNoInteractions(errorsArchiverServiceMock);
        Mockito.verifyNoInteractions(debtPositionTypeOrgServiceMock);
    }

    @Test
    void processDebtPositionTypeOrgWhenDebtPositionTypeOrgAlreadyExists() {
        // Given
        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        DebtPositionTypeOrgIngestionFlowFileDTO dto = mock(DebtPositionTypeOrgIngestionFlowFileDTO.class);

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
                .build();

        DebtPositionTypeOrg existingDebtPosType = DebtPositionTypeOrg.builder()
                .code("CODE")
                .description("DESCRIPTION")
                .debtPositionTypeId(1L)
                .organizationId(2L)
                .flagNotifyIo(false)
                .flagAnonymousFiscalCode(false)
                .flagMandatoryDueDate(false)
                .ioTemplateSubject("SUBJECT")
                .ioTemplateMessage("MESSAGE")
                .build();


        Mockito.when(organizationServiceMock.getOrganizationById(ingestionFlowFile.getOrganizationId()))
                .thenReturn(Optional.of(orgFromService));
        Mockito.when(debtPositionTypeOrgServiceMock.getDebtPositionTypeOrgByOrganizationIdAndCode(
                Mockito.anyLong(), Mockito.any()
        )).thenReturn(existingDebtPosType);
        Mockito.when(errorsArchiverServiceMock.archiveErrorFiles(workingDirectory, ingestionFlowFile))
                .thenReturn("zipFileName.csv");

        // When
        DebtPositionTypeOrgIngestionFlowFileResult result = service.processDebtPositionTypeOrg(
                Stream.of(dto).iterator(), List.of(),
                ingestionFlowFile, workingDirectory);

        // Then
        Assertions.assertEquals(0, result.getProcessedRows());
        Assertions.assertEquals(1, result.getTotalRows());
        Assertions.assertEquals("Some rows have failed", result.getErrorDescription());
        Assertions.assertEquals("zipFileName.csv", result.getDiscardedFileName());
        Mockito.verify(organizationServiceMock).getOrganizationById(ingestionFlowFile.getOrganizationId());
        Mockito.verify(errorsArchiverServiceMock).archiveErrorFiles(workingDirectory, ingestionFlowFile);
        Mockito.verify(errorsArchiverServiceMock).writeErrors(
            same(workingDirectory),
            same(ingestionFlowFile),
            Mockito.anyList()
        );
        Mockito.verify(debtPositionTypeOrgServiceMock).getDebtPositionTypeOrgByOrganizationIdAndCode(
                Mockito.anyLong(), Mockito.any()
        );
        Mockito.verifyNoInteractions(mapperMock);
    }

}
