package it.gov.pagopa.payhub.activities.dto.ingestion.sendnotification;

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
public class SendNotificationErrorDTO extends ErrorFileDTO {
  private Long rowNumber;

  public SendNotificationErrorDTO(String fileName, Long rowNumber, String errorCode, String errorMessage) {
    super(fileName, errorCode, errorMessage);
    this.rowNumber = rowNumber;
  }

  @Override
  public String[] toCsvRow() {
    return new String[]{
        getFileName(),
        rowNumber.toString(),
        getErrorCode(), getErrorMessage()
    };
  }
}
