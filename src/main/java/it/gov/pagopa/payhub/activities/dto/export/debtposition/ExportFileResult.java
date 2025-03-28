package it.gov.pagopa.payhub.activities.dto.export.debtposition;

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
    /** File path */
    private String filePath;
    /** File name */
    private String fileName;
    /** The number of exported rows correctly handled */
    private Long exportedRows;

}
