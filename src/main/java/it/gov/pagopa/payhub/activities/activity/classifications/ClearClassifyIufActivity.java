package it.gov.pagopa.payhub.activities.activity.classifications;

/**
 * Interface for defining an activity to delete classifications based on IUF.
 */
public interface ClearClassifyIufActivity {
    /**
     * deletion of a classification based on the provided parameters
     *
     * @param organizationId organization id
     * @param iuf flow identifier
     * @throws Exception possible exception
     */
    void deleteClassificationByIuf(Long organizationId, String iuf) throws Exception;

}
