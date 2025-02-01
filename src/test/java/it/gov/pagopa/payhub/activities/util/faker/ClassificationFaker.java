package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.classification.dto.generated.Classification;

public class ClassificationFaker {

    public static Classification buildClassificationDTO() {
        return TestUtils.getPodamFactory().manufacturePojo(Classification.class);
    }

}