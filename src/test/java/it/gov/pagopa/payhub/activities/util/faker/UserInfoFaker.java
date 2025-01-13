package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.pu.auth.dto.generated.UserInfo;
import it.gov.pagopa.pu.auth.dto.generated.UserOrganizationRoles;

import java.util.List;

public class UserInfoFaker {

    public static UserInfo buildUserInfo() {
        return UserInfo.builder()

                .userId("USERID")
                .mappedExternalUserId("MAPPEDEXTERNALUSERID")
                .fiscalCode("FISCALCODE")
                .familyName("FAMILYNAME")
                .name("NAME")
                .email("user@email.it")
                .issuer("ISSUER")
                .brokerId(1L)
                .brokerFiscalCode("BRFISCALCODE")
                .canManageUsers(true)
                .organizations(List.of(UserOrganizationRoles.builder()
                        .organizationId(1L)
                        .operatorId("OPERATORID")
                        .organizationIpaCode("ORGIPACODE")
                        .roles(List.of("ROLE_ADMIN"))
                        .build()))
                .build();
    }
}
