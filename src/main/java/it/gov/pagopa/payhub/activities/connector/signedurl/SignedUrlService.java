package it.gov.pagopa.payhub.activities.connector.signedurl;

public interface SignedUrlService {
    public byte[] downloadArchive(Long organizationId, Long ingestionFlowFileId, String signedUrl);
}
