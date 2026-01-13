package it.gov.pagopa.payhub.activities.dto.ingestion.treasury.poste;

import com.opencsv.bean.CsvBindByPosition;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static it.gov.pagopa.payhub.activities.dto.ingestion.treasury.poste.TreasuryCsvPosteIngestionFlowFileVersions.V1_0;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreasuryPosteIngestionFlowFileDTO {

  @Size(max = 15)
  @CsvBindByPosition(position = 0, required = true, profiles = {V1_0})
  private String billDate;

  @Size(max = 15)
  @CsvBindByPosition(position = 1, profiles = {V1_0})
  private String regionValueDate;

  @Size(max = 3)
  @CsvBindByPosition(position = 2, profiles = {V1_0})
  private String remittanceCode;

  @CsvBindByPosition(position = 5, profiles = {V1_0})
  private BigDecimal debitBillAmount;

  @CsvBindByPosition(position = 6, profiles = {V1_0})
  private BigDecimal creditBillAmount;

  @Size(max = 255)
  @CsvBindByPosition(position = 8, profiles = {V1_0})
  private String remittanceDescription;

}
