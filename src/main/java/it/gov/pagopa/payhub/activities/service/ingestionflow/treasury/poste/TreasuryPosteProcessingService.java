package it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.poste;

import static it.gov.pagopa.payhub.activities.mapper.ingestionflow.treasury.poste.TreasuryPosteMapper.POSTE_DATE_FORMAT;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.classification.TreasuryService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.TreasuryIufIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.poste.TreasuryPosteErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.poste.TreasuryPosteIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.poste.TreasuryPosteIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuf;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.treasury.poste.TreasuryPosteMapper;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowProcessingService;
import it.gov.pagopa.payhub.activities.util.TreasuryUtils;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Lazy
@Slf4j
public class TreasuryPosteProcessingService extends IngestionFlowProcessingService<TreasuryPosteIngestionFlowFileDTO, TreasuryIufIngestionFlowFileResult, TreasuryPosteErrorDTO> {

  private final TreasuryPosteMapper treasuryPosteMapper;
  private final TreasuryService treasuryService;

  public TreasuryPosteProcessingService(
      TreasuryPosteMapper treasuryPosteMapper,
      TreasuryService treasuryService,
      TreasuryPosteErrorsArchiverService treasuryPosteErrorsArchiverService,
      OrganizationService organizationService) {
    super(treasuryPosteErrorsArchiverService, organizationService);
    this.treasuryPosteMapper = treasuryPosteMapper;
    this.treasuryService = treasuryService;
  }

  public TreasuryIufIngestionFlowFileResult processTreasuryPoste(
      Iterator<TreasuryPosteIngestionFlowFileDTO> iterator,
      String iban,
      List<CsvException> readerException,
      IngestionFlowFile ingestionFlowFile, Path workingDirectory) {
    List<TreasuryPosteErrorDTO> errorList = new ArrayList<>();
    TreasuryPosteIngestionFlowFileResult ingestionFlowFileResult = new TreasuryPosteIngestionFlowFileResult();
    ingestionFlowFileResult.setOrganizationId(ingestionFlowFile.getOrganizationId());
    ingestionFlowFileResult.setIban(iban);
    ingestionFlowFileResult.setIuf2TreasuryIdMap(new HashMap<>());

    process(iterator, readerException, ingestionFlowFileResult, ingestionFlowFile, errorList, workingDirectory);
    return ingestionFlowFileResult;
  }

  @Override
  protected boolean consumeRow(long lineNumber, TreasuryPosteIngestionFlowFileDTO row, TreasuryIufIngestionFlowFileResult ingestionFlowFileResult, List<TreasuryPosteErrorDTO> errorList,
      IngestionFlowFile ingestionFlowFile) {
    String iuf = TreasuryUtils.getIdentificativo(row.getRemittanceDescription(), TreasuryUtils.IUF);

    LocalDate billDate = LocalDate.parse(row.getBillDate(), POSTE_DATE_FORMAT);
    String billCode = TreasuryUtils.generateBillCode(iuf);
    String billYear = String.valueOf(billDate.getYear());

    TreasuryPosteIngestionFlowFileResult treasuryPosteIngestionFlowFileResult = (TreasuryPosteIngestionFlowFileResult) ingestionFlowFileResult;
    String iban = treasuryPosteIngestionFlowFileResult.getIban();

    try {
      TreasuryIuf existingTreasury = treasuryService.getByOrganizationIdAndIuf(treasuryPosteIngestionFlowFileResult.getOrganizationId(), iuf);

      if (existingTreasury != null) {
        boolean treasuryMatch = !existingTreasury.getBillCode().equals(billCode) || !existingTreasury.getBillYear().equals(billYear);
        if (treasuryMatch) {
          String errorMessage = String.format(
              "IUF %s already associated to another treasury for organization with IPA code %s",
              iuf, iban);
          log.error(errorMessage);
          TreasuryPosteErrorDTO error = new TreasuryPosteErrorDTO(
              ingestionFlowFile.getFileName(), iuf,
              lineNumber, "IUF_ALREADY_ASSOCIATED", errorMessage);
          errorList.add(error);
          return false;
        }
      }

      Treasury treasury = treasuryService.insert(
          treasuryPosteMapper.map(row, iban, iuf, billCode, billDate, ingestionFlowFile));

      treasuryPosteIngestionFlowFileResult.getIuf2TreasuryIdMap().put(treasury.getIuf(), treasury.getTreasuryId());
      return true;
    } catch (Exception e) {
      log.error("Error processing treasury poste with iuf {}: {}",
          iuf,
          e.getMessage());
      TreasuryPosteErrorDTO error = new TreasuryPosteErrorDTO(
          ingestionFlowFile.getFileName(), iuf,
          lineNumber, "PROCESS_EXCEPTION", e.getMessage());
      errorList.add(error);
      log.info("Current error list size after handleProcessingError: {}", errorList.size());
      return false;
    }
  }

  @Override
  protected TreasuryPosteErrorDTO buildErrorDto(String fileName, long lineNumber, String errorCode, String message) {
    return TreasuryPosteErrorDTO.builder()
        .fileName(fileName)
        .rowNumber(lineNumber)
        .errorCode(errorCode)
        .errorMessage(message)
        .build();
  }
}