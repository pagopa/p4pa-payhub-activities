package it.gov.pagopa.payhub.activities.utility.faker;

import it.gov.pagopa.payhub.activities.dto.OrganizationDTO;

import java.time.LocalDate;

import static it.gov.pagopa.payhub.activities.utility.TestUtils.DATE;

public class OrganizationFaker {

    public static OrganizationDTO buildOrganizationDTO() {
        return OrganizationDTO.builder()
                .orgId(1L)
                .ipaCode("ipaCode")
                .orgFiscalCode("orgFiscalCode")
                .orgName("orgName")
                .adminEmail("adminEmail")
                .creationDate(DATE.toInstant())
                .lastUpdateDate(DATE.toInstant())
                .paymentTypeCode("paymentTypeCode")
                .fee(500L)
                .iban("iban")
                .myBoxClientKey("myBoxClientKey")
                .myBoxClientSecret("myBoxClientSecret")
                .urlOrgSendSILPaymentResult("urlOrgSendSILPaymentResult")
                .codeGlobalLocationNumber("codeGlobalLocationNumber")
                .password("password")
                .creditBicSeller(true)
                .beneficiaryOrgName("beneficiaryOrgName")
                .beneficiaryOrgAddress("beneficiaryOrgAddress")
                .beneficiaryOrgCivic("beneficiaryOrgCivic")
                .beneficiaryOrgPostalCode("beneficiaryOrgPostalCode")
                .beneficiaryOrgLocation("beneficiaryOrgLocation")
                .beneficiaryOrgProvince("beneficiaryOrgProvince")
                .beneficiaryOrgNation("beneficiaryOrgNation")
                .beneficiaryOrgPhoneNumber("beneficiaryOrgPhoneNumber")
                .beneficiaryOrgWebSite("beneficiaryOrgWebSite")
                .beneficiaryOrgEmail("beneficiaryOrgEmail")
                .applicationCode("applicationCode")
                .cbillInterbankCode("cbillInterbankCode")
                .orgInformation("orgInformation")
                .orgLogoDesc("orgLogoDesc")
                .authorizationDesc("authorizationDesc")
                .status("status")
                .urlActiveExternal("urlActiveExternal")
                .additionalLanguage("additionalLanguage")
                .orgTypeCode("orgTypeCode")
                .startDate(LocalDate.of(2024, 1, 1))
                .brokerId(2L)
                .build();
    }
}
