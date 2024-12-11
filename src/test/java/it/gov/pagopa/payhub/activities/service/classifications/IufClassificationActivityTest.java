package it.gov.pagopa.payhub.activities.service.classifications;

import it.gov.pagopa.payhub.activities.activity.classifications.IufClassificationActivity;
import it.gov.pagopa.payhub.activities.activity.classifications.IufClassificationActivityImpl;
import it.gov.pagopa.payhub.activities.dao.PaymentsClassificationDao;
import it.gov.pagopa.payhub.activities.dto.classifications.PaymentsClassificationDTO;
import it.gov.pagopa.payhub.activities.exception.PaymentsClassificationSaveException;
import it.gov.pagopa.payhub.activities.utility.faker.ClassificationFaker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IufClassificationActivityTest {

    @Mock
    private PaymentsClassificationDao paymentsClassificationDao;

    private IufClassificationActivity iufClassificationActivity;

    @BeforeEach
    void init() {
        iufClassificationActivity = new IufClassificationActivityImpl(paymentsClassificationDao);
    }

    @Test
    void saveClassificationSuccess() {
        PaymentsClassificationDTO paymentsClassificationDTO = ClassificationFaker.buildPaymentsClassification();
        when(paymentsClassificationDao.save(paymentsClassificationDTO)).thenReturn(true);
        Assertions.assertTrue(iufClassificationActivity.save(paymentsClassificationDTO));
    }

    @Test
    void saveClassificationFailed() {
        PaymentsClassificationDTO paymentsClassificationDTO=null;
        PaymentsClassificationSaveException paymentsClassificationSaveException =
                assertThrows(PaymentsClassificationSaveException.class, () ->
                        iufClassificationActivity.save(paymentsClassificationDTO));

        assertEquals("Null payment classification not valid", paymentsClassificationSaveException.getMessage());
    }

}

