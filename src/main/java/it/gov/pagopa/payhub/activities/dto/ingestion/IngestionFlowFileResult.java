package it.gov.pagopa.payhub.activities.dto.ingestion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * DTO for the IngestionResult, representing the result of file processing.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class IngestionFlowFileResult {
    private Long organizationId;
    private String fileVersion;
    /** The total number of rows in the file */
    private long totalRows;
    /** The number of rows correctly handled */
    private long processedRows;
    /** Error description */
    private String errorDescription;
    /** Discarded file name */
    private String discardedFileName;
    /** OperatorExternalUserId */
    private String operatorExternalUserId;
    /** FileSize */
    private Long fileSize;
}
