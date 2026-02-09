package it.gov.pagopa.payhub.activities.dto.ingestion.sendnotification;

import it.gov.pagopa.payhub.activities.dto.ErrorFileDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@ToString(callSuper = true)
public class SendNotificationErrorDTO extends ErrorFileDTO {

  public SendNotificationErrorDTO(String fileName, Long rowNumber, String errorCode, String errorMessage) {
    super(fileName, rowNumber, errorCode, errorMessage);
  }

  @Override
  public String[] toCsvRow() {
    return new String[]{
        getFileName(),
        getRowNumber().toString(),
        getErrorCode(), getErrorMessage()
    };
  }
}
