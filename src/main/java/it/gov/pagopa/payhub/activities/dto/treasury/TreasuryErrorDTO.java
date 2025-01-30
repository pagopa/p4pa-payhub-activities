package it.gov.pagopa.payhub.activities.dto.treasury;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
public class TreasuryErrorDTO implements Serializable {
  private String fileName;
  private String billYear;
  private String billCode;
  private String errorCode;
  private String errorMessage;
}
