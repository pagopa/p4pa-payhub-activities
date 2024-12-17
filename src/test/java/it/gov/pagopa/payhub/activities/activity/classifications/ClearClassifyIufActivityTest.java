package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.dao.ClassifyDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ClearClassifyIufActivityTest {
    @Mock
    private ClassifyDao classifyDao;

    private ClearClassifyIufActivity clearClassifyIufActivity;

    @BeforeEach
    void init() {
        clearClassifyIufActivity = new ClearClassifyIufActivityImpl(classifyDao);
    }

    @Test
    void deleteClassification() {
        Long organizationId = 1L;
        String iuf = "IUF";
        assertDoesNotThrow(() -> clearClassifyIufActivity.deleteClassificationByIuf(organizationId,iuf));
    }

}
