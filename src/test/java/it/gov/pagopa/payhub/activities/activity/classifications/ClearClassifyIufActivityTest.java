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
    private ClassifyDao classifyDao;

    private ClearClassifyIufActivity clearClassifyIufActivity;

    @BeforeEach
    void init() {
        clearClassifyIufActivity = new ClearClassifyIufActivityImpl(classifyDao);
    }

    /*
    @Test
    void deleteClassificationSuccess() {
        ClassifyDTO expectedClassifyDTO = ClassificationFaker.buildClassifyDTO();

        assertTrue(clearClassifyIufActivity
                .deleteClassificationByIuf(expectedClassifyDTO));
    }
     */

    @ParameterizedTest
    @ValueSource(strings = {"OK","KO_1","KO_2","KO_3","KO_4","KO_5","KO_6","KO_7","KO_8"})
    void deleteClassification(String params) {
        Long paymentReportingId = 2L;
        String classification = "0";
        String error = "";

        switch (params)  {
            case "OK":
                ClassifyDTO expectedClassifyDTO = ClassificationFaker.buildClassifyDTO();
                assertTrue(clearClassifyIufActivity.deleteClassificationByIuf(expectedClassifyDTO));
                break;
            case "KO_1":
                paymentReportingId = 1L;
                classification = "";
                error = "classification may be not null or blank";
                deleteSingleClassification(paymentReportingId, classification, error);
                break;
            case "KO_2":
                paymentReportingId =  1L;
                classification = null;
                error = "classification may be not null or blank";
                deleteSingleClassification(paymentReportingId, classification, error);
                break;
            case "KO_3":
                paymentReportingId =  0L;
                classification = "CLASSIFICATION";
                error = "payment reporting id may be not null or zero";
                deleteSingleClassification(paymentReportingId, classification, error);
                break;
            case "KO_4":
                paymentReportingId = null;
                classification = "CLASSIFICATION";
                error = "payment reporting id may be not null or zero";
                deleteSingleClassification(paymentReportingId, classification, error);
                break;
            case "KO_5":
                paymentReportingId = null;
                classification = "";
                error = "payment reporting id may be not null or zero";
                deleteSingleClassification(paymentReportingId, classification, error);
                break;
            case "KO_6":
                paymentReportingId = 0L;
                classification = "";
                error = "payment reporting id may be not null or zero";
                deleteSingleClassification(paymentReportingId, classification, error);
                break;
            case "KO_7":
                paymentReportingId = 0L;
                classification = null;
                error = "payment reporting id may be not null or zero";
                deleteSingleClassification(paymentReportingId, classification, error);
                break;
            case "KO_8":
                paymentReportingId = null;
                classification = null;
                error = "payment reporting id may be not null or zero";
                deleteSingleClassification(paymentReportingId, classification, error);
                break;

            default:
                break;
        }
    }

    private void deleteSingleClassification(Long paymentReportingId, String classification, String error)  {
        ClassifyDTO expectedClassifyDTO = ClassificationFaker.buildClassifyDTO();
        expectedClassifyDTO.setPaymentReportingId(paymentReportingId);
        expectedClassifyDTO.setClassificationCode(classification);
        ClearClassifyIufException clearClassifyIufException =
                assertThrows(ClearClassifyIufException.class, () ->
                        clearClassifyIufActivity.deleteClassificationByIuf(expectedClassifyDTO));
        assertEquals(error, clearClassifyIufException.getMessage());
    }


}

