package it.gov.pagopa.payhub.activities.connector.auth.client;

import it.gov.pagopa.payhub.activities.connector.auth.config.AuthApisHolder;
import it.gov.pagopa.pu.auth.dto.generated.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Lazy
@Slf4j
@Service
public class AuthzClient {

    private final AuthApisHolder authApisHolder;

    public AuthzClient(AuthApisHolder authApisHolder) {
        this.authApisHolder = authApisHolder;
    }

    public UserInfo getOperatorInfo(String mappedExternalUserId, String accessToken) {
        try {
            return authApisHolder.getAuthzApi(accessToken)
                    .getUserInfoFromMappedExternaUserId(mappedExternalUserId);
        } catch (HttpClientErrorException.NotFound e) {
            log.info("Cannot find User having externalUserId {}", mappedExternalUserId);
            return null;
        }
    }
}
