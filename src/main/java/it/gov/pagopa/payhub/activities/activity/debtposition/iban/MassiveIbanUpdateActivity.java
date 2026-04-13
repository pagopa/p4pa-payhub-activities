package it.gov.pagopa.payhub.activities.activity.debtposition.iban;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface MassiveIbanUpdateActivity {
    @ActivityMethod
    Boolean massiveIbanUpdateRetrieveAndUpdateDp(Long orgId, Long dptoId, String oldIban, String newIban, String oldPostalIban, String newPostalIban);
}
