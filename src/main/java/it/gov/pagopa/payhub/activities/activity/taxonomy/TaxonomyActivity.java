package it.gov.pagopa.payhub.activities.activity.taxonomy;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * Sends an email.
 */
@ActivityInterface
public interface TaxonomyActivity {
    @ActivityMethod
    Integer syncTaxonomy();
}
