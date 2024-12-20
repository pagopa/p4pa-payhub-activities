package it.gov.pagopa.payhub.activities.utility.faker;

import it.gov.pagopa.payhub.activities.dto.classifications.Transfer2ClassifyDTO;

public class ClassifyResultFaker {

    public static Transfer2ClassifyDTO buildTransfer2ClassifyDTO(Long organizationId){
        return Transfer2ClassifyDTO.builder()
                .iuv("IUV")
                .iur("IUR")
                .transferIndex(1)
                .build();
    }
}
