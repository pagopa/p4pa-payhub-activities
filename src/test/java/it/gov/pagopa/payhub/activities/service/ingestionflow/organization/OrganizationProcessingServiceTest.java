package it.gov.pagopa.payhub.activities.service.ingestionflow.organization;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.dto.ingestion.organization.OrganizationErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.organization.OrganizationIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.organization.OrganizationIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.enums.FileErrorCode;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.organization.OrganizationMapper;
import it.gov.pagopa.payhub.activities.service.files.ErrorArchiverService;
import it.gov.pagopa.payhub.activities.service.files.FileExceptionHandlerService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.BaseIngestionFlowProcessingServiceTest;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationCreateDTO;
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

@ExtendWith(MockitoExtension.class)
class OrganizationProcessingServiceTest extends BaseIngestionFlowProcessingServiceTest<OrganizationIngestionFlowFileDTO, OrganizationIngestionFlowFileResult, OrganizationErrorDTO> {


    @Mock
    private OrganizationErrorsArchiverService errorsArchiverServiceMock;
    @Mock
    private OrganizationMapper mapperMock;

    private OrganizationProcessingService serviceSpy;

    protected OrganizationProcessingServiceTest() {
        super(true);
    }

    @BeforeEach
    void init() {
        FileExceptionHandlerService fileExceptionHandlerService = new FileExceptionHandlerService();
        serviceSpy = Mockito.spy(new OrganizationProcessingService(
                MAX_CONCURRENT_PROCESSING_ROWS,
                mapperMock,
                errorsArchiverServiceMock,
                organizationServiceMock,
                fileExceptionHandlerService
        ));
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                mapperMock,
                errorsArchiverServiceMock,
                organizationServiceMock
        );
    }

    @Override
    protected OrganizationProcessingService getServiceSpy() {
        return serviceSpy;
    }

    @Override
    protected ErrorArchiverService<OrganizationErrorDTO> getErrorsArchiverServiceMock() {
        return errorsArchiverServiceMock;
    }

    @Override
    protected OrganizationIngestionFlowFileResult startProcess(Iterator<OrganizationIngestionFlowFileDTO> rowIterator, List<CsvException> readerExceptions, IngestionFlowFile ingestionFlowFile, Path workingDirectory) {
        return serviceSpy.processOrganization(rowIterator, readerExceptions, ingestionFlowFile, workingDirectory);
    }

    @Override
    protected OrganizationIngestionFlowFileDTO buildAndConfigureHappyUseCase(IngestionFlowFile ingestionFlowFile, int sequencingId, boolean sequencingIdAlreadySent, long rowNumber) {
        OrganizationIngestionFlowFileDTO dto = podamFactory.manufacturePojo(OrganizationIngestionFlowFileDTO.class);
        dto.setBrokerCf(organization.getOrgFiscalCode());
        dto.setOrgFiscalCode("ORGFISCALCODE" + sequencingId);

        OrganizationCreateDTO mappedOrg = podamFactory.manufacturePojo(OrganizationCreateDTO.class);
        Organization createdOrg = podamFactory.manufacturePojo(Organization.class);

        if(!sequencingIdAlreadySent) {
            Mockito.doReturn(Optional.empty())
                    .when(organizationServiceMock)
                    .getOrganizationByFiscalCode(dto.getOrgFiscalCode());
        }

        Mockito.doReturn(mappedOrg)
                .when(mapperMock)
                .map(dto, organization.getBrokerId());
        Mockito.doReturn(createdOrg)
                .when(organizationServiceMock)
                .createOrganization(mappedOrg);

        return dto;
    }

    @Override
    protected List<Pair<OrganizationIngestionFlowFileDTO, List<OrganizationErrorDTO>>> buildAndConfigureUnhappyUseCases(IngestionFlowFile ingestionFlowFile, long previousRowNumber) {
        return List.of(
                configureUnhappyUseCaseBrokerMissmatch(ingestionFlowFile, ++previousRowNumber),
                configureUnhappyUseCaseAlreadyExists(ingestionFlowFile, ++previousRowNumber)
        );
    }

    private Pair<OrganizationIngestionFlowFileDTO, List<OrganizationErrorDTO>> configureUnhappyUseCaseBrokerMissmatch(IngestionFlowFile ingestionFlowFile, long rowNumber) {
        OrganizationIngestionFlowFileDTO dto = podamFactory.manufacturePojo(OrganizationIngestionFlowFileDTO.class);
        dto.setBrokerCf("DIFFERENT_BROKER_CF");

        List<OrganizationErrorDTO> expectedErrors = List.of(
                OrganizationErrorDTO.builder()
                        .fileName(ingestionFlowFile.getFileName())
                        .rowNumber(rowNumber)
                        .errorCode(FileErrorCode.BROKER_MISMATCH.name())
                        .errorMessage(FileErrorCode.BROKER_MISMATCH.getMessage())
                        .ipaCode(dto.getIpaCode())
                        .build()
        );

        return Pair.of(dto, expectedErrors);
    }

    private Pair<OrganizationIngestionFlowFileDTO, List<OrganizationErrorDTO>> configureUnhappyUseCaseAlreadyExists(IngestionFlowFile ingestionFlowFile, long rowNumber) {
        OrganizationIngestionFlowFileDTO dto = podamFactory.manufacturePojo(OrganizationIngestionFlowFileDTO.class);
        dto.setBrokerCf(organization.getOrgFiscalCode());

        Organization existingOrg = podamFactory.manufacturePojo(Organization.class);

        Mockito.doReturn(Optional.of(existingOrg))
                .when(organizationServiceMock)
                .getOrganizationByFiscalCode(dto.getOrgFiscalCode());

        List<OrganizationErrorDTO> expectedErrors = List.of(
                OrganizationErrorDTO.builder()
                        .fileName(ingestionFlowFile.getFileName())
                        .rowNumber(rowNumber)
                        .errorCode("ORGANIZATION_ALREADY_EXISTS")
                        .errorMessage("L'ente esiste gia'")
                        .ipaCode(dto.getIpaCode())
                        .build()
        );

        return Pair.of(dto, expectedErrors);
    }

    @Test
    void givenBrokerOrganizationNotFoundWhenProcessThenSetErrorDescription() {
        // Given
        OrganizationIngestionFlowFileDTO dto = podamFactory.manufacturePojo(OrganizationIngestionFlowFileDTO.class);

        Mockito.reset(organizationServiceMock);
        Mockito.when(organizationServiceMock.getOrganizationById(ingestionFlowFile.getOrganizationId()))
                .thenReturn(Optional.of(new Organization()));

        // When
        OrganizationIngestionFlowFileResult result = serviceSpy.processOrganization(
                Stream.of(dto).iterator(), List.of(),
                ingestionFlowFile, workingDirectory);

        // Then
        Assertions.assertNull(result.getBrokerId());
        Assertions.assertEquals("L'intermediario non e' stato trovato", result.getErrorDescription());
        Assertions.assertEquals(0, result.getProcessedRows());
    }
}