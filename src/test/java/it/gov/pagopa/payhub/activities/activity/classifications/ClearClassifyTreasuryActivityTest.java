package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.connector.classification.ClassificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClearClassifyTreasuryActivityTest {
    private static final Long ORGANIZATION = 1L;
    private static final String TREASURY_ID = "TREASURY_ID";

    @Mock
    private ClassificationService classificationService;

    private ClearClassifyTreasuryActivity clearClassifyTreasuryActivity;

    @BeforeEach
    void init() {
        clearClassifyTreasuryActivity = new ClearClassifyTreasuryActivityImpl(classificationService);
    }

    @Test
    void deleteClassificationSuccess() {
        assertDoesNotThrow(() -> clearClassifyTreasuryActivity.deleteClassificationByTreasuryId(ORGANIZATION,TREASURY_ID));
    }

    @Test
    void deleteClassificationFailed() {
        when(classificationService.deleteByOrganizationIdAndTreasuryId(ORGANIZATION, TREASURY_ID)).thenReturn(0);
        assertEquals(0, clearClassifyTreasuryActivity.deleteClassificationByTreasuryId(ORGANIZATION,TREASURY_ID));
    }

}

