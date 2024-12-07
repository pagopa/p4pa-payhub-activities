package it.gov.pagopa.payhub.activities.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class UserInfoDTO {
  private String userId;
  private String mappedExternalUserId;
  private String fiscalCode;
  private String familyName;
  private String name;
  private String email;
  private String issuer;
  private String organizationAccess;

}

