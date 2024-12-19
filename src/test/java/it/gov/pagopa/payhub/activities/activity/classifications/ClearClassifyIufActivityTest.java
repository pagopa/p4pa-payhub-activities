package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.dao.ClassificationDao;
import it.gov.pagopa.payhub.activities.enums.ClassificationsEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClearClassifyIufActivityTest {
    private static final Long ORGANIZATION = 1L;
    private static final String IUF = "IUF";

    @Mock
    private ClassificationDao classificationDao;

    private ClearClassifyIufActivity clearClassifyIufActivity;

    @BeforeEach
    void init() {
        clearClassifyIufActivity = new ClearClassifyIufActivityImpl(classificationDao);
    }

    @Test
    void deleteClassificationSuccess() {
        assertDoesNotThrow(() -> clearClassifyIufActivity.deleteClassificationByIuf(ORGANIZATION,IUF));
    }

    @Test
    void deleteClassificationFailed() {
        when(classificationDao.deleteClassificationByIuf(ORGANIZATION, IUF, ClassificationsEnum.TES_NO_MATCH)).thenReturn(Boolean.FALSE);
        assertFalse(clearClassifyIufActivity.deleteClassificationByIuf(ORGANIZATION,IUF));
    }

}

