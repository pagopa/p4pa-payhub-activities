package it.gov.pagopa.payhub.activities.service.ingestionflow.assessmentsregistry;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.classification.AssessmentsRegistryService;
import it.gov.pagopa.payhub.activities.dto.assessments.AssessmentsRegistrySemanticKey;
import it.gov.pagopa.payhub.activities.dto.ingestion.assessmentsregistry.AssessmentsRegistryErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.assessmentsregistry.AssessmentsRegistryIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.assessmentsregistry.AssessmentsRegistryIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.enums.FileErrorCode;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.assessmentsregistry.AssessmentsRegistryMapper;
import it.gov.pagopa.payhub.activities.service.files.ErrorArchiverService;
import it.gov.pagopa.payhub.activities.service.files.FileExceptionHandlerService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.BaseIngestionFlowProcessingServiceTest;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsRegistry;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static it.gov.pagopa.payhub.activities.util.faker.AssessmentsRegistryFaker.buildAssessmentsRegistry;

@ExtendWith(MockitoExtension.class)
class AssessmentRegistryProcessingServiceTest extends BaseIngestionFlowProcessingServiceTest<AssessmentsRegistryIngestionFlowFileDTO, AssessmentsRegistryIngestionFlowFileResult, AssessmentsRegistryErrorDTO> {

    @Mock
    private AssessmentsRegistryErrorsArchiverService errorsArchiverServiceMock;
    @Mock
    private AssessmentsRegistryMapper mapperMock;
    @Mock
    private AssessmentsRegistryService assessmentsRegistryServiceMock;

    private AssessmentsRegistryProcessingService serviceSpy;

    protected AssessmentRegistryProcessingServiceTest() {
        super(true);
    }

