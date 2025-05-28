package it.gov.pagopa.payhub.activities.dto.ingestion.debtposition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncIngestedDebtPositionDTO {
    private String errorsDescription;
    private String pdfGeneratedId;
    private String iuvFilePath;
    private String iuvFileName;
}
