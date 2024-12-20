package it.gov.pagopa.payhub.activities.connector.auth.client;

import it.gov.pagopa.payhub.activities.connector.auth.config.AuthApiHolder;
import it.gov.pagopa.pu.p4paauth.controller.generated.AuthnApi;
import it.gov.pagopa.pu.p4paauth.dto.generated.AccessToken;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class AuthnClient {

    private final AuthApiHolder<AuthnApi> authnApiAuthApiHolder;

    public AuthnClient(AuthApiHolder<AuthnApi> authnApiAuthApiHolder) {
        this.authnApiAuthApiHolder = authnApiAuthApiHolder;
    }

    public AccessToken postToken(String clientId, String grantType, String scope, String subjectToken, String subjectIssuer, String subjectTokenType, String clientSecret) {
        return authnApiAuthApiHolder.getAuthApi(null)
                .postToken(clientId, grantType, scope, subjectToken, subjectIssuer, subjectTokenType, clientSecret);
    }

}
