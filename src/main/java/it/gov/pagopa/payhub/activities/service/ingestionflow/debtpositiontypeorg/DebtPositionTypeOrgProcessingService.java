package it.gov.pagopa.payhub.activities.service.ingestionflow.debtpositiontypeorg;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionTypeOrgService;
import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionTypeService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontypeorg.DebtPositionTypeOrgErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontypeorg.DebtPositionTypeOrgIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontypeorg.DebtPositionTypeOrgIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.enums.FileErrorCode;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.debtpositiontypeorg.DebtPositionTypeOrgMapper;
import it.gov.pagopa.payhub.activities.service.files.FileExceptionHandlerService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowProcessingService;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionType;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionTypeOrg;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.*;

@Service
@Lazy
@Slf4j
public class DebtPositionTypeOrgProcessingService extends IngestionFlowProcessingService<DebtPositionTypeOrgIngestionFlowFileDTO, DebtPositionTypeOrgIngestionFlowFileResult, DebtPositionTypeOrgErrorDTO> {

    private final DebtPositionTypeOrgMapper debtPositionTypeOrgMapper;
    private final DebtPositionTypeOrgService debtPositionTypeOrgService;
    private final DebtPositionTypeService debtPositionTypeService;
    private final OrganizationService organizationServiceSpecialized;

    public DebtPositionTypeOrgProcessingService(
            @Value("${ingestion-flow-files.dp-type-orgs.max-concurrent-processing-rows}") int maxConcurrentProcessingRows,

            DebtPositionTypeOrgMapper debtPositionTypeOrgMapper,
            DebtPositionTypeOrgErrorsArchiverService debtPositionTypeErrorsArchiverService,
            DebtPositionTypeOrgService debtPositionTypeOrgService, DebtPositionTypeService debtPositionTypeService,
            OrganizationService organizationServiceSpecialized,
            OrganizationService organizationServiceSuper,
            FileExceptionHandlerService fileExceptionHandlerService) {
        super(maxConcurrentProcessingRows, debtPositionTypeErrorsArchiverService, organizationServiceSuper, fileExceptionHandlerService);
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
        ingestionFlowFileResult.setFileVersion(ingestionFlowFile.getFileVersion());

        process(iterator, readerException, ingestionFlowFileResult, ingestionFlowFile, errorList, workingDirectory);
        return ingestionFlowFileResult;
    }

    @Override
    protected String getSequencingId(DebtPositionTypeOrgIngestionFlowFileDTO row) {
        return row.getCode();
    }

    @Override
    protected List<DebtPositionTypeOrgErrorDTO> consumeRow(
            long lineNumber,
            DebtPositionTypeOrgIngestionFlowFileDTO debtPositionTypeOrgDTO,
            DebtPositionTypeOrgIngestionFlowFileResult ingestionFlowFileResult,
            IngestionFlowFile ingestionFlowFile) {
        DebtPositionTypeOrg existingDebtPosTypeOrg = debtPositionTypeOrgService.getDebtPositionTypeOrgByOrganizationIdAndCode(ingestionFlowFile.getOrganizationId(), debtPositionTypeOrgDTO.getCode());


        if (existingDebtPosTypeOrg != null) {
            DebtPositionTypeOrgErrorDTO error = buildErrorDto(
                    ingestionFlowFile, lineNumber, debtPositionTypeOrgDTO,
                    FileErrorCode.DEBT_POSITION_TYPE_ORG_ALREADY_EXISTS.name(),
                    FileErrorCode.DEBT_POSITION_TYPE_ORG_ALREADY_EXISTS.getMessage());
            return List.of(error);
        }


        List<DebtPositionType> debtPositionTypeList = Objects.requireNonNull(debtPositionTypeService.getByBrokerIdAndCode(ingestionFlowFileResult.getBrokerId(), debtPositionTypeOrgDTO.getCode()).getEmbedded()).getDebtPositionTypes();
        if (CollectionUtils.isEmpty(debtPositionTypeList)) {
            DebtPositionTypeOrgErrorDTO error = buildErrorDto(
                    ingestionFlowFile, lineNumber, debtPositionTypeOrgDTO,
                    FileErrorCode.DEBT_POSITION_TYPE_BY_CODE_NOT_FOUND.name(),
                    FileErrorCode.DEBT_POSITION_TYPE_BY_CODE_NOT_FOUND.format(debtPositionTypeOrgDTO.getCode()));
            return List.of(error);
        }
        Long debtPositionTypeId = debtPositionTypeList.getFirst().getDebtPositionTypeId();

        debtPositionTypeOrgService.createDebtPositionTypeOrg(debtPositionTypeOrgMapper.map(debtPositionTypeOrgDTO, debtPositionTypeId, ingestionFlowFile.getOrganizationId()));
        return Collections.emptyList();
    }

    @Override
    protected DebtPositionTypeOrgErrorDTO buildErrorDto(IngestionFlowFile ingestionFlowFile, long lineNumber, DebtPositionTypeOrgIngestionFlowFileDTO row, String errorCode, String message) {
        DebtPositionTypeOrgErrorDTO errorDTO = DebtPositionTypeOrgErrorDTO.builder()
                .fileName(ingestionFlowFile.getFileName())
                .rowNumber(lineNumber)
                .errorCode(errorCode)
                .errorMessage(message)
                .build();
        if (row != null) {
            errorDTO.setDebtPositionTypeCode(row.getCode());
            errorDTO.setOrganizationId(ingestionFlowFile.getOrganizationId());
        }
        return errorDTO;
    }
}