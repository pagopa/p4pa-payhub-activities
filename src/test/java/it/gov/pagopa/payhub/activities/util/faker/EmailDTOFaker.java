package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.payhub.activities.dto.email.EmailDTO;

public class EmailDTOFaker {
    public static EmailDTO buildEmailDTO(){
        return EmailDTO.builder()
            .mailSubject("Subject")
            .to(new String[]{"test_receiver@mailtest.com"})
            .cc(new String[]{"test_cc@mailtest.com"})
            .htmlText("Html Text")
            .build();
    }
}
