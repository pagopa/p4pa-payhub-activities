package it.gov.pagopa.payhub.activities.dto.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Utility transfer object to manage mail parameters
 */
@Data
@Builder(toBuilder=true)
@NoArgsConstructor
@AllArgsConstructor
public class EmailDTO {
    private Long brokerId;
    private String[] to;
    private String[] cc;
    private String mailSubject;
    private String htmlText;
    private List<FileResourceDTO> attachments;
    private List<FileResourceDTO> inlines;
}
