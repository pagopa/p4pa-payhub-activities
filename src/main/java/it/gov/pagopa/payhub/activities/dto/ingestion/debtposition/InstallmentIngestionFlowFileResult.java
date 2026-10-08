package it.gov.pagopa.payhub.activities.dto.ingestion.debtposition;

import it.gov.pagopa.payhub.activities.dto.ingestion.IngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.service.files.CsvHeaderAware;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * DTO for the InstallmentIngestionResult, representing the result of installment file processing.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class InstallmentIngestionFlowFileResult extends IngestionFlowFileResult implements CsvHeaderAware {

    private String[] originalHeader;

    @Override
    public void setOriginalHeader(String[] header) {
        this.originalHeader = header;
    }

    @Override
    public String[] getOriginalHeader() {
        return originalHeader;
    }

}
