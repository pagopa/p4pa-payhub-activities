package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.payhub.activities.dto.email.EmailDTO;

public class EmailDTOFaker {
    public static EmailDTO buildEmailDTO(){
        return buildEmailDTO(null);
    }

    public static EmailDTO buildEmailDTO(String from){
        return EmailDTO.builder()
                .mailSubject("Subject")
                .from(from)
                .to(new String[]{"test_receiver@mailtest.com"})
                .cc(new String[]{"test_cc@mailtest.com"})
                .htmlText("Html Text")
                .build();
    }
}
