package it.gov.pagopa.payhub.activities.connector.auth;

import it.gov.pagopa.payhub.activities.connector.auth.client.AuthnClient;
import it.gov.pagopa.payhub.activities.connector.auth.service.AuthAccessTokenRetriever;
import it.gov.pagopa.pu.p4paauth.dto.generated.AccessToken;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class AuthnServiceImpl implements AuthnService {

    private final AuthnClient authnClient;
    private final AuthAccessTokenRetriever accessTokenRetriever;

    public AuthnServiceImpl(AuthnClient authnClient, AuthAccessTokenRetriever accessTokenRetriever) {
        this.authnClient = authnClient;
        this.accessTokenRetriever = accessTokenRetriever;
    }

    @Override
    public AccessToken getAccessToken() {
        return accessTokenRetriever.getAccessToken();
    }
}
