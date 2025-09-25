package it.gov.pagopa.payhub.activities.mapper.ingestionflow.treasury.poste;

import static it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.TreasuryVersionBaseHandlerService.ORG_BT_CODE_DEFAULT;
import static it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.TreasuryVersionBaseHandlerService.ORG_ISTAT_CODE_DEFAULT;
import static it.gov.pagopa.payhub.activities.util.Utilities.bigDecimalEuroToLongCentsAmount;

import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.poste.TreasuryPosteIngestionFlowFileDTO;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.pu.classification.dto.generated.TreasuryOrigin;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;

@Service
public class TreasuryPosteMapper {
  public static final String POSTE_PSP_LAST_NAME = "POSTE ITALIANE SPA";
  public static final DateTimeFormatter POSTE_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

  public Treasury map(TreasuryPosteIngestionFlowFileDTO dto, String iban, String iuf, String billCode, LocalDate billDate, IngestionFlowFile ingestionFlowFile) {
    LocalDate regionValueDate = null;
    if (dto.getRegionValueDate() != null) {
      regionValueDate = LocalDate.parse(dto.getRegionValueDate(), POSTE_DATE_FORMAT);
    }

    return Treasury.builder()
        .accountCode(iban.substring(20))
        .billDate(billDate)
        .regionValueDate(regionValueDate)
        .remittanceCode(dto.getRemittanceCode())
        .billAmountCents(getBillAmountCents(dto))
        .remittanceDescription(dto.getRemittanceDescription())
        .iuf(iuf)
        .pspLastName(POSTE_PSP_LAST_NAME)
        .billCode(billCode)
        .billYear(String.valueOf(billDate.getYear()))
        .orgIstatCode(ORG_ISTAT_CODE_DEFAULT)
        .orgBtCode(ORG_BT_CODE_DEFAULT)
        .organizationId(ingestionFlowFile.getOrganizationId())
        .ingestionFlowFileId(ingestionFlowFile.getIngestionFlowFileId())
        .treasuryOrigin(TreasuryOrigin.TREASURY_POSTE)
        .build();
  }

  private Long getBillAmountCents(TreasuryPosteIngestionFlowFileDTO dto) {
    BigDecimal billAmount = dto.getDebitBillAmount() != null ? dto.getDebitBillAmount().negate() : dto.getCreditBillAmount();
    return bigDecimalEuroToLongCentsAmount(billAmount);
  }

}

