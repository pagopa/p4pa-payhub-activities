package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionTypeOrg;

public class DebtPositionTypeOrgFaker {
    public static DebtPositionTypeOrg buildDebtPositionTypeOrgDTO() {
        return TestUtils.getPodamFactory().manufacturePojo(DebtPositionTypeOrg.class);
    }
}
