package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.connector.classification.ClassificationService;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClearClassifyIufActivityTest {
    private static final Long ORGANIZATION = 1L;
    private static final String IUF = "IUF";

    @Mock
    private ClassificationService classificationService;

    private ClearClassifyIufActivity clearClassifyIufActivity;

    @BeforeEach
    void init() {
        clearClassifyIufActivity = new ClearClassifyIufActivityImpl(classificationService);
    }

    @Test
    void deleteClassificationSuccess() {
        assertDoesNotThrow(() -> clearClassifyIufActivity.deleteClassificationByIuf(ORGANIZATION,IUF));
    }

    @Test
    void deleteClassificationFailed() {
        when(classificationService.deleteByOrganizationIdAndIufAndLabel(ORGANIZATION, IUF, ClassificationsEnum.TES_NO_IUF_OR_IUV)).thenReturn(0);
        assertEquals(0,clearClassifyIufActivity.deleteClassificationByIuf(ORGANIZATION,IUF));
    }

}

