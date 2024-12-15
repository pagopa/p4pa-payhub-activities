package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.dto.classifications.ClassifyDTO;

public interface ClearClassifyIufActivity {
    /**
     * deletion of a classification based on the provided dto parameter
     *
     * @param classifyDTO classification dto
     * @return  boolean true for a successful deletion otherwise false
     */
    boolean deleteClassificationByIuf(ClassifyDTO classifyDTO);

}
