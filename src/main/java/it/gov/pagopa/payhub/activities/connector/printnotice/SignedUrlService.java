package it.gov.pagopa.payhub.activities.connector.printnotice;

public interface SignedUrlService {
    byte[] downloadFileFromSignedUrl(String signedUrl);
}
