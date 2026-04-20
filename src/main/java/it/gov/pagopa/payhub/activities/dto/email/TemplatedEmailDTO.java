package it.gov.pagopa.payhub.activities.dto.email;

import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Utility transfer object to manage mail parameters
 */
@Data
@Builder(toBuilder=true)
@NoArgsConstructor
@AllArgsConstructor
public class TemplatedEmailDTO {
    private EmailTemplateName templateName;
    private String from;
    private String[] to;
    private String[] cc;
    private Map<String, String> params;
    private FileResourceDTO attachment;

    public TemplatedEmailDTO(EmailTemplateName templateName, String[] to, String[] cc, Map<String, String> params, FileResourceDTO attachment) {
        this.templateName = templateName;
        this.to = to;
        this.cc = cc;
        this.params = params;
        this.attachment = attachment;
    }
}
