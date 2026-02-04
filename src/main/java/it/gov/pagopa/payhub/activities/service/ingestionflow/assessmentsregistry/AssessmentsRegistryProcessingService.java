package it.gov.pagopa.payhub.activities.service.ingestionflow.assessmentsregistry;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.classification.AssessmentsRegistryService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.assessments.AssessmentsRegistrySemanticKey;
import it.gov.pagopa.payhub.activities.dto.ingestion.assessmentsregistry.AssessmentsRegistryErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.assessmentsregistry.AssessmentsRegistryIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.assessmentsregistry.AssessmentsRegistryIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.enums.FileErrorCode;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.assessmentsregistry.AssessmentsRegistryMapper;
import it.gov.pagopa.payhub.activities.service.files.FileExceptionHandlerService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowProcessingService;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsRegistry;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.*;

@Service
@Lazy
@Slf4j
public class AssessmentsRegistryProcessingService extends
        IngestionFlowProcessingService<AssessmentsRegistryIngestionFlowFileDTO, AssessmentsRegistryIngestionFlowFileResult, AssessmentsRegistryErrorDTO> {

    private final AssessmentsRegistryMapper assessmentsRegistryMapper;
    private final AssessmentsRegistryService assessmentsRegistryService;
    private final FileExceptionHandlerService fileExceptionHandlerService;

    public AssessmentsRegistryProcessingService(
            @Value("${ingestion-flow-files.assessments-registry.max-concurrent-processing-rows}") int maxConcurrentProcessingRows,

            AssessmentsRegistryMapper assessmentsRegistryMapper,
            AssessmentsRegistryErrorsArchiverService assessmentsRegistryErrorsArchiverService,
            AssessmentsRegistryService assessmentsRegistryService, OrganizationService organizationService,
            FileExceptionHandlerService fileExceptionHandlerService) {
        super(maxConcurrentProcessingRows, assessmentsRegistryErrorsArchiverService, organizationService, fileExceptionHandlerService);
        this.assessmentsRegistryMapper = assessmentsRegistryMapper;
        this.assessmentsRegistryService = assessmentsRegistryService;
        this.fileExceptionHandlerService = fileExceptionHandlerService;
    }

    public AssessmentsRegistryIngestionFlowFileResult processAssessmentsRegistry(
            Iterator<AssessmentsRegistryIngestionFlowFileDTO> iterator,
            List<CsvException> readerException,
            IngestionFlowFile ingestionFlowFile, Path workingDirectory) {

        List<AssessmentsRegistryErrorDTO> errorList = new ArrayList<>();

        AssessmentsRegistryIngestionFlowFileResult ingestionFlowFileResult = new AssessmentsRegistryIngestionFlowFileResult();
        ingestionFlowFileResult.setFileVersion(ingestionFlowFile.getFileVersion());
        ingestionFlowFileResult.setOrganizationId(ingestionFlowFile.getOrganizationId());

        String ipaCode = getIpaCodeByOrganizationId(ingestionFlowFile.getOrganizationId());
        ingestionFlowFileResult.setIpaCode(ipaCode);

        process(iterator, readerException, ingestionFlowFileResult, ingestionFlowFile, errorList, workingDirectory);
        return ingestionFlowFileResult;
    }

    @Override
    protected String getSequencingId(AssessmentsRegistryIngestionFlowFileDTO row) {
        return row.getDebtPositionTypeOrgCode() +
                "-" + row.getSectionCode() +
                "-" + row.getOfficeCode() +
                "-" +row.getAssessmentCode()+
                "-" +row.getOperatingYear();
    }

    @Override
    protected List<AssessmentsRegistryErrorDTO> consumeRow(long lineNumber,
                                 AssessmentsRegistryIngestionFlowFileDTO row,
                                 AssessmentsRegistryIngestionFlowFileResult ingestionFlowFileResult,
                                 IngestionFlowFile ingestionFlowFile) {
        try {
            String ipa = ingestionFlowFileResult.getIpaCode();
            if (!row.getOrganizationIpaCode().equalsIgnoreCase(ipa)) {
                log.error("Organization IPA code {} does not match with the one in the ingestion flow file {}", row.getOrganizationIpaCode(), ipa);
                AssessmentsRegistryErrorDTO error = new AssessmentsRegistryErrorDTO(
                        ingestionFlowFile.getFileName(), lineNumber, row.getAssessmentCode(),
                        row.getOrganizationIpaCode(),
                        FileErrorCode.ORGANIZATION_IPA_MISMATCH.name(),
                        FileErrorCode.ORGANIZATION_IPA_MISMATCH.format(row.getOrganizationIpaCode(), ipa));
                return List.of(error);
            }

            AssessmentsRegistry assessmentsRegistry = assessmentsRegistryMapper.map(
                    row, ingestionFlowFileResult.getOrganizationId());

            Optional<AssessmentsRegistry> assessmentsRegistryOptional =
                    assessmentsRegistryService.searchAssessmentsRegistryBySemanticKey(
                            AssessmentsRegistrySemanticKey.builder()
                                    .organizationId(assessmentsRegistry.getOrganizationId())
                                    .debtPositionTypeOrgCode(assessmentsRegistry.getDebtPositionTypeOrgCode())
                                    .sectionCode(assessmentsRegistry.getSectionCode())
                                    .officeCode(assessmentsRegistry.getOfficeCode())
                                    .assessmentCode(assessmentsRegistry.getAssessmentCode())
                                    .operatingYear(assessmentsRegistry.getOperatingYear())
                                    .build()
                    );
            if (assessmentsRegistryOptional.isPresent()) {
                AssessmentsRegistryErrorDTO error = new AssessmentsRegistryErrorDTO(
                        ingestionFlowFile.getFileName(), lineNumber, assessmentsRegistry.getAssessmentCode(),
                        row.getOrganizationIpaCode(),
                        FileErrorCode.ASSESSMENTS_REGISTRY_ALREADY_EXISTS.name(),
                        FileErrorCode.ASSESSMENTS_REGISTRY_ALREADY_EXISTS.getMessage());
                return List.of(error);
            }

            assessmentsRegistryService.createAssessmentsRegistry(assessmentsRegistry);
            return Collections.emptyList();

        } catch (Exception e) {
            log.error("Error processing row {} in file {}: {}", lineNumber, ingestionFlowFile.getFileName(), e.getMessage(), e);
            FileExceptionHandlerService.ErrorDetails errorDetails = fileExceptionHandlerService.mapExceptionToErrorCodeAndMessage(e.getMessage());
            AssessmentsRegistryErrorDTO error = new AssessmentsRegistryErrorDTO(
                    ingestionFlowFile.getFileName(),
                    lineNumber,
                    row.getAssessmentCode(),
                    row.getOrganizationIpaCode(),
                    errorDetails.getErrorCode(), errorDetails.getErrorMessage());
            return List.of(error);
        }
    }

    @Override
    protected AssessmentsRegistryErrorDTO buildErrorDto(String fileName, long lineNumber,
                                                        String errorCode, String message) {
        return AssessmentsRegistryErrorDTO.builder()
                .fileName(fileName)
                .rowNumber(lineNumber)
                .errorCode(errorCode)
                .errorMessage(message)
                .build();
    }
}

