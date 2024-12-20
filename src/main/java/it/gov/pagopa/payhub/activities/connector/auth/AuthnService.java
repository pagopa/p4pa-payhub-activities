package it.gov.pagopa.payhub.activities.connector.auth;

import it.gov.pagopa.pu.p4paauth.dto.generated.AccessToken;

public interface AuthnService {
    AccessToken getAccessToken();
}
