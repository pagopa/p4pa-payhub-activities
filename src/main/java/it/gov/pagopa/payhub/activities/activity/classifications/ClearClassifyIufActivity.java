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
     * @return boolean true if success deletion or exception
     */
    boolean deleteClassificationByIuf(Long organizationId, String iuf);
}
