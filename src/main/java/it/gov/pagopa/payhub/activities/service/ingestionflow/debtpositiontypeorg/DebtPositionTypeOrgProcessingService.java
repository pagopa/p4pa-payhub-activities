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
import it.gov.pagopa.payhub.activities.service.ingestionflow.spontaneousform.SpontaneousFormHandlerService;
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
    private final SpontaneousFormHandlerService spontaneousFormHandlerService;

    public DebtPositionTypeOrgProcessingService(
        @Value("${ingestion-flow-files.dp-type-orgs.max-concurrent-processing-rows}") int maxConcurrentProcessingRows,

        DebtPositionTypeOrgMapper debtPositionTypeOrgMapper,
        DebtPositionTypeOrgErrorsArchiverService debtPositionTypeErrorsArchiverService,
        DebtPositionTypeOrgService debtPositionTypeOrgService, DebtPositionTypeService debtPositionTypeService,
        OrganizationService organizationService,
        SpontaneousFormHandlerService spontaneousFormHandlerService,
        FileExceptionHandlerService fileExceptionHandlerService) {
        super(maxConcurrentProcessingRows, debtPositionTypeErrorsArchiverService, organizationService, fileExceptionHandlerService);
        this.debtPositionTypeOrgMapper = debtPositionTypeOrgMapper;
        this.debtPositionTypeOrgService = debtPositionTypeOrgService;
        this.debtPositionTypeService = debtPositionTypeService;
        this.spontaneousFormHandlerService = spontaneousFormHandlerService;
    }

    public DebtPositionTypeOrgIngestionFlowFileResult processDebtPositionTypeOrg(
        Iterator<DebtPositionTypeOrgIngestionFlowFileDTO> iterator,
        List<CsvException> readerException,
        IngestionFlowFile ingestionFlowFile,
        Path workingDirectory,
        DebtPositionTypeOrgIngestionFlowFileResult result) {

        List<DebtPositionTypeOrgErrorDTO> errorList = new ArrayList<>();

        Organization organization = getOrganizationById(ingestionFlowFile.getOrganizationId());
        Long brokerId = organization.getBrokerId();
        if (brokerId == null) {
            log.error("Broker for organization id {} not found", ingestionFlowFile.getOrganizationId());
            result.setErrorDescription(FileErrorCode.BROKER_NOT_FOUND.getMessage());
            return result;
        }
        result.setBrokerId(brokerId);
        result.setOrgIpaCode(organization.getIpaCode());

        process(iterator, readerException, result, ingestionFlowFile, errorList, workingDirectory);
        return result;
    }

    @Override
    protected String getSequencingId(DebtPositionTypeOrgIngestionFlowFileDTO row) {
        return row.getCode();
    }

    @Override
    protected List<DebtPositionTypeOrgErrorDTO> consumeRow(
        long lineNumber,
        DebtPositionTypeOrgIngestionFlowFileDTO row,
        DebtPositionTypeOrgIngestionFlowFileResult ingestionFlowFileResult,
        IngestionFlowFile ingestionFlowFile) {
	    String ipa = ingestionFlowFileResult.getOrgIpaCode();
	    if (!row.getIpaCode().equalsIgnoreCase(ipa)) {
		    log.error("Organization IPA code {} does not match with the one in the ingestion flow file {}", row.getIpaCode(), ipa);
		    DebtPositionTypeOrgErrorDTO error = buildErrorDto(
			    ingestionFlowFile, lineNumber, row,
			    FileErrorCode.ORGANIZATION_IPA_MISMATCH.name(),
			    FileErrorCode.ORGANIZATION_IPA_MISMATCH.format(row.getIpaCode(), ipa));
		    return List.of(error);
	    }

	    DebtPositionTypeOrg existingDebtPosTypeOrg = debtPositionTypeOrgService.getDebtPositionTypeOrgByOrganizationIdAndCode(ingestionFlowFile.getOrganizationId(), row.getCode());

	    if (existingDebtPosTypeOrg != null) {
		    DebtPositionTypeOrgErrorDTO error = buildErrorDto(
			    ingestionFlowFile, lineNumber, row,
			    FileErrorCode.DEBT_POSITION_TYPE_ORG_ALREADY_EXISTS.name(),
			    FileErrorCode.DEBT_POSITION_TYPE_ORG_ALREADY_EXISTS.getMessage());
		    return List.of(error);
	    }

	    List<DebtPositionType> debtPositionTypeList = Objects.requireNonNull(debtPositionTypeService.getByBrokerIdAndCode(ingestionFlowFileResult.getBrokerId(), row.getCode()).getEmbedded()).getDebtPositionTypes();
	    if (CollectionUtils.isEmpty(debtPositionTypeList)) {
		    DebtPositionTypeOrgErrorDTO error = buildErrorDto(
			    ingestionFlowFile, lineNumber, row,
			    FileErrorCode.DEBT_POSITION_TYPE_BY_CODE_NOT_FOUND.name(),
			    FileErrorCode.DEBT_POSITION_TYPE_BY_CODE_NOT_FOUND.format(row.getCode()));
		    return List.of(error);
	    }
	    Long debtPositionTypeId = debtPositionTypeList.getFirst().getDebtPositionTypeId();


	    Long spontaneousFormId;
	    try {
		    spontaneousFormId = spontaneousFormHandlerService.handleSpontaneousForm(
			    ingestionFlowFile.getOrganizationId(), row);
	    } catch (Exception e) {
		    DebtPositionTypeOrgErrorDTO error = buildErrorDto(
			    ingestionFlowFile, lineNumber, row,
			    FileErrorCode.SPONTANEOUS_FORM_PARSING_ERROR.name(),
			    FileErrorCode.SPONTANEOUS_FORM_PARSING_ERROR.format(row.getSpontaneousFormCode()));
		    return List.of(error);
	    }

	    debtPositionTypeOrgService.createDebtPositionTypeOrg(debtPositionTypeOrgMapper.map(row, debtPositionTypeId, ingestionFlowFile.getOrganizationId(), spontaneousFormId));
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
