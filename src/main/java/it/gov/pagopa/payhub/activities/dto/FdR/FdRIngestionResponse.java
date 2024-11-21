package it.gov.pagopa.payhub.activities.dto.FdR;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for the FdRIngestionResponse, representing the result of file processing.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FdRIngestionResponse {
    private List<String> iufList;  // List of extracted IUFs
    private boolean success;       // Success flag for the operation
}
