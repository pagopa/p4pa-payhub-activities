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
                .organizations(List.of(UserOrganizationRoles.builder()
                        .operatorId("OPERATORID")
                        .organizationIpaCode("ORGIPACODE")
                        .roles(List.of("ROLE_ADMIN"))
                        .build()))
                .build();
    }
}
