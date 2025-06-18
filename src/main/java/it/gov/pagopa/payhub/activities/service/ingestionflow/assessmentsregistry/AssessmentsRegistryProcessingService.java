package it.gov.pagopa.payhub.activities.service.ingestionflow.assessmentsregistry;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.classification.AssessmentsRegistryService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.assessmentsregistry.AssessmentsRegistryErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.assessmentsregistry.AssessmentsRegistryIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.assessmentsregistry.AssessmentsRegistryIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.exception.organization.OrganizationIpaCodeNotMatchException;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.assessmentsregistry.AssessmentsRegistryMapper;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowProcessingService;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsRegistry;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@Lazy
@Slf4j
public class AssessmentsRegistryProcessingService extends
        IngestionFlowProcessingService<AssessmentsRegistryIngestionFlowFileDTO, AssessmentsRegistryIngestionFlowFileResult, AssessmentsRegistryErrorDTO> {

    private final AssessmentsRegistryMapper assessmentsRegistryMapper;
    private final AssessmentsRegistryService assessmentsRegistryService;

    public AssessmentsRegistryProcessingService(
            AssessmentsRegistryMapper assessmentsRegistryMapper,
            AssessmentsRegistryErrorsArchiverService assessmentsRegistryErrorsArchiverService,
            AssessmentsRegistryService assessmentsRegistryService, OrganizationService organizationService) {
        super(assessmentsRegistryErrorsArchiverService, organizationService);
        this.assessmentsRegistryMapper = assessmentsRegistryMapper;
        this.assessmentsRegistryService = assessmentsRegistryService;
    }

    public AssessmentsRegistryIngestionFlowFileResult processAssessmentsRegistry(
            Iterator<AssessmentsRegistryIngestionFlowFileDTO> iterator,
            List<CsvException> readerException,
            IngestionFlowFile ingestionFlowFile, Path workingDirectory) {

        List<AssessmentsRegistryErrorDTO> errorList = new ArrayList<>();

        AssessmentsRegistryIngestionFlowFileResult ingestionFlowFileResult = new AssessmentsRegistryIngestionFlowFileResult();
        ingestionFlowFileResult.setFileVersion("1.0");
        ingestionFlowFileResult.setOrganizationId(ingestionFlowFile.getOrganizationId());

        String ipaCode = getIpaCodeByOrganizationId(ingestionFlowFile.getOrganizationId());
        ingestionFlowFileResult.setIpaCode(ipaCode);

        process(iterator, readerException, ingestionFlowFileResult, ingestionFlowFile, errorList, workingDirectory);
        return ingestionFlowFileResult;
    }

    @Override
    protected boolean consumeRow(long lineNumber,
                                 AssessmentsRegistryIngestionFlowFileDTO row,
                                 AssessmentsRegistryIngestionFlowFileResult ingestionFlowFileResult,
                                 List<AssessmentsRegistryErrorDTO> errorList, IngestionFlowFile ingestionFlowFile) {
        try {
            String ipa = ingestionFlowFileResult.getIpaCode();
            if (!row.getOrganizationIpaCode().equalsIgnoreCase(ipa)) {
                String errorMessage = String.format(
                        "Organization IPA code %s does not match with the one in the ingestion flow file %s",
                        row.getOrganizationIpaCode(), ipa);
                log.error(errorMessage);
                throw new OrganizationIpaCodeNotMatchException(errorMessage);
            }

            AssessmentsRegistry assessmentsRegistry = assessmentsRegistryMapper.map(
                    row, ingestionFlowFileResult.getOrganizationId());

            assessmentsRegistryService.createAssessmentsRegistry(assessmentsRegistry);
            return true;

        } catch (Exception e) {
            log.error("Error processing row {} in file {}: {}", lineNumber, ingestionFlowFile.getFileName(), e.getMessage(), e);
            AssessmentsRegistryErrorDTO error = new AssessmentsRegistryErrorDTO(
                    ingestionFlowFile.getFileName(),
                    lineNumber,
                    row.getAssessmentCode(),
                    row.getOrganizationIpaCode(),
                    "PROCESS_EXCEPTION", e.getMessage());
            errorList.add(error);
            log.info("Current error list size after handleProcessingError: {}", errorList.size());
            return false;
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

