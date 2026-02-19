package it.gov.pagopa.payhub.activities.service.ingestionflow.orgsilservice;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.organization.OrgSilServiceService;
import it.gov.pagopa.payhub.activities.dto.ingestion.orgsilservice.OrgSilServiceErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.orgsilservice.OrgSilServiceIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.orgsilservice.OrgSilServiceIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.orgsilservice.OrgSilServiceMapper;
import it.gov.pagopa.payhub.activities.service.files.ErrorArchiverService;
import it.gov.pagopa.payhub.activities.service.files.FileExceptionHandlerService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.BaseIngestionFlowProcessingServiceTest;
import it.gov.pagopa.pu.organization.dto.generated.OrgSilService;
import it.gov.pagopa.pu.organization.dto.generated.OrgSilServiceDTO;
import it.gov.pagopa.pu.organization.dto.generated.OrgSilServiceType;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class OrgSilServiceProcessingServiceTest extends BaseIngestionFlowProcessingServiceTest<OrgSilServiceIngestionFlowFileDTO, OrgSilServiceIngestionFlowFileResult, OrgSilServiceErrorDTO> {

    @Mock
    private OrgSilServiceErrorsArchiverService errorsArchiverServiceMock;
    @Mock
    private OrgSilServiceMapper mapperMock;
    @Mock
    private OrgSilServiceService orgSilServiceServiceMock;

    private OrgSilServiceProcessingService serviceSpy;

    protected OrgSilServiceProcessingServiceTest() {
        super(true);
    }

    @BeforeEach
    void init() {
        FileExceptionHandlerService fileExceptionHandlerService = new FileExceptionHandlerService();
        serviceSpy = Mockito.spy(new OrgSilServiceProcessingService(
                MAX_CONCURRENT_PROCESSING_ROWS,
                mapperMock,
                errorsArchiverServiceMock,
                organizationServiceMock,
                orgSilServiceServiceMock,
                fileExceptionHandlerService
        ));
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                mapperMock,
                errorsArchiverServiceMock,
                organizationServiceMock,
                orgSilServiceServiceMock
        );
    }

    @Override
    protected OrgSilServiceProcessingService getServiceSpy() {
        return serviceSpy;
    }

    @Override
    protected ErrorArchiverService<OrgSilServiceErrorDTO, OrgSilServiceIngestionFlowFileResult> getErrorsArchiverServiceMock() {
        return errorsArchiverServiceMock;
    }

    @Override
    protected OrgSilServiceIngestionFlowFileResult startProcess(Iterator<OrgSilServiceIngestionFlowFileDTO> rowIterator, List<CsvException> readerExceptions, IngestionFlowFile ingestionFlowFile, Path workingDirectory) {
        return serviceSpy.processOrgSilService(rowIterator, readerExceptions, ingestionFlowFile, workingDirectory);
    }

    @Override
    protected OrgSilServiceIngestionFlowFileDTO buildAndConfigureHappyUseCase(IngestionFlowFile ingestionFlowFile, int sequencingId, boolean sequencingIdAlreadySent, long rowNumber) {
        long existingOrgSilServiceId = sequencingId * 10L;
        OrgSilServiceType serviceType = OrgSilServiceType.ACTUALIZATION;

        OrgSilServiceIngestionFlowFileDTO dto = podamFactory.manufacturePojo(OrgSilServiceIngestionFlowFileDTO.class);
        dto.setIpaCode(organization.getIpaCode());
        dto.setServiceType(serviceType.getValue());
        dto.setApplicationName("APPLICATION_NAME" + sequencingId);

        OrgSilServiceDTO orgSilServiceDTOMapped = podamFactory.manufacturePojo(OrgSilServiceDTO.class);
        Mockito.doReturn(orgSilServiceDTOMapped)
                .when(mapperMock)
                .map(dto, ingestionFlowFile.getOrganizationId());

        Mockito.doAnswer(a -> {
                    if (sequencingId == 1) {
                        OrgSilServiceDTO savingEntity = a.getArgument(0);
                        Assertions.assertEquals(existingOrgSilServiceId, savingEntity.getOrgSilServiceId());
                    }
                    return podamFactory.manufacturePojo(OrgSilServiceDTO.class);
                })
                .when(orgSilServiceServiceMock)
                .createOrUpdateOrgSilService(orgSilServiceDTOMapped);

        // due to the limit on possible useCases caused by ServiceType enum, the retrieve operation has been configured just once per enum value used

        // Using ACTUALIZATION service type to configure the useCase of existing serviceType
        if (!sequencingIdAlreadySent && sequencingId == 1) {
            // configuring useCase of matching applicationName just for sequencingId 1
            OrgSilService existingMatchIfSequencing1 = podamFactory.manufacturePojo(OrgSilService.class);
            existingMatchIfSequencing1.setOrgSilServiceId(existingOrgSilServiceId);
            existingMatchIfSequencing1.setApplicationName(dto.getApplicationName());

            OrgSilService existingNotMatch = podamFactory.manufacturePojo(OrgSilService.class);
            existingNotMatch.setApplicationName("OTHERNAME");
            Mockito.doReturn(List.of(existingMatchIfSequencing1, existingNotMatch))
                    .when(orgSilServiceServiceMock)
                    .getAllByOrganizationIdAndServiceType(ingestionFlowFile.getOrganizationId(), serviceType);
        }

        // Using PAID_NOTIFICATION_OUTCOME service type to configure the useCase of not existing serviceType
        if (sequencingId == 2) {
            serviceType = OrgSilServiceType.PAID_NOTIFICATION_OUTCOME;
            dto.setServiceType(serviceType.getValue());

            if (!sequencingIdAlreadySent) {
                Mockito.doReturn(Collections.emptyList())
                        .when(orgSilServiceServiceMock)
                        .getAllByOrganizationIdAndServiceType(ingestionFlowFile.getOrganizationId(), serviceType);
            }
        }

        return dto;
    }

    @Override
    protected List<Pair<OrgSilServiceIngestionFlowFileDTO, List<OrgSilServiceErrorDTO>>> buildAndConfigureUnhappyUseCases(IngestionFlowFile ingestionFlowFile, long previousRowNumber) {
        return List.of(
                configureUnhappyUseCaseIpaMissmatch(ingestionFlowFile, ++previousRowNumber),
                configureUnhappyUseCaseInvalidServiceType(ingestionFlowFile, ++previousRowNumber)
        );
    }

    private Pair<OrgSilServiceIngestionFlowFileDTO, List<OrgSilServiceErrorDTO>> configureUnhappyUseCaseIpaMissmatch(IngestionFlowFile ingestionFlowFile, long rowNumber) {
        OrgSilServiceIngestionFlowFileDTO dto = podamFactory.manufacturePojo(OrgSilServiceIngestionFlowFileDTO.class);
        dto.setIpaCode("WRONGIPACODE");
        dto.setServiceType(OrgSilServiceType.ACTUALIZATION.getValue());

        List<OrgSilServiceErrorDTO> expectedErrors = List.of(
                OrgSilServiceErrorDTO.builder()
                        .fileName(ingestionFlowFile.getFileName())
                        .rowNumber(rowNumber)
                        .errorCode("ORGANIZATION_IPA_MISMATCH")
                        .errorMessage("Il codice IPA WRONGIPACODE dell'ente non corrisponde a quello del file " + organization.getIpaCode())
                        .ipaCode(dto.getIpaCode())
                        .applicationName(dto.getApplicationName())
                        .build()
        );

        return Pair.of(dto, expectedErrors);
    }

    private Pair<OrgSilServiceIngestionFlowFileDTO, List<OrgSilServiceErrorDTO>> configureUnhappyUseCaseInvalidServiceType(IngestionFlowFile ingestionFlowFile, long rowNumber) {
        OrgSilServiceIngestionFlowFileDTO dto = podamFactory.manufacturePojo(OrgSilServiceIngestionFlowFileDTO.class);
        dto.setIpaCode(organization.getIpaCode());
        dto.setServiceType("INVALID_SERVICE_TYPE");

        List<OrgSilServiceErrorDTO> expectedErrors = List.of(
                OrgSilServiceErrorDTO.builder()
                        .fileName(ingestionFlowFile.getFileName())
                        .rowNumber(rowNumber)
                        .errorCode("ORG_SIL_SERVICE_TYPE_INVALID")
                        .errorMessage("L'Org Sil Service type indicato non e' valido: INVALID_SERVICE_TYPE")
                        .ipaCode(dto.getIpaCode())
                        .applicationName(dto.getApplicationName())
                        .build()
        );

        return Pair.of(dto, expectedErrors);
    }

}