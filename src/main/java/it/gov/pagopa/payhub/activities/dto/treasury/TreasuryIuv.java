package it.gov.pagopa.payhub.activities.dto.treasury;

import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/** A {@link Treasury} related to IUV */
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class TreasuryIuv extends Treasury {
}
