package it.gov.pagopa.payhub.activities.activity.treasury;

import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIufResult;

/**
 * Interface for the TreasuryOpiIngestionActivity.
 * Defines methods for processing files based on an IngestionFlow ID.
 */
public interface TreasuryOpiIngestionActivity {

    /**
     * Processes a file based on the provided IngestionFlow ID.
     *
     * @param ingestionFlowId the unique identifier related to the file to process.
     * @return {@link TreasuryIufResult} containing the list of IUFs and status.
     */
    TreasuryIufResult processFile(Long ingestionFlowId);
}