    @BeforeEach
    void init() {
        FileExceptionHandlerService fileExceptionHandlerService = new FileExceptionHandlerService();
        serviceSpy = Mockito.spy(new AssessmentsRegistryProcessingService(
                MAX_CONCURRENT_PROCESSING_ROWS,
                mapperMock,
                errorsArchiverServiceMock,
                assessmentsRegistryServiceMock,
                organizationServiceMock,
                fileExceptionHandlerService
        ));
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                mapperMock,
                errorsArchiverServiceMock,
                assessmentsRegistryServiceMock,
                organizationServiceMock
        );
    }

    @Override
    protected AssessmentsRegistryProcessingService getServiceSpy() {
        return serviceSpy;
    }

    @Override
    protected ErrorArchiverService<AssessmentsRegistryErrorDTO, AssessmentsRegistryIngestionFlowFileResult> getErrorsArchiverServiceMock() {
        return errorsArchiverServiceMock;
    }

    @Override
    protected AssessmentsRegistryIngestionFlowFileResult startProcess(Iterator<AssessmentsRegistryIngestionFlowFileDTO> rowIterator, List<CsvException> readerExceptions, IngestionFlowFile ingestionFlowFile, Path workingDirectory) {
        return serviceSpy.processAssessmentsRegistry(rowIterator, readerExceptions, ingestionFlowFile, workingDirectory);
    }

    @Override
    protected AssessmentsRegistryIngestionFlowFileDTO buildAndConfigureHappyUseCase(IngestionFlowFile ingestionFlowFile, int sequencingId, boolean sequencingIdAlreadySent, long rowNumber) {
        AssessmentsRegistryIngestionFlowFileDTO dto = podamFactory.manufacturePojo(AssessmentsRegistryIngestionFlowFileDTO.class);
        dto.setOrganizationIpaCode(organization.getIpaCode());
        dto.setDebtPositionTypeOrgCode("DTOCODE" + sequencingId);
        dto.setSectionCode("SECTIONCODE" + sequencingId);
        dto.setOfficeCode("OFFICECODE" + sequencingId);
        dto.setAssessmentCode("ASSESSMENTCODE" + sequencingId);
        dto.setOperatingYear(String.valueOf(2020 + sequencingId));

        AssessmentsRegistry assessmentsRegistry = podamFactory.manufacturePojo(AssessmentsRegistry.class);
        AssessmentsRegistrySemanticKey registrySemanticKey = AssessmentsRegistrySemanticKey.builder()
                .organizationId(assessmentsRegistry.getOrganizationId())
                .debtPositionTypeOrgCode(assessmentsRegistry.getDebtPositionTypeOrgCode())
                .sectionCode(assessmentsRegistry.getSectionCode())
                .officeCode(assessmentsRegistry.getOfficeCode())
                .assessmentCode(assessmentsRegistry.getAssessmentCode())
                .operatingYear(assessmentsRegistry.getOperatingYear())
                .build();

        Mockito.doReturn(assessmentsRegistry)
                .when(mapperMock)
                .map(dto, ingestionFlowFile.getOrganizationId());
        Mockito.doReturn(Optional.empty())
                .when(assessmentsRegistryServiceMock)
                .searchAssessmentsRegistryBySemanticKey(registrySemanticKey);

        Mockito.doNothing()
                .when(assessmentsRegistryServiceMock)
                .createAssessmentsRegistry(assessmentsRegistry);

        return dto;
    }

    @Override
    protected List<Pair<AssessmentsRegistryIngestionFlowFileDTO, List<AssessmentsRegistryErrorDTO>>> buildAndConfigureUnhappyUseCases(IngestionFlowFile ingestionFlowFile, long previousRowNumber) {
        return List.of(
                configureUnhappyUseCaseIpaMismatch(ingestionFlowFile, ++previousRowNumber),
                configureUnhappyUseCaseAlreadyExists(ingestionFlowFile, ++previousRowNumber)
        );
    }

    private Pair<AssessmentsRegistryIngestionFlowFileDTO, List<AssessmentsRegistryErrorDTO>> configureUnhappyUseCaseIpaMismatch(IngestionFlowFile ingestionFlowFile, long rowNumber) {
        AssessmentsRegistryIngestionFlowFileDTO dto = podamFactory.manufacturePojo(AssessmentsRegistryIngestionFlowFileDTO.class);
        dto.setOrganizationIpaCode("WRONGIPACODE");

        List<AssessmentsRegistryErrorDTO> expectedErrors = List.of(
                AssessmentsRegistryErrorDTO.builder()
                        .fileName(ingestionFlowFile.getFileName())
                        .errorCode(FileErrorCode.ORGANIZATION_IPA_MISMATCH.name())
                        .errorMessage("Il codice IPA WRONGIPACODE dell'ente non corrisponde a quello del file " + organization.getIpaCode())
                        .rowNumber(rowNumber)
                        .organizationIpaCode(dto.getOrganizationIpaCode())
                        .assessmentCode(dto.getAssessmentCode())
                        .build()
        );
        return Pair.of(dto, expectedErrors);
    }

    private Pair<AssessmentsRegistryIngestionFlowFileDTO, List<AssessmentsRegistryErrorDTO>> configureUnhappyUseCaseAlreadyExists(IngestionFlowFile ingestionFlowFile, long rowNumber) {
        AssessmentsRegistryIngestionFlowFileDTO dto = podamFactory.manufacturePojo(AssessmentsRegistryIngestionFlowFileDTO.class);
        dto.setOrganizationIpaCode(organization.getIpaCode());

        AssessmentsRegistry assessmentsRegistry = buildAssessmentsRegistry();
        AssessmentsRegistrySemanticKey registrySemanticKey = AssessmentsRegistrySemanticKey.builder()
                .organizationId(assessmentsRegistry.getOrganizationId())
                .debtPositionTypeOrgCode(assessmentsRegistry.getDebtPositionTypeOrgCode())
                .sectionCode(assessmentsRegistry.getSectionCode())
                .officeCode(assessmentsRegistry.getOfficeCode())
                .assessmentCode(assessmentsRegistry.getAssessmentCode())
                .operatingYear(assessmentsRegistry.getOperatingYear())
                .build();

        Mockito.doReturn(assessmentsRegistry)
                .when(mapperMock)
                .map(dto, ingestionFlowFile.getOrganizationId());

        Mockito.doReturn(Optional.of(assessmentsRegistry))
                .when(assessmentsRegistryServiceMock)
                .searchAssessmentsRegistryBySemanticKey(registrySemanticKey);

        List<AssessmentsRegistryErrorDTO> expectedErrors = List.of(
                AssessmentsRegistryErrorDTO.builder()
                        .fileName(ingestionFlowFile.getFileName())
                        .errorCode(FileErrorCode.ASSESSMENTS_REGISTRY_ALREADY_EXISTS.name())
                        .errorMessage(FileErrorCode.ASSESSMENTS_REGISTRY_ALREADY_EXISTS.getMessage())
                        .rowNumber(rowNumber)
                        .organizationIpaCode(dto.getOrganizationIpaCode())
                        .assessmentCode(dto.getAssessmentCode())
                        .build()
        );
        return Pair.of(dto, expectedErrors);
    }
}
