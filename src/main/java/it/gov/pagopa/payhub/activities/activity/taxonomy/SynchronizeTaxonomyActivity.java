package it.gov.pagopa.payhub.activities.activity.taxonomy;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * Synchronize taxonomy activity
 */
@ActivityInterface
public interface SynchronizeTaxonomyActivity {
    @ActivityMethod
    Integer syncTaxonomy();
}
