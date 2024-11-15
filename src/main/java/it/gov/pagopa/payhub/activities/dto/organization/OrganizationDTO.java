package it.gov.pagopa.payhub.activities.dto.organization;

import it.gov.pagopa.payhub.activities.dto.RegistryStatusDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

@Data
@Builder(toBuilder=true)
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationDTO implements Serializable {

    private Long orgId;
    private String ipaCode;
    private String orgFiscalCode;
    private String orgName;
    private String adminEmail;
    private Timestamp creationDate;
    private Timestamp lastChangeDate;
    private String paymentTypeCode;
    private BigDecimal numPAChargeCommittee;
    private String creditIban;
    private String creditBic;
    private String backingIban;
    private String backingBic;
    private String myBoxClientKey;
    private String myBoxClientSecret;
    private String urlOrgSendSILPaymentResult;
    private String codeGlobalLocationNumber;
    private String password;
    private Boolean creditBicSeller;
    private String beneficiaryOrgName;
    private String beneficiaryOrgAddress;
    private String beneficiaryOrgCivic;
    private String beneficiaryOrgPostalCode;
    private String beneficiaryOrgLocation;
    private String beneficiaryOrgProvince;
    private String beneficiaryOrgNation;
    private String beneficiaryOrgPhoneNumber;
    private String beneficiaryOrgWebSite;
    private String beneficiaryOrgEmail;
    private String applicationCode;
    private String cbillInterbankCode;
    private String orgInformation;
    private String orgLogoDesc;
    private String authorizationDesc;
    private String status;
    private String urlActiveExternal;
    private String additionalLanguage;
    private String orgTypeCode;
    private LocalDate startDate;
    private Long brokerId;
}