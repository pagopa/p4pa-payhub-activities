package it.gov.pagopa.payhub.activities.dto.treasury;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * DTO for the TreasuryDto
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TreasuryDTO {
  private Long treasuryId;
  private String billYear;
  private String billCode;
  private String accountCode;
  private String domainIdCode;
  private String transactionTypeCode;
  private String remittanceCode;
  private String remittanceInformation;
  private BigDecimal billIpNumber;
  private Date billDate;
  private Date receptionDate;
  private String documentYear;
  private String documentCode;
  private String sealCode;
  private String lastName;
  private String firstName;
  private String address;
  private String postalCode;
  private String city;
  private String fiscalCode;
  private String vatNumber;
  private String abiCode;
  private String cabCode;
  private String accountRegistryCode;
  private String provisionalAe;
  private String provisionalCode;
  private String ibanCode;
  private Character accountTypeCode;
  private String processCode;
  private String executionPgCode;
  private String transferPgCode;
  private Long processPgNumber;
  private Date regionValueDate;
  private Long organizationId;
  private String flowIdentifierCode;
  private String iuv;
  private Date creationDate;
  private Date lastUpdateDate;
  private boolean isRegularized;
  private Long ingestionFlowFileId;
  private Date actualSuspensionDate;
  private String managementProvisionalCode;
  private String endToEndId;

  private byte[] taxCodeHash;
  private byte[] vatNumberHash;
  private byte[] lastNameHash;

  private Long personalDataId;
}
