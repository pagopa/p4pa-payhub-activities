package it.gov.pagopa.payhub.activities.service.ingestionflow.debtpositiontypeorg;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionTypeOrgService;
import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionTypeService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontypeorg.DebtPositionTypeOrgErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontypeorg.DebtPositionTypeOrgIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontypeorg.DebtPositionTypeOrgIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.debtpositiontypeorg.DebtPositionTypeOrgMapper;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowProcessingService;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionType;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionTypeOrg;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Validated
@Service
@Lazy
@Slf4j
public class DebtPositionTypeOrgProcessingService extends IngestionFlowProcessingService<DebtPositionTypeOrgIngestionFlowFileDTO, DebtPositionTypeOrgIngestionFlowFileResult, DebtPositionTypeOrgErrorDTO> {

    private final DebtPositionTypeOrgMapper debtPositionTypeOrgMapper;
    private final DebtPositionTypeOrgService debtPositionTypeOrgService;
    private final DebtPositionTypeService debtPositionTypeService;
    private final OrganizationService organizationServiceSpecialized;


    public DebtPositionTypeOrgProcessingService(
            DebtPositionTypeOrgMapper debtPositionTypeOrgMapper,
            DebtPositionTypeOrgErrorsArchiverService debtPositionTypeErrorsArchiverService,
            DebtPositionTypeOrgService debtPositionTypeOrgService, DebtPositionTypeService debtPositionTypeService,
            OrganizationService organizationServiceSpecialized,
            OrganizationService organizationServiceSuper) {
        super(debtPositionTypeErrorsArchiverService, organizationServiceSuper);
        this.debtPositionTypeOrgMapper = debtPositionTypeOrgMapper;
        this.debtPositionTypeOrgService = debtPositionTypeOrgService;
        this.debtPositionTypeService = debtPositionTypeService;
        this.organizationServiceSpecialized = organizationServiceSpecialized;
    }


    public DebtPositionTypeOrgIngestionFlowFileResult processDebtPositionTypeOrg(
            Iterator<DebtPositionTypeOrgIngestionFlowFileDTO> iterator,
            List<CsvException> readerException,
            IngestionFlowFile ingestionFlowFile,
            Path workingDirectory) {

        List<DebtPositionTypeOrgErrorDTO> errorList = new ArrayList<>();
        DebtPositionTypeOrgIngestionFlowFileResult ingestionFlowFileResult = new DebtPositionTypeOrgIngestionFlowFileResult();

        Organization organization = organizationServiceSpecialized.getOrganizationById(ingestionFlowFile.getOrganizationId()).orElse(null);
        Long brokerId = organization != null ? organization.getBrokerId() : null;
        if (brokerId == null) {
            log.error("Broker for organization id {} not found", ingestionFlowFile.getOrganizationId());
            ingestionFlowFileResult.setErrorDescription("Broker not found");
            return ingestionFlowFileResult;
        }
        ingestionFlowFileResult.setBrokerId(brokerId);
        ingestionFlowFileResult.setBrokerFiscalCode(organization.getOrgFiscalCode());

        process(iterator, readerException, ingestionFlowFileResult, ingestionFlowFile, errorList, workingDirectory);
        return ingestionFlowFileResult;
    }

    @Override
    protected boolean consumeRow(
            long lineNumber,
            @Valid  DebtPositionTypeOrgIngestionFlowFileDTO debtPositionTypeOrgDTO,
            DebtPositionTypeOrgIngestionFlowFileResult ingestionFlowFileResult,
            List<DebtPositionTypeOrgErrorDTO> errorList,
            IngestionFlowFile ingestionFlowFile) {

        try {
            DebtPositionTypeOrg existingDebtPosTypeOrg = debtPositionTypeOrgService.getDebtPositionTypeOrgByOrganizationIdAndCode(ingestionFlowFile.getOrganizationId(), debtPositionTypeOrgDTO.getCode());


            if (existingDebtPosTypeOrg != null) {
                DebtPositionTypeOrgErrorDTO error = new DebtPositionTypeOrgErrorDTO(
                        ingestionFlowFile.getFileName(),
                        debtPositionTypeOrgDTO.getCode(),
                        ingestionFlowFile.getOrganizationId(),
                        lineNumber,
                        "DEBT_POSITION_TYPE_ORG_ALREADY_EXISTS",
                        "Debt position type org already exists");
                errorList.add(error);
                return false;
            }


            List<DebtPositionType> debtPositionTypeList=debtPositionTypeService.getByBrokerIdAndCode(ingestionFlowFileResult.getBrokerId(),debtPositionTypeOrgDTO.getCode()).getEmbedded().getDebtPositionTypes();
            if (debtPositionTypeList.isEmpty()) {
                DebtPositionTypeOrgErrorDTO error = new DebtPositionTypeOrgErrorDTO(
                        ingestionFlowFile.getFileName(),
                        debtPositionTypeOrgDTO.getCode(),
                        ingestionFlowFile.getOrganizationId(),
                        lineNumber,
                        "DEBT_POSITION_TYPE_NOT_FOUND",
                        "Debt position type not found for code: " + debtPositionTypeOrgDTO.getCode());
                errorList.add(error);
                return false;
            }
            Long debtPositionTypeId= debtPositionTypeList.getFirst().getDebtPositionTypeId();

            debtPositionTypeOrgService.createDebtPositionTypeOrg(debtPositionTypeOrgMapper.map(debtPositionTypeOrgDTO, debtPositionTypeId, ingestionFlowFile.getOrganizationId()));
            return true;

        } catch (Exception e) {
            log.error("Error processing debt position type org with organization id:{} and type code {}: {}", ingestionFlowFile.getOrganizationId(), debtPositionTypeOrgDTO.getCode(), e.getMessage());
            DebtPositionTypeOrgErrorDTO error = new DebtPositionTypeOrgErrorDTO(
                    ingestionFlowFile.getFileName(),
                    debtPositionTypeOrgDTO.getCode(),
                    ingestionFlowFile.getOrganizationId(),
                    lineNumber,
                    "PROCESS_EXCEPTION",
                    e.getMessage());
            errorList.add(error);
            log.info("Current error list size after handleProcessingError: {}", errorList.size());
            return false;
        }
    }

    @Override
    protected DebtPositionTypeOrgErrorDTO buildErrorDto(String fileName, long lineNumber, String errorCode, String message) {
        return DebtPositionTypeOrgErrorDTO.builder()
                .fileName(fileName)
                .rowNumber(lineNumber)
                .errorCode(errorCode)
                .errorMessage(message)
                .build();
    }
}