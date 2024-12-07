package it.gov.pagopa.payhub.activities.dto.treasury;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for the TreasuryIufResult, representing the result of file processing.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreasuryIngestionResulDTO implements Serializable {
    /** List of extracted IUFs and IUVs */
    private List<IufIuvDTO> iufIuvs;
    /** Success flag for the operation */
    private boolean success;
}
