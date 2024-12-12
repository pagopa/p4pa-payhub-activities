package it.gov.pagopa.payhub.activities.dto.treasury;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
public class IufIuvDTO implements Serializable {
  private String iuf;
  private String iuv;
}
