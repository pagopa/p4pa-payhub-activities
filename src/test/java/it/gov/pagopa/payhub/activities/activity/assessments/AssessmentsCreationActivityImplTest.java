package it.gov.pagopa.payhub.activities.activity.assessments;

import it.gov.pagopa.payhub.activities.connector.classification.AssessmentsService;
import it.gov.pagopa.pu.classification.dto.generated.Assessments;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssessmentsCreationActivityImplTest {

	@Mock
	private AssessmentsService assessmentsServiceMock;

	private AssessmentsCreationActivity activity;

	@BeforeEach
	void setUp() {
		activity = new AssessmentsCreationActivityImpl(assessmentsServiceMock);
	}

	@AfterEach
	void verifyNoMoreInteractions(){Mockito.verifyNoMoreInteractions(assessmentsServiceMock);}
	@Test
	void createAssessmentsSuccess() {
		Long receiptId = 123L;
		List<Assessments> assessmentsList = List.of(new Assessments());

		when(assessmentsServiceMock.createAssessments(receiptId)).thenReturn(assessmentsList);

		assertDoesNotThrow(() -> activity.createAssessments(receiptId));

		Mockito.verify(assessmentsServiceMock, Mockito.times(1)).createAssessments(receiptId);
	}

	@Test
	void createAssessmentsWithEmptyReceiptId() {
		Long receiptId = null;

		assertDoesNotThrow(() -> activity.createAssessments(receiptId));

		Mockito.verify(assessmentsServiceMock, Mockito.times(1)).createAssessments(receiptId);
	}

	@Test
	void createAssessmentsWithNoAssessmentsCreated() {
		Long receiptId = 123L;
		List<Assessments> emptyAssessmentsList = List.of();

		when(assessmentsServiceMock.createAssessments(receiptId)).thenReturn(emptyAssessmentsList);

		assertDoesNotThrow(() -> activity.createAssessments(receiptId));

		Mockito.verify(assessmentsServiceMock, Mockito.times(1)).createAssessments(receiptId);
	}
}
