package it.gov.pagopa.payhub.activities.connector.auth;

import it.gov.pagopa.payhub.activities.connector.auth.client.AuthzClient;
import it.gov.pagopa.pu.auth.dto.generated.UserInfo;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class AuthzServiceImpl implements AuthzService {

    private final AuthzClient authzClient;
    private final AuthnService authnService;

    public AuthzServiceImpl(AuthzClient authzClient, AuthnService authnService) {
        this.authzClient = authzClient;
        this.authnService = authnService;
    }

    @Override
    public UserInfo getOperatorInfo(String mappedExternalUserId) {
        return authzClient.getOperatorInfo(mappedExternalUserId, authnService.getAccessToken());
    }
}
