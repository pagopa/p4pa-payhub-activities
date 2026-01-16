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
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@Lazy
@Slf4j
public class OrganizationProcessingService extends IngestionFlowProcessingService<OrganizationIngestionFlowFileDTO, OrganizationIngestionFlowFileResult, OrganizationErrorDTO> {

    private final OrganizationMapper organizationMapper;
    private final FileExceptionHandlerService fileExceptionHandlerService;

    public OrganizationProcessingService(
            OrganizationMapper organizationMapper,
            OrganizationErrorsArchiverService organizationErrorsArchiverService,
            OrganizationService organizationService, FileExceptionHandlerService fileExceptionHandlerService) {
        super(organizationErrorsArchiverService, organizationService, fileExceptionHandlerService);
        this.organizationMapper = organizationMapper;
        this.fileExceptionHandlerService = fileExceptionHandlerService;
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
    protected boolean consumeRow(long lineNumber, OrganizationIngestionFlowFileDTO organizationDTO, OrganizationIngestionFlowFileResult ingestionFlowFileResult, List<OrganizationErrorDTO> errorList, IngestionFlowFile ingestionFlowFile) {
        try {
            if (!ingestionFlowFileResult.getBrokerFiscalCode().equals(organizationDTO.getBrokerCf())) {
                log.error("Broker with fiscal code {} is not related to organization having fiscal code {}", ingestionFlowFileResult.getBrokerFiscalCode(), organizationDTO.getOrgFiscalCode());
                OrganizationErrorDTO error = new OrganizationErrorDTO(
                        ingestionFlowFile.getFileName(), organizationDTO.getIpaCode(),
                        lineNumber, FileErrorCode.BROKER_MISMATCH.name(),
                        FileErrorCode.BROKER_MISMATCH.getMessage());
                errorList.add(error);
                return false;
            }

            Optional<Organization> existingOrg = organizationService.getOrganizationByFiscalCode(organizationDTO.getOrgFiscalCode());
            if (existingOrg.isPresent()) {
                OrganizationErrorDTO error = new OrganizationErrorDTO(
                        ingestionFlowFile.getFileName(), organizationDTO.getIpaCode(),
                        lineNumber, FileErrorCode.ORGANIZATION_ALREADY_EXISTS.name(),
                        FileErrorCode.ORGANIZATION_ALREADY_EXISTS.getMessage());
                errorList.add(error);
                return false;
            }

            organizationService.createOrganization(
                    organizationMapper.map(organizationDTO, ingestionFlowFileResult.getBrokerId()));

            return true;

        } catch (Exception e) {
            log.error("Error processing organization with ipa code {}: {}", organizationDTO.getIpaCode(), e.getMessage());
            FileExceptionHandlerService.ErrorDetails errorDetails = fileExceptionHandlerService.mapExceptionToErrorCodeAndMessage(e.getMessage());
            OrganizationErrorDTO error = new OrganizationErrorDTO(
                    ingestionFlowFile.getFileName(), organizationDTO.getIpaCode(),
                    lineNumber, errorDetails.getErrorCode(), errorDetails.getErrorMessage());
            errorList.add(error);
            log.info("Current error list size after handleProcessingError: {}", errorList.size());
            return false;
        }
    }

    @Override
    protected OrganizationErrorDTO buildErrorDto(String fileName, long lineNumber, String errorCode, String message) {
        return OrganizationErrorDTO.builder()
                .fileName(fileName)
                .rowNumber(lineNumber)
                .errorCode(errorCode)
                .errorMessage(message)
                .build();
    }

}

