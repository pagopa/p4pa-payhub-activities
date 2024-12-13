package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.dao.ClassifyDao;
import it.gov.pagopa.payhub.activities.dao.TreasuryDao;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.exception.ClearClassifyIufException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static it.gov.pagopa.payhub.activities.utility.faker.TreasuryFaker.buildTreasuryDTO;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClearClassifyIufActivityTest {
    @Mock
    TreasuryDao treasuryDao;
    @Mock
    private ClassifyDao classifyDao;

    private ClearClassifyIufActivity clearClassifyIufActivity;

    @BeforeEach
    void init() {
        clearClassifyIufActivity = new ClearClassifyIufActivityImpl(treasuryDao, classifyDao);
    }

    @Test
    void deleteClassificationSuccess() {
        TreasuryDTO expectedTreasuryDTO = buildTreasuryDTO();
        List<TreasuryDTO> expectedTreasuryDTOS = new ArrayList<>();
        expectedTreasuryDTOS.add(expectedTreasuryDTO);
        String flowIdentifierCode = expectedTreasuryDTO.getCodIdUnivocoFlusso();

        when(treasuryDao.searchByIuf(flowIdentifierCode))
                .thenReturn(expectedTreasuryDTOS);

        assertTrue(clearClassifyIufActivity
                .deleteClassificationByIuf(flowIdentifierCode,"CLASSIFICATION"));
    }

    @Test
    void deleteClassificationIufNullFailed() {
        String iuf = null;
        String classification = "CLASSIFICATION";

        ClearClassifyIufException clearClassifyIufException =
                assertThrows(ClearClassifyIufException.class, () ->
                        clearClassifyIufActivity.deleteClassificationByIuf(iuf, classification));

        assertEquals("iuf may be not null or blank", clearClassifyIufException.getMessage());
    }

    @Test
    void deleteClassificationIufBlankFailed() {
        String iuf = "";
        String classification = "CLASSIFICATION";

        ClearClassifyIufException clearClassifyIufException =
                assertThrows(ClearClassifyIufException.class, () ->
                        clearClassifyIufActivity.deleteClassificationByIuf(iuf, classification));

        assertEquals("iuf may be not null or blank", clearClassifyIufException.getMessage());
    }

    @Test
    void deleteClassificationIufNullClassFailed() {
        String iuf = "IUF";
        String classification = null;

        ClearClassifyIufException clearClassifyIufException =
                assertThrows(ClearClassifyIufException.class, () ->
                        clearClassifyIufActivity.deleteClassificationByIuf(iuf, classification));

        assertEquals("classification may be not null or blank", clearClassifyIufException.getMessage());
    }

    @Test
    void deleteClassificationBlankClassFailed() {
        String iuf = "IUF";
        String classification = "";

        ClearClassifyIufException clearClassifyIufException =
                assertThrows(ClearClassifyIufException.class, () ->
                        clearClassifyIufActivity.deleteClassificationByIuf(iuf, classification));

        assertEquals("classification may be not null or blank", clearClassifyIufException.getMessage());
    }



}

