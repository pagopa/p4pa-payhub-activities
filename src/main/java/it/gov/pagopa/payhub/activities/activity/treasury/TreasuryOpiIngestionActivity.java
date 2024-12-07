package it.gov.pagopa.payhub.activities.activity.treasury;

import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIngestionResulDTO;

/**
 * Interface for the TreasuryOpiIngestionActivity.
 * Defines methods for processing files based on an IngestionFlow ID.
 */
public interface TreasuryOpiIngestionActivity {

    /**
     * Processes a file based on the provided IngestionFlow ID.
     *
     * @param ingestionFlowId the unique identifier related to the file to process.
     * @return {@link TreasuryIngestionResulDTO} containing the list of IUF/IUVs and status.
     */
    TreasuryIngestionResulDTO processFile(Long ingestionFlowId);
}