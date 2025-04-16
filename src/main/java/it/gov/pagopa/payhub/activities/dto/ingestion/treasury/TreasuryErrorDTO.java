package it.gov.pagopa.payhub.activities.dto.ingestion.treasury;

import it.gov.pagopa.payhub.activities.dto.ErrorFileDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class TreasuryErrorDTO extends ErrorFileDTO {

  private String billYear;
  private String billCode;

  public TreasuryErrorDTO(String fileName, String billYear, String billCode, String errorCode, String errorMessage) {
    super(fileName, errorCode, errorMessage);
    this.billYear = billYear;
    this.billCode = billCode;
  }

  @Override
  public String[] toCsvRow() {
    return new String[]{
            getFileName(), billYear, billCode, getErrorCode(), getErrorMessage()
    };
  }
}


