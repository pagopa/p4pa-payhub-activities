package it.gov.pagopa.payhub.activities.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationTypeInstallmentDTO implements Serializable {

    private Long orgInstallmentTypeId;
    private Long orgId;
    private String ipaCode;
    private String orgName;
    private String orgThumbLogo;
    private String orgHashThumbLogo;
    private String typeCode;
    private String typeDesc;
    private String installmentPaymentUrl;
    private boolean flagAnonymousFiscalCode;
    private boolean flagMandatoryDueDate;
    private boolean flagActive;
    private String amount;
    private LocalDateTime lastEnablingDate;
    private LocalDateTime lastDisablingDate;
}
