package it.gov.pagopa.payhub.activities.dto.email;

import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Lazy;

/**
 * Utility transfer object to manage mail parameters
 */
@Data
@Builder(toBuilder=true)
@NoArgsConstructor
@AllArgsConstructor
@Lazy
public class TemplatedEmailDTO {
    private EmailTemplateName templateName;
    private String[] to;
    private String[] cc;
    private Map<String, String> params;
    private AttachmentDTO attachment;
}
