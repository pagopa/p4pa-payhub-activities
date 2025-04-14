package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.dto.classifications.IudClassificationActivityResult;

/**
 * Interface for defining an activity to process payment classifications based on IUD.
 */
public interface IudClassificationActivity {

    /**
     * Processes IUD classification based on the provided parameters.
     *
     * @param organizationId the unique identifier of the organization
     * @param iud            the unique identifier of the payment (IUD)
     * @return an {@link IudClassificationActivityResult} object containing the classification results
     */
    IudClassificationActivityResult classify(Long organizationId, String iud);
}