package it.gov.pagopa.payhub.activities.activity.taxonomy;

import it.gov.pagopa.payhub.activities.connector.organization.TaxonomyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Slf4j
@Lazy
@Service
public class SynchronizeTaxonomyActivityImpl implements SynchronizeTaxonomyActivity {

    private final TaxonomyService taxonomyService;

    public SynchronizeTaxonomyActivityImpl(TaxonomyService taxonomyService) {
        this.taxonomyService = taxonomyService;
    }

    @Override
    public Integer syncTaxonomy() {
        log.info("Synchronizing taxonomy");
        return taxonomyService.syncTaxonomies();
    }

}
