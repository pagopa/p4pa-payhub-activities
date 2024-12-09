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
     * @param iuf            the unique identifier of the payment reporting flow (IUF)
     * @return  List<ClassificationDTO> list of classifications
     */
    IufClassificationDTO classify(String organizationId, String iuf);
}