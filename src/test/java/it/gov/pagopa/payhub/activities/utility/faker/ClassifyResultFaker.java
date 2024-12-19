package it.gov.pagopa.payhub.activities.utility.faker;

import it.gov.pagopa.payhub.activities.dto.classifications.ClassifyResultDTO;

public class ClassifyResultFaker {

    public static ClassifyResultDTO buildClassifyResultDTO(){
        return ClassifyResultDTO.builder()
                .organizationId(1L)
                .creditorReferenceId("IUV")
                .regulationUniqueIdentifier("IUR")
                .transferIndex(1)
                .build();
    }
}
