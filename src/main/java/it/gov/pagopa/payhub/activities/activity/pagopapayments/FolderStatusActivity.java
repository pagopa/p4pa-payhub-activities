package it.gov.pagopa.payhub.activities.activity.pagopapayments;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.pu.pagopapayments.dto.generated.SignedUrlResultDTO;

@ActivityInterface
public interface FolderStatusActivity {
    /**
     * Retrieves the status of the specified folderId.
     * Returns a SignedUrlResultDTO which includes:
     * - a signedUrl
     * - two lists: one of processed items and another of notices in error
     * If the status is not complete, returns a 204 no content.
     *
     * @param organizationId the ID of the organization
     * @param folderId the id of the folder
     */
    @ActivityMethod
    SignedUrlResultDTO retrieveFolderStatus(Long organizationId, String folderId);
}

