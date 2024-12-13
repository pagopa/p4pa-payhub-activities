package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.dto.classifications.IufClassificationActivityResult;

/**
 * Interface for defining an activity to process payment reporting classifications based on IUF.
 */
public interface IufClassificationActivity {

    /**
     * Processes IUF classification based on the provided parameters.
     *
     * @param organizationId the unique identifier of the organization
     * @param iuf            the unique identifier of the payment reporting flow (IUF)
     * @return IufClassificationActivityResult containing PaymentsReportingDTO list and success flag
     */
    IufClassificationActivityResult classify(Long organizationId, String iuf);
}