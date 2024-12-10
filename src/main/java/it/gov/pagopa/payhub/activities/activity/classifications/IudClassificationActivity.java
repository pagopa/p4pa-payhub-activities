package it.gov.pagopa.payhub.activities.activity.classifications;
/**
 * Interface for defining an activity to process payment classifications based on IUD.
 */
public interface IudClassificationActivity {

    /**
     * Processes IUD classification based on the provided parameters.
     *
     * @param organizationId the unique identifier of the organization
     * @param iud            the unique identifier of the payment (IUD)
     * @return true if the classification is successful, false otherwise
     */
    boolean classify(String organizationId, String iud);
}