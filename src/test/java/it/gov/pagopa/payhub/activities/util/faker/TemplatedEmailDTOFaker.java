package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.payhub.activities.dto.email.FileResourceDTO;
import it.gov.pagopa.payhub.activities.dto.email.TemplatedEmailDTO;
import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;
import org.springframework.core.io.ByteArrayResource;

import java.util.Map;

public class TemplatedEmailDTOFaker {
    public static TemplatedEmailDTO buildTemplatedEmailDTO(EmailTemplateName templateName, Map<String, String> params){
        FileResourceDTO attachment = new FileResourceDTO(
                new ByteArrayResource("PDF-DATA".getBytes()),
                "filename"
        );
        return TemplatedEmailDTO.builder()
                .templateName(templateName)
                .from("test_sender@mailtest.com")
                .to(new String[]{"test_receiver@mailtest.com"})
                .cc(new String[]{"test_cc@mailtest.com"})
                .params(params)
                .attachment(attachment)
                .build();
    }
}
