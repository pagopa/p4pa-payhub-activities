package it.gov.pagopa.payhub.activities.activity.classifications;
/**
 * Interface for defining an activity to process payment classifications.
 */
public interface IuvClassificationActivity {

    /**
     * Processes IUV classification based on the provided parameters.
     *
     * @param organizationId the unique identifier of the organization
     * @param IUV            the unique identifier of the payment (IUV)
     * @param receiptId      the identifier of the receipt associated with the payment
     * @param transferIndex  the index of the transfer to be classified
     * @return true if the classification is successful, false otherwise
     */
    boolean classify(String organizationId, String iuv, String receiptId, int transferIndex);
}