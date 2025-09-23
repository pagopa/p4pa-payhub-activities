package it.gov.pagopa.payhub.activities.mapper.ingestionflow.treasury.poste;

import static it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.TreasuryVersionBaseHandlerService.ORG_BT_CODE_DEFAULT;
import static it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.TreasuryVersionBaseHandlerService.ORG_ISTAT_CODE_DEFAULT;

import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.poste.TreasuryPosteIngestionFlowFileDTO;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.pu.classification.dto.generated.TreasuryOrigin;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;

@Service
public class TreasuryPosteMapper {

  public static final String POSTE_PSP_LAST_NAME = "POSTE ITALIANE SPA";

  public Treasury map(TreasuryPosteIngestionFlowFileDTO dto, String iban, LocalDate billDate, IngestionFlowFile ingestionFlowFile) {
    LocalDate regionValueDate = null;
    if (dto.getRegionValueDate() != null) {
      regionValueDate = LocalDate.parse(dto.getRegionValueDate(), DateTimeFormatter.ISO_LOCAL_DATE);
    }

    return Treasury.builder()
        .accountCode(iban.substring(20))
        .billDate(billDate)
        .regionValueDate(regionValueDate)
        .remittanceCode(dto.getRemittanceCode())
        .billAmountCents(dto.getDebitBillAmountCents() != null ? dto.getDebitBillAmountCents() : dto.getCreditBillAmountCents())
        .remittanceDescription(dto.getRemittanceDescription())
        .iuf(dto.getIuf())
        .pspLastName(POSTE_PSP_LAST_NAME)
        .billCode(dto.getBillCode())
        .billYear(dto.getBillYear())
        .orgIstatCode(ORG_ISTAT_CODE_DEFAULT)
        .orgBtCode(ORG_BT_CODE_DEFAULT)
        .organizationId(ingestionFlowFile.getOrganizationId())
        .ingestionFlowFileId(ingestionFlowFile.getIngestionFlowFileId())
        .treasuryOrigin(TreasuryOrigin.TREASURY_POSTE)
        .build();
  }

}

