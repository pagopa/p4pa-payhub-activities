package it.gov.pagopa.payhub.activities.util.faker;


import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionType;

public class DebtPositionTypeFaker {
    public static DebtPositionType buildDebtPositionType(){
        return TestUtils.getPodamFactory().manufacturePojo(DebtPositionType.class);
    }
}
