package it.gov.pagopa.payhub.activities.connector.auth.client;

import it.gov.pagopa.payhub.activities.connector.auth.config.AuthApisHolder;
import it.gov.pagopa.pu.auth.dto.generated.UserInfo;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class AuthzClient {

    private final AuthApisHolder authApisHolder;

    public AuthzClient(AuthApisHolder authApisHolder) {
        this.authApisHolder = authApisHolder;
    }

    public UserInfo getOperatorInfo(String mappedExternalUserId, String accessToken){
        return authApisHolder.getAuthzApi(accessToken)
                .getUserInfoFromMappedExternaUserId(mappedExternalUserId);
    }
}
