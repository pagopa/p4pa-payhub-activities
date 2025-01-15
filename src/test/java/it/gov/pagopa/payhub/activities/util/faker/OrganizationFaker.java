package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.pu.organization.dto.generated.Organization;

import java.time.LocalDate;

import static it.gov.pagopa.payhub.activities.util.TestUtils.OFFSETDATETIME;

public class OrganizationFaker {

    public static Organization buildOrganizationDTO() {
        return Organization.builder()
                .organizationId(1L)
                .ipaCode("ipaCode")
                .orgFiscalCode("orgFiscalCode")
                .orgName("orgName")
                .adminEmail("adminEmail")
                .creationDate(OFFSETDATETIME)
                .updateDate(OFFSETDATETIME)
                .iban("iban")
                .password("password".getBytes())
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
                .segregationCode("segregationCode")
                .cbillInterBankCode("cbillInterbankCode")
                .orgLogo("orgLog")
                .status(Organization.StatusEnum.ACTIVE)
                .additionalLanguage("additionalLanguage")
                .orgTypeCode("orgTypeCode")
                .startDate(LocalDate.of(2024, 1, 1))
                .brokerId(2L)
                .build();
    }
}
