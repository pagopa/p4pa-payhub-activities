package it.gov.pagopa.payhub.activities.dto.ingestion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class IngestionFlowFileErrorDTO implements Serializable {

    private String fileName;
    private String errorCode;
    private String errorMessage;
    public abstract String[] toCsvRow();
}
