package it.gov.pagopa.payhub.activities.activity.taxonomy;

import it.gov.pagopa.payhub.activities.connector.organization.TaxonomyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class TaxonomyActivityTest {

    @Mock
    private TaxonomyService taxonomyServiceMock;

    private TaxonomyActivity taxonomyActivity;

    @BeforeEach
    void init() {
        taxonomyActivity = new TaxonomyActivityImpl(taxonomyServiceMock);
    }


    @Test
    void syncTaxonomyReturnsSyncedCount() {
        Mockito.when(taxonomyServiceMock.syncTaxonomies()).thenReturn(42);

        Integer result = taxonomyActivity.syncTaxonomy();

        assertEquals(42, result);
        Mockito.verify(taxonomyServiceMock).syncTaxonomies();
    }

}