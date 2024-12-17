package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.dao.ClassifyDao;
import it.gov.pagopa.payhub.activities.utility.Utilities;
import net.bytebuddy.implementation.bytecode.Throw;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClearClassifyIufActivityTest {
    private static final Long ORGANIZATION = 1L;
    private static final String IUF = "IUF";

    @Mock
    private ClassifyDao classifyDao;

    private ClearClassifyIufActivity clearClassifyIufActivity;

    @BeforeEach
    void init() {
        clearClassifyIufActivity = new ClearClassifyIufActivityImpl(classifyDao);
    }

    @Test
    void deleteClassificationSuccess() {
        assertDoesNotThrow(() -> clearClassifyIufActivity.deleteClassificationByIuf(ORGANIZATION,IUF));
    }

    @Test
    void deleteClassificationFailed() {
        when(classifyDao.deleteClassificationByIuf(ORGANIZATION, IUF, Utilities.CLASSIFICATION.TES_NO_MATCH.getValue())).thenReturn(false);
        assertFalse(clearClassifyIufActivity.deleteClassificationByIuf(ORGANIZATION,IUF));
    }

}

