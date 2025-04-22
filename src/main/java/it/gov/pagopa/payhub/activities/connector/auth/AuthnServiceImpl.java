package it.gov.pagopa.payhub.activities.connector.auth;

import it.gov.pagopa.payhub.activities.connector.auth.service.AuthAccessTokenRetriever;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class AuthnServiceImpl implements AuthnService {

    private final AuthAccessTokenRetriever accessTokenRetriever;

    public AuthnServiceImpl(AuthAccessTokenRetriever accessTokenRetriever) {
        this.accessTokenRetriever = accessTokenRetriever;
    }

    @Override
    public String getAccessToken() {
        return accessTokenRetriever.getAccessToken(null)
                .getAccessToken();
    }

    @Override
    public String getAccessToken(String orgIpaCode) {
        return accessTokenRetriever.getAccessToken(orgIpaCode)
                .getAccessToken();
    }
}
