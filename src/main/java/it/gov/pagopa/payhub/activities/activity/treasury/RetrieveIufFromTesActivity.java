package it.gov.pagopa.payhub.activities.activity.treasury;

import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryRetrieveIufActivityResult;

/**
 * Interface for defining an activity to process Treasury
 */
public interface RetrieveIufFromTesActivity {
    /**
     * retrieve treasury having the same iuf
     *
     * @param iuf flow unique identifier
     * @return list of treasury dto associated to the iuf and flag true if a list is returned
     */
    TreasuryRetrieveIufActivityResult searchByIuf(String iuf);
}