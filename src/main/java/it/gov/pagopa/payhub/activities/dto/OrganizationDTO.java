package it.gov.pagopa.payhub.activities.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
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
    private Instant creationDate;
    private Instant lastUpdateDate;
    private Long fee;
    private String iban;
    private String urlOrgSendSILPaymentResult;
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