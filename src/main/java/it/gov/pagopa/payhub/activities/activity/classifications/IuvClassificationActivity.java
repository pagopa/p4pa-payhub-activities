package it.gov.pagopa.payhub.activities.activity.classifications;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * Interface for defining an activity to process payment classifications based on IUV.
 */
@ActivityInterface
public interface IuvClassificationActivity {

    /**
     * Processes IUV classification based on the provided parameters.
     *
     * @param organizationId the unique identifier of the organization
     * @param iuv            the unique identifier of the payment (IUV)
     * @param receiptId      the identifier of the receipt associated with the payment
     * @param transferIndex  the index of the transfer to be classified
     * @return true if the classification is successful, false otherwise
     */
    @ActivityMethod
    boolean classify(Long organizationId, String iuv, String receiptId, int transferIndex);
}