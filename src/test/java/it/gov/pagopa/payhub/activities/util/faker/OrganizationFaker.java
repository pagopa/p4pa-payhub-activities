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
                .orgEmail("adminEmail")
                .creationDate(OFFSETDATETIME)
                .updateDate(OFFSETDATETIME)
                .postalIban("postalIban")
                .iban("iban")
                .password("password".getBytes())
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
