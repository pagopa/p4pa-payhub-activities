package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.auth.dto.generated.UserInfo;
import it.gov.pagopa.pu.auth.dto.generated.UserOrganizationRoles;

import java.util.List;

public class UserInfoFaker {

    public static UserInfo buildUserInfo() {
        return TestUtils.getPodamFactory().manufacturePojo(UserInfo.class)
                .userId("USERID")
                .mappedExternalUserId("MAPPEDEXTERNALUSERID")
                .fiscalCode("FISCALCODE")
                .familyName("FAMILYNAME")
                .name("NAME")
                .issuer("ISSUER")
                .brokerId(1L)
                .brokerFiscalCode("BRFISCALCODE")
                .canManageUsers(true)
                .organizations(List.of(UserOrganizationRoles.builder()
                        .organizationId(1L)
                        .operatorId("OPERATORID")
                        .email("user@email.it")
                        .organizationIpaCode("ORGIPACODE")
                        .roles(List.of("ROLE_ADMIN"))
                        .build(),
                        UserOrganizationRoles.builder()
                                .organizationId(2L)
                                .operatorId("OPERATORID2")
                                .email("user@email2.it")
                                .organizationIpaCode("ORGIPACODE2")
                                .roles(List.of("ROLE_ADMIN"))
                                .build()));
    }
}
