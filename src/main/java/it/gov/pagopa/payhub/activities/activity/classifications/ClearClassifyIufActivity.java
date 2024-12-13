package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.dto.classifications.ClassifyDTO;

public interface ClearClassifyIufActivity {
    /**
     * deletion of a classification based on the provided parameters
     *
     * @param iuf the unique identifier of the payment reporting flow (IUF)
     * @param classifyDTO classification dto
     * @return boolean true for a successful deletion otherwise false
     */
    boolean deleteClassificationByIuf(String iuf, ClassifyDTO classifyDTO);

}
