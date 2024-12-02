package it.gov.pagopa.payhub.activities.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Lazy;

import java.util.Map;

/**
 * Utility DTO to manage mail parameters
 */
@Data
@Builder(toBuilder=true)
@NoArgsConstructor
@AllArgsConstructor
@Lazy
public class MailDTO {
    Map<String, String> params;
    private String[] to;
    private String mailSubject;
    private String mailText;
    private String htmlText;
    private String emailFromAddress;
    private String templateName;
}
