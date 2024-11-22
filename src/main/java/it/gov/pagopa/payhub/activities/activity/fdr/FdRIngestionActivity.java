package it.gov.pagopa.payhub.activities.activity.fdr;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.payhub.activities.dto.fdr.FdRIngestionResponse;
import it.gov.pagopa.payhub.activities.dto.fdr.FdRIngestionResponse;

/**
 * Interface for the FdRIngestionActivity.
 * Defines methods for processing files based on an IngestionFlow ID.
 */
@ActivityInterface
public interface FdRIngestionActivity {

    /**
     * Processes a file based on the provided ID.
     *
     * @param fileId the unique identifier of the file to process.
     * @return {@link FdRIngestionResponse} containing the list of IUFs and status.
     */
    @ActivityMethod
    FdRIngestionResponse processFile(String fileId);
}
