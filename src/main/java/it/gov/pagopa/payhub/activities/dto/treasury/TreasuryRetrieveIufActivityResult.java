package it.gov.pagopa.payhub.activities.dto.treasury;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TreasuryRetrieveIufActivityResult {
    private List<TreasuryDTO> reportingDTOList;
    private boolean success;
}
