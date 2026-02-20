package it.gov.pagopa.payhub.activities.service.ingestionflow.debtpositiontype;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionTypeService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontype.DebtPositionTypeErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontype.DebtPositionTypeIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontype.DebtPositionTypeIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.enums.FileErrorCode;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.debtpositiontype.DebtPositionTypeMapper;
import it.gov.pagopa.payhub.activities.service.files.FileExceptionHandlerService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowProcessingService;
import it.gov.pagopa.pu.debtposition.dto.generated.CollectionModelDebtPositionType;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionType;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Service
@Lazy
@Slf4j
public class DebtPositionTypeProcessingService extends
        IngestionFlowProcessingService<DebtPositionTypeIngestionFlowFileDTO, DebtPositionTypeIngestionFlowFileResult, DebtPositionTypeErrorDTO> {

    private final DebtPositionTypeMapper debtPositionTypeMapper;
    private final DebtPositionTypeService debtPositionTypeService;

    public DebtPositionTypeProcessingService(
            @Value("${ingestion-flow-files.dp-types.max-concurrent-processing-rows}") int maxConcurrentProcessingRows,

            DebtPositionTypeMapper debtPositionTypeMapper,
            DebtPositionTypeErrorsArchiverService debtPositionTypeErrorsArchiverService,
            DebtPositionTypeService debtPositionTypeService, OrganizationService organizationService,
            FileExceptionHandlerService fileExceptionHandlerService) {
        super(maxConcurrentProcessingRows, debtPositionTypeErrorsArchiverService, organizationService, fileExceptionHandlerService);
        this.debtPositionTypeMapper = debtPositionTypeMapper;
        this.debtPositionTypeService = debtPositionTypeService;
    }


    public DebtPositionTypeIngestionFlowFileResult processDebtPositionType(
            Iterator<DebtPositionTypeIngestionFlowFileDTO> iterator,
            List<CsvException> readerException,
            IngestionFlowFile ingestionFlowFile,
            Path workingDirectory,
            DebtPositionTypeIngestionFlowFileResult result) {

        List<DebtPositionTypeErrorDTO> errorList = new ArrayList<>();

        Organization organization = getOrganizationById(ingestionFlowFile.getOrganizationId());
        Long brokerId = organization.getBrokerId();
        if (brokerId == null) {
            log.error("Broker for organization id {} not found", ingestionFlowFile.getOrganizationId());
            result.setErrorDescription(FileErrorCode.BROKER_NOT_FOUND.getMessage());
            return result;
        }
        result.setBrokerId(brokerId);
        result.setBrokerFiscalCode(organization.getOrgFiscalCode());

        process(iterator, readerException, result, ingestionFlowFile, errorList, workingDirectory);
        return result;
    }

    @Override
    protected String getSequencingId(DebtPositionTypeIngestionFlowFileDTO row) {
        return row.getDebtPositionTypeCode();
    }

    @Override
    protected List<DebtPositionTypeErrorDTO> consumeRow(long lineNumber,
                                                        DebtPositionTypeIngestionFlowFileDTO debtPositionTypeDTO,
                                                        DebtPositionTypeIngestionFlowFileResult ingestionFlowFileResult,
                                                        IngestionFlowFile ingestionFlowFile) {
        CollectionModelDebtPositionType existingDebtPosType = debtPositionTypeService.getByMainFields(
                debtPositionTypeDTO.getDebtPositionTypeCode(),
                ingestionFlowFileResult.getBrokerId(),
                debtPositionTypeDTO.getOrgType(),
                debtPositionTypeDTO.getMacroArea(),
                debtPositionTypeDTO.getServiceType(),
                debtPositionTypeDTO.getCollectingReason(),
                debtPositionTypeDTO.getTaxonomyCode());

        List<DebtPositionType> debtPositionTypeList = null;
        if (existingDebtPosType != null && existingDebtPosType.getEmbedded() != null && existingDebtPosType.getEmbedded().getDebtPositionTypes() != null) {
            debtPositionTypeList = existingDebtPosType.getEmbedded().getDebtPositionTypes();
        }

        if (!CollectionUtils.isEmpty(debtPositionTypeList)) {
            DebtPositionTypeErrorDTO error = buildErrorDto(
                    ingestionFlowFile, lineNumber, debtPositionTypeDTO,
                    FileErrorCode.DEBT_POSITION_TYPE_ALREADY_EXISTS.name(),
                    FileErrorCode.DEBT_POSITION_TYPE_ALREADY_EXISTS.getMessage());
            return List.of(error);
        }

        debtPositionTypeService.createDebtPositionType(debtPositionTypeMapper.map(debtPositionTypeDTO, ingestionFlowFileResult.getBrokerId()));
        return Collections.emptyList();
    }

    @Override
    protected DebtPositionTypeErrorDTO buildErrorDto(IngestionFlowFile ingestionFlowFile, long lineNumber, DebtPositionTypeIngestionFlowFileDTO row,
                                                     String errorCode, String message) {
        DebtPositionTypeErrorDTO errorDTO = DebtPositionTypeErrorDTO.builder()
                .fileName(ingestionFlowFile.getFileName())
                .rowNumber(lineNumber)
                .errorCode(errorCode)
                .errorMessage(message)
                .build();
        if (row != null) {
            errorDTO.setDebtPositionTypeCode(row.getDebtPositionTypeCode());
            errorDTO.setBrokerCf(row.getBrokerCf());
        }
        return errorDTO;
    }
}

