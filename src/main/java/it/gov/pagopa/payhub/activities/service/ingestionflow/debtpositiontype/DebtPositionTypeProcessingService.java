package it.gov.pagopa.payhub.activities.service.ingestionflow.debtpositiontype;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionTypeService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontype.DebtPositionTypeErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontype.DebtPositionTypeIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.debtpositiontype.DebtPositionTypeIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.debtpositiontype.DebtPositionTypeMapper;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowProcessingService;
import it.gov.pagopa.pu.debtposition.dto.generated.CollectionModelDebtPositionType;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionType;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Lazy
@Slf4j
public class DebtPositionTypeProcessingService extends
    IngestionFlowProcessingService<DebtPositionTypeIngestionFlowFileDTO, DebtPositionTypeIngestionFlowFileResult, DebtPositionTypeErrorDTO> {

  private final DebtPositionTypeMapper debtPositionTypeMapper;
  private final DebtPositionTypeService debtPositionTypeService;
  private final OrganizationService organizationService;


  public DebtPositionTypeProcessingService(
      DebtPositionTypeMapper debtPositionTypeMapper,
      DebtPositionTypeErrorsArchiverService debtPositionTypeErrorsArchiverService,
      DebtPositionTypeService debtPositionTypeService, OrganizationService organizationService) {
    super(debtPositionTypeErrorsArchiverService);
    this.debtPositionTypeMapper = debtPositionTypeMapper;
    this.debtPositionTypeService = debtPositionTypeService;
    this.organizationService = organizationService;
  }


  public DebtPositionTypeIngestionFlowFileResult processDebtPositionType(
      Iterator<DebtPositionTypeIngestionFlowFileDTO> iterator,
      List<CsvException> readerException,
      IngestionFlowFile ingestionFlowFile, Path workingDirectory) {

    List<DebtPositionTypeErrorDTO> errorList = new ArrayList<>();
    DebtPositionTypeIngestionFlowFileResult ingestionFlowFileResult = new DebtPositionTypeIngestionFlowFileResult();
    ingestionFlowFileResult.setDebtPositionTypeCodeList(new ArrayList<>());

    Optional<Organization> organizationBroker = organizationService.getOrganizationById(
        ingestionFlowFile.getOrganizationId());
    Organization organization = organizationBroker.orElse(null);
    Long brokerId = organization != null ? organization.getBrokerId() : null;
    if (brokerId == null) {
      log.error("Broker for organization id {} not found", ingestionFlowFile.getOrganizationId());
      ingestionFlowFileResult.setErrorDescription("Broker not found");
      return ingestionFlowFileResult;
    }
    ingestionFlowFileResult.setBrokerId(brokerId);
    ingestionFlowFileResult.setBrokerFiscalCode(organization.getOrgFiscalCode());

    process(iterator, readerException, ingestionFlowFileResult, ingestionFlowFile, errorList,
        workingDirectory);
    return ingestionFlowFileResult;
  }

  @Override
  protected boolean consumeRow(long lineNumber,
      DebtPositionTypeIngestionFlowFileDTO debtPositionTypeDTO,
      DebtPositionTypeIngestionFlowFileResult ingestionFlowFileResult,
      List<DebtPositionTypeErrorDTO> errorList, IngestionFlowFile ingestionFlowFile) {
    try {
      CollectionModelDebtPositionType existingDebtPosType = debtPositionTypeService.getByMainFields(
          debtPositionTypeDTO.getDebtPositionTypeCode(),
          ingestionFlowFileResult.getBrokerId(),
          debtPositionTypeDTO.getOrgType(),
          debtPositionTypeDTO.getMacroArea(),
          debtPositionTypeDTO.getServiceType(),
          debtPositionTypeDTO.getCollectingReason(),
          debtPositionTypeDTO.getTaxonomyCode());

      List<DebtPositionType> debtPositionTypeList = new ArrayList<>();
      if (existingDebtPosType != null && existingDebtPosType.getEmbedded() != null && existingDebtPosType.getEmbedded().getDebtPositionTypes() != null) {
        debtPositionTypeList = existingDebtPosType.getEmbedded().getDebtPositionTypes();
      }

      if (!debtPositionTypeList.isEmpty()) {
        DebtPositionTypeErrorDTO error = new DebtPositionTypeErrorDTO(
            ingestionFlowFile.getFileName(), debtPositionTypeDTO.getDebtPositionTypeCode(),
            debtPositionTypeDTO.getBrokerCf(), lineNumber, "DEBT_POSITION_TYPE_ALREADY_EXISTS",
            "Debt position type already exists");
        errorList.add(error);
        return false;
      }

      DebtPositionType debtPositionTypeCreated = debtPositionTypeService.createDebtPositionType(
          debtPositionTypeMapper.map(debtPositionTypeDTO, ingestionFlowFileResult.getBrokerId()));
      ingestionFlowFileResult.getDebtPositionTypeCodeList().add(debtPositionTypeCreated.getCode());
      return true;

    } catch (Exception e) {
      log.error("Error processing debt position type with type code {}: {}",
          debtPositionTypeDTO.getDebtPositionTypeCode(), e.getMessage());
      DebtPositionTypeErrorDTO error = new DebtPositionTypeErrorDTO(
          ingestionFlowFile.getFileName(), debtPositionTypeDTO.getDebtPositionTypeCode(),
          debtPositionTypeDTO.getBrokerCf(),
          lineNumber, "PROCESS_EXCEPTION", e.getMessage());
      errorList.add(error);
      log.info("Current error list size after handleProcessingError: {}", errorList.size());
      return false;
    }
  }

  @Override
  protected DebtPositionTypeErrorDTO buildErrorDto(String fileName, long lineNumber,
      String errorCode, String message) {
    return DebtPositionTypeErrorDTO.builder()
        .fileName(fileName)
        .rowNumber(lineNumber)
        .errorCode(errorCode)
        .errorMessage(message)
        .build();
  }
}

