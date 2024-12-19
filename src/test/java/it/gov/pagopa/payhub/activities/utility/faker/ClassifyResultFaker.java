package it.gov.pagopa.payhub.activities.utility.faker;

import it.gov.pagopa.payhub.activities.dto.classifications.ClassifyResultDTO;

public class ClassifyResultFaker {

    public static ClassifyResultDTO buildClassifyResultDTO(Long organizationId){
        return ClassifyResultDTO.builder()
                .organizationId(organizationId)
                .creditorReferenceId("IUV")
                .regulationUniqueIdentifier("IUR")
                .transferIndex(1)
                .build();
    }
}
