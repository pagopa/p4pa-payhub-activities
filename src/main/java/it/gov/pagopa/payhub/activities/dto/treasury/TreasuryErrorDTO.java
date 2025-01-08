package it.gov.pagopa.payhub.activities.dto.treasury;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
public class TreasuryErrorDTO implements Serializable {
  private String nomeFile;
  private String deAnnoBolletta;
  private String codBolletta;
  private String errorCode;
  private String errorMessage;
}
