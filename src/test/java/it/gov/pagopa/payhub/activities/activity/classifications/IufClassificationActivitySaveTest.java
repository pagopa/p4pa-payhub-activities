package it.gov.pagopa.payhub.activities.activity.classifications;

import it.gov.pagopa.payhub.activities.dao.PaymentsClassificationDao;
import it.gov.pagopa.payhub.activities.dto.classifications.PaymentsClassificationDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static it.gov.pagopa.payhub.activities.utility.faker.ClassificationFaker.buildPaymentsClassification;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class IufClassificationActivitySaveTest {

    @Mock
    private PaymentsClassificationDao paymentsClassificationDao;
    @InjectMocks
    private IufClassificationActivityImpl iufClassificationActivity;

    @Mock
    private PaymentsClassificationDTO paymentsClassificationDTO;

    @Test
    void saveClassificationThenSuccess() {
        paymentsClassificationDTO = buildPaymentsClassification();
        try {
            Mockito.when(paymentsClassificationDao.save(paymentsClassificationDTO))
                    .thenReturn(true);
            assertTrue(iufClassificationActivity.save(paymentsClassificationDTO));
        } catch (Exception e){
            System.out.println("Error saving classification");
        }
    }

    @Test
    void saveClassificationThenFailed() {
        paymentsClassificationDTO = null;
        try {
           assertFalse(iufClassificationActivity.save(paymentsClassificationDTO));
        } catch (Exception e){
            System.out.println("Error saving classification: "+e.getMessage());
        }
    }

}
