package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.payhub.activities.dto.email.TemplatedEmailDTO;
import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;

import java.util.Map;

public class TemplatedEmailDTOFaker {
    public static TemplatedEmailDTO buildTemplatedEmailDTO(Map<String, String> params){
        return TemplatedEmailDTO.builder()
                .templateName(EmailTemplateName.INGESTION_PAGOPA_RT)
                .to(new String[]{"test_receiver@mailtest.com"})
                .cc(new String[]{"test_cc@mailtest.com"})
                .params(params)
                .build();
    }
}
