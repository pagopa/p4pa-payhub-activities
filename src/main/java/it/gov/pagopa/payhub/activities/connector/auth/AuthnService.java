package it.gov.pagopa.payhub.activities.connector.auth;

public interface AuthnService {
    String getAccessToken();
    String getAccessToken(String orgIpaCode);
}
