package it.gov.pagopa.payhub.activities.dto.ingestion.treasury.poste;

import it.gov.pagopa.payhub.activities.dto.ErrorFileDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class TreasuryPosteErrorDTO extends ErrorFileDTO {

  private String iuf;

  public TreasuryPosteErrorDTO(String fileName, String iuf, Long rowNumber, String errorCode, String errorMessage) {
    super(fileName, rowNumber, errorCode, errorMessage);
    this.iuf = iuf;
  }

  @Override
  public String[] toCsvRow() {
    return new String[]{
        getFileName(), iuf,
        getRowNumber().toString(),
        getErrorCode(), getErrorMessage()
    };
  }
}
