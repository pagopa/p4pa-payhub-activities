package it.gov.pagopa.payhub.activities.utility.faker;

import it.gov.pagopa.payhub.activities.dto.MailTo;

public class MailFaker {
    public static MailTo buildMailTo(){
        return MailTo.builder()
            .emailFromAddress("test_sender@mailtest.com")
            .mailSubject("Subject")
            .to(new String[]{"test_receiver@mailtest.com"})
            .cc(new String[]{"test_cc@mailtest.com"})
            .htmlText("Html Text")
            .build();
    }
}
