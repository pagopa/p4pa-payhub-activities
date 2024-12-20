package it.gov.pagopa.payhub.activities.connector.auth.client;

import it.gov.pagopa.payhub.activities.connector.auth.config.AuthApiHolder;
import it.gov.pagopa.pu.p4paauth.controller.generated.AuthzApi;
import it.gov.pagopa.pu.p4paauth.dto.generated.UserInfo;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class AuthzClient {

    private final AuthApiHolder<AuthzApi> authzApiHolder;

    public AuthzClient(AuthApiHolder<AuthzApi> authzApiHolder) {
        this.authzApiHolder = authzApiHolder;
    }

    public UserInfo getOperatorInfo(String mappedExternalUserId, String accessToken){
        return authzApiHolder.getAuthApi(accessToken)
                .getUserInfoFromMappedExternaUserId(mappedExternalUserId);
    }
}
