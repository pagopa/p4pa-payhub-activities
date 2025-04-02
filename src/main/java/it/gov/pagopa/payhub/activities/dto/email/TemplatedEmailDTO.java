package it.gov.pagopa.payhub.activities.dto.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Lazy;

import java.util.Map;

/**
 * Utility transfer object to manage mail parameters
 */
@Data
@Builder(toBuilder=true)
@NoArgsConstructor
@AllArgsConstructor
@Lazy
public class TemplatedEmailDTO {
    private Map<String, String> params;
    private String[] to;
    private String[] cc;
    private String templateName;
}
