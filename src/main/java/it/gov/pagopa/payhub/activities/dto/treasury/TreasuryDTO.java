package it.gov.pagopa.payhub.activities.dto.treasury;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TreasuryDTO {
    private String iuf;
    private Long amount;
}
