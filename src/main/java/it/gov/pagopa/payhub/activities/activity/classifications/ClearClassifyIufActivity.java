package it.gov.pagopa.payhub.activities.activity.classifications;

public interface ClearClassifyIufActivity {

    /**
     *  deletion of a classification based on the provided parameters
     *
     * @param iuf the unique identifier of the payment reporting flow (IUF)
     * @param classification classification to delete
     * @return boolean true for a successful deletion otherwise false
     */
    boolean deleteClassificationByIuf(String iuf, String classification);

}
