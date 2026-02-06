package it.gov.pagopa.payhub.activities.service.ingestionflow.organization;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.organization.OrganizationErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.organization.OrganizationIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.organization.OrganizationIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.enums.FileErrorCode;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.organization.OrganizationMapper;
import it.gov.pagopa.payhub.activities.service.files.FileExceptionHandlerService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowProcessingService;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
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
public class OrganizationProcessingService extends IngestionFlowProcessingService<OrganizationIngestionFlowFileDTO, OrganizationIngestionFlowFileResult, OrganizationErrorDTO> {

    private final OrganizationMapper organizationMapper;

    public OrganizationProcessingService(
            @Value("${ingestion-flow-files.organizations.max-concurrent-processing-rows}") int maxConcurrentProcessingRows,

            OrganizationMapper organizationMapper,
            OrganizationErrorsArchiverService organizationErrorsArchiverService,
            OrganizationService organizationService, FileExceptionHandlerService fileExceptionHandlerService) {
        super(maxConcurrentProcessingRows, organizationErrorsArchiverService, organizationService, fileExceptionHandlerService);
        this.organizationMapper = organizationMapper;
    }

    public OrganizationIngestionFlowFileResult processOrganization(
            Iterator<OrganizationIngestionFlowFileDTO> iterator,
            List<CsvException> readerException,
            IngestionFlowFile ingestionFlowFile, Path workingDirectory) {
        List<OrganizationErrorDTO> errorList = new ArrayList<>();
        OrganizationIngestionFlowFileResult ingestionFlowFileResult = new OrganizationIngestionFlowFileResult();

        Optional<Organization> organizationBroker = organizationService.getOrganizationById(ingestionFlowFile.getOrganizationId());
        Long brokerId = organizationBroker.map(Organization::getBrokerId).orElse(null);
        if (brokerId == null) {
            log.error("Broker for organization id {} not found", ingestionFlowFile.getOrganizationId());
            ingestionFlowFileResult.setErrorDescription("Broker not found");
            return ingestionFlowFileResult;
        }
        ingestionFlowFileResult.setBrokerId(organizationBroker.get().getBrokerId());
        ingestionFlowFileResult.setBrokerFiscalCode(organizationBroker.get().getOrgFiscalCode());
        ingestionFlowFileResult.setFileVersion(ingestionFlowFile.getFileVersion());

        process(iterator, readerException, ingestionFlowFileResult, ingestionFlowFile, errorList, workingDirectory);
        return ingestionFlowFileResult;
    }

    @Override
    protected String getSequencingId(OrganizationIngestionFlowFileDTO row) {
        return row.getOrgFiscalCode();
    }

    @Override
    protected List<OrganizationErrorDTO> consumeRow(long lineNumber,
                                                    OrganizationIngestionFlowFileDTO organizationDTO,
                                                    OrganizationIngestionFlowFileResult ingestionFlowFileResult,
                                                    IngestionFlowFile ingestionFlowFile) {
        if (!ingestionFlowFileResult.getBrokerFiscalCode().equals(organizationDTO.getBrokerCf())) {
            log.error("Broker with fiscal code {} is not related to organization having fiscal code {}", ingestionFlowFileResult.getBrokerFiscalCode(), organizationDTO.getOrgFiscalCode());
            OrganizationErrorDTO error = buildErrorDto(
                    ingestionFlowFile, lineNumber, organizationDTO,
                    FileErrorCode.BROKER_MISMATCH.name(),
                    FileErrorCode.BROKER_MISMATCH.getMessage());
            return List.of(error);
        }

        Optional<Organization> existingOrg = organizationService.getOrganizationByFiscalCode(organizationDTO.getOrgFiscalCode());
        if (existingOrg.isPresent()) {
            OrganizationErrorDTO error = buildErrorDto(
                    ingestionFlowFile, lineNumber, organizationDTO,
                    FileErrorCode.ORGANIZATION_ALREADY_EXISTS.name(),
                    FileErrorCode.ORGANIZATION_ALREADY_EXISTS.getMessage());
            return List.of(error);
        }

        organizationService.createOrganization(
                organizationMapper.map(organizationDTO, ingestionFlowFileResult.getBrokerId()));

        return Collections.emptyList();
    }

    @Override
    protected OrganizationErrorDTO buildErrorDto(IngestionFlowFile ingestionFlowFile, long lineNumber, OrganizationIngestionFlowFileDTO row, String errorCode, String message) {
        OrganizationErrorDTO errorDTO = OrganizationErrorDTO.builder()
                .fileName(ingestionFlowFile.getFileName())
                .rowNumber(lineNumber)
                .errorCode(errorCode)
                .errorMessage(message)
                .build();
        if (row != null) {
            errorDTO.setIpaCode(row.getIpaCode());
        }
        return errorDTO;
    }

}

