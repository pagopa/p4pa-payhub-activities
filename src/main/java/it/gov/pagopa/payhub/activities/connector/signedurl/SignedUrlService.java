package it.gov.pagopa.payhub.activities.connector.signedurl;

public interface SignedUrlService {
    byte[] downloadArchive(Long organizationId, Long ingestionFlowFileId, String signedUrl);
}
