package it.gov.pagopa.payhub.activities.connector.signedurl;

public interface SignedUrlService {
    byte[] downloadFileFromSignedUrl(String signedUrl);
}
