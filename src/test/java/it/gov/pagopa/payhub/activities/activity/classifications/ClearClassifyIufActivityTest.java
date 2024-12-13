package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.dao.ClassifyDao;
import it.gov.pagopa.payhub.activities.dao.TreasuryDao;
import it.gov.pagopa.payhub.activities.dto.classifications.ClassifyDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryDTO;
import it.gov.pagopa.payhub.activities.exception.ClearClassifyIufException;
import it.gov.pagopa.payhub.activities.utility.faker.ClassificationFaker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
        ClassifyDTO expectedClassifyDTO = ClassificationFaker.buildClassifyDTO();

        TreasuryDTO expectedTreasuryDTO = buildTreasuryDTO();
        List<TreasuryDTO> expectedTreasuryDTOS = new ArrayList<>();
        expectedTreasuryDTOS.add(expectedTreasuryDTO);

        String flowIdentifierCode = expectedTreasuryDTO.getCodIdUnivocoFlusso();

        when(treasuryDao.searchByIuf(flowIdentifierCode))
                .thenReturn(expectedTreasuryDTOS);

        assertTrue(clearClassifyIufActivity
                .deleteClassificationByIuf(flowIdentifierCode,expectedClassifyDTO));
    }

    @ParameterizedTest
    @ValueSource(strings = {"A","B", "C", "D"})
    void deleteClassificationFailed(String params) {
        String iuf = "";
        String classification = "";
        String error = "";

        switch (params)  {
            case "A":
                iuf = "IUF";
                classification = "";
                error = "classification may be not null or blank";
                break;
            case "B":
                iuf = "IUF";
                classification = null;
                error = "classification may be not null or blank";
                break;
            case "C":
                iuf = "";
                classification = "CLASSIFICATION";
                error = "iuf may be not null or blank";
                break;
            case "D":
                iuf = null;
                classification = "CLASSIFICATION";
                error = "iuf may be not null or blank";
                break;
            default:
                break;
        }

        ClassifyDTO expectedClassifyDTO = ClassificationFaker.buildClassifyDTO();
        expectedClassifyDTO.setClassificationCode(classification);

        String finalIuf = iuf;
        ClearClassifyIufException clearClassifyIufException =
                assertThrows(ClearClassifyIufException.class, () ->
                        clearClassifyIufActivity.deleteClassificationByIuf(finalIuf, expectedClassifyDTO));

        assertEquals(error, clearClassifyIufException.getMessage());
    }


}

