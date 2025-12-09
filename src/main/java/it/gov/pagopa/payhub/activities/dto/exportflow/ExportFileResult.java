package it.gov.pagopa.payhub.activities.dto.exportflow;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for the ExportFileResult, representing the result of export file processing.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExportFileResult {
    /** Organization ID */
    private Long organizationId;
    /** File path */
    private String filePath;
    /** File name */
    private String fileName;
    /** The number of exported rows correctly handled */
    private Long exportedRows;
    /** The export date */
    private LocalDate exportDate;
    /** The size of the file */
    private Long fileSize;
    /** UpdateOperatorExternalId */
    private String updateOperatorExternalId;
}
