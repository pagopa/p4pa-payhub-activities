package it.gov.pagopa.payhub.activities.dto.ingestion.treasury.poste;

import com.opencsv.bean.CsvBindByPosition;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreasuryPosteIngestionFlowFileDTO {

  @NotNull
  @Size(max = 15)
  @CsvBindByPosition(position = 0)
  private String billDate;

  @Size(max = 15)
  @CsvBindByPosition(position = 1)
  private String regionValueDate;

  @Size(max = 3)
  @CsvBindByPosition(position = 2)
  private String remittanceCode;

  @CsvBindByPosition(position = 5)
  private Long debitBillAmountCents;

  @CsvBindByPosition(position = 6)
  private Long creditBillAmountCents;

  @Size(max = 255)
  @CsvBindByPosition(position = 8)
  private String remittanceDescription;

}
