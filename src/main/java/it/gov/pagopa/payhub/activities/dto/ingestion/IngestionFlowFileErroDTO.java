package it.gov.pagopa.payhub.activities.dto.ingestion;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public abstract class IngestionFlowFileErroDTO {

    private String fileName;
    private String errorCode;
    private String errorMessage;
    public abstract String[] toCsvRow();
}
