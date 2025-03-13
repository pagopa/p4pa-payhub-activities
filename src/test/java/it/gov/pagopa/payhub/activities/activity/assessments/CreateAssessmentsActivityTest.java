package it.gov.pagopa.payhub.activities.activity.assessments;

import it.gov.pagopa.payhub.activities.service.assessments.AssessmentsService;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentNoPIIResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAssessmentsActivityTest {

    @Mock
    private AssessmentsService assessmentsServiceMock;

    private CreateAssessmentsActivity activity;

    @BeforeEach
    void init() {
        activity = new CreateAssessmentsActivityImpl(assessmentsServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(assessmentsServiceMock);
    }


    @Test
    void createAssessments_receiptIdWithInstallments() {
        Long receiptId = 1L;
        List<InstallmentNoPIIResponse> installments = List.of(new InstallmentNoPIIResponse());
        when(assessmentsServiceMock.getInstallmentsByReceiptId(receiptId)).thenReturn(installments);

        activity.createAssessments(receiptId);

        verify(assessmentsServiceMock, times(1)).getInstallmentsByReceiptId(receiptId);
        assertEquals(1, installments.size());
    }

    @Test
    void createAssessments_receiptIdWithoutInstallments() {
        Long receiptId = 2L;
        when(assessmentsServiceMock.getInstallmentsByReceiptId(receiptId)).thenReturn(Collections.emptyList());

        activity.createAssessments(receiptId);

        verify(assessmentsServiceMock, times(1)).getInstallmentsByReceiptId(receiptId);
    }

}