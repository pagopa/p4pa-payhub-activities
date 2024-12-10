package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.dao.PaymentsClassificationDao;
import it.gov.pagopa.payhub.activities.dto.classifications.PaymentsClassificationDTO;
import it.gov.pagopa.payhub.activities.exception.PaymentsClassificatioSaveException;
import jakarta.validation.constraints.AssertTrue;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class IufClassificationActivitySaveTest {

    @Mock
    private PaymentsClassificationDao paymentsClassificationDao;
    @Mock
    private PaymentsClassificationDTO classificationDTO;
    @InjectMocks
    private IufClassificationActivityImpl iufClassificationActivity;

    @Test
    void saveClassificationThenSuccess() {
        buildDto();
        try {
            boolean result = iufClassificationActivity.save(classificationDTO);
            assertTrue(result);
        } catch (Exception e){
            System.out.println("Error saving classification");
        }
    }

    @Test
    void saveClassificationThenFailed() {
        buildDto();
        boolean result = false;
        try {
            result = iufClassificationActivity.save(null);
        } catch (Exception e){
            System.out.println("Error saving classification");
        }
        assertFalse(result);
    }

    private void buildDto()  {
        classificationDTO = PaymentsClassificationDTO
                .builder()
                .transferId(1L)
                .classificationCode("CLASSIFICATION_CODE")
                .creationDate(LocalDate.now())
                .organizationId(1L)
                .paymentNotifyId(1L)
                .paymentReportingId(1L)
                .treasuryId(1L)
                .build();
    }
}
