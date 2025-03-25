package it.gov.pagopa.payhub.activities.activity.classifications;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

import it.gov.pagopa.payhub.activities.connector.classification.AssessmentService;
import it.gov.pagopa.pu.classification.dto.generated.Assessments;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AssessmentsCreationActivityImplTest {

	@Mock
	private AssessmentService assessmentServiceMock;

	private AssessmentsCreationActivity activity;

	@BeforeEach
	void setUp() {
		activity = new AssessmentsCreationActivityImpl(assessmentServiceMock);
	}

	@AfterEach
	void verifyNoMoreInteractions(){Mockito.verifyNoMoreInteractions(assessmentServiceMock);}
	@Test
	void createAssessmentsSuccess() {
		Long receiptId = 123L;
		List<Assessments> assessmentsList = List.of(new Assessments());

		when(assessmentServiceMock.createAssessments(receiptId)).thenReturn(assessmentsList);

		assertDoesNotThrow(() -> activity.createAssessments(receiptId));

		Mockito.verify(assessmentServiceMock, Mockito.times(1)).createAssessments(receiptId);
	}

	@Test
	void createAssessmentsWithEmptyReceiptId() {
		Long receiptId = null;

		assertDoesNotThrow(() -> activity.createAssessments(receiptId));

		Mockito.verify(assessmentServiceMock, Mockito.times(1)).createAssessments(receiptId);
	}

	@Test
	void createAssessmentsWithNoAssessmentsCreated() {
		Long receiptId = 123L;
		List<Assessments> emptyAssessmentsList = List.of();

		when(assessmentServiceMock.createAssessments(receiptId)).thenReturn(emptyAssessmentsList);

		assertDoesNotThrow(() -> activity.createAssessments(receiptId));

		Mockito.verify(assessmentServiceMock, Mockito.times(1)).createAssessments(receiptId);
	}
}
