package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.dto.classifications.IufClassificationDTO;

/**
 * Interface for defining an activity to process payment reporting classifications based on IUF.
 */
public interface IufClassificationActivity {

    /**
     * Processes IUF classification based on the provided parameters.
     *
     * @param organizationId the unique identifier of the organization
     * @param flowIdentifierCode the unique identifier of the payment reporting flow (IUF)
     * @return do IufClassificationDTO containing list of classifications and boolean value for process OK or KO
     */
    IufClassificationDTO classify(Long organizationId, String flowIdentifierCode);
}