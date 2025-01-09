package it.gov.pagopa.payhub.activities.utility.faker;

import it.gov.pagopa.payhub.activities.dto.classifications.ClassificationDTO;
import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;

public class ClassificationFaker {

    public static ClassificationDTO buildClassificationDTO(){
        return ClassificationDTO.builder()
                .organizationId(1L)
                .treasuryId("treasuryId")
                .iuf("IUF")
                .classificationsEnum(ClassificationsEnum.TES_NO_MATCH)
                .build();
    }
}