package it.gov.pagopa.payhub.activities.dto.email;

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
public class EmailDTO {
    private String from;
    private String[] to;
    private String[] cc;
    private String mailSubject;
    private String htmlText;
    private FileResourceDTO attachment;
}
