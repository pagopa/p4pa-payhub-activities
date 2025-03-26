package it.gov.pagopa.payhub.activities.dto.treasury;

import it.gov.pagopa.payhub.activities.dto.ErrorFileDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class TreasuryErrorFileDTO extends ErrorFileDTO {

  private String billYear;
  private String billCode;

  public TreasuryErrorFileDTO(String fileName, String billYear, String billCode, String errorCode, String errorMessage) {
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


