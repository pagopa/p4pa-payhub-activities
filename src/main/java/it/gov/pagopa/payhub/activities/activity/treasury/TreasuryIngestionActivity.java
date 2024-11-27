package it.gov.pagopa.payhub.activities.activity.treasury;

import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIngestionActivityResult;

/**
 * Interface for the TreasuryIngestionActivity.
 * Defines methods for processing files based on an IngestionFlow ID.
 */
public interface TreasuryIngestionActivity {

    /**
     * Processes a file based on the provided IngestionFlow ID.
     *
     * @param ingestionFlowId the unique identifier related to the file to process.
     * @return {@link TreasuryIngestionActivityResult} containing the list of IUFs and status.
     */
    TreasuryIngestionActivityResult processFile(String ingestionFlowId);
}