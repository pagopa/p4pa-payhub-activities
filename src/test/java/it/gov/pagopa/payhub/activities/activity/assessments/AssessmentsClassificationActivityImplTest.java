package it.gov.pagopa.payhub.activities.activity.assessments;

import it.gov.pagopa.payhub.activities.connector.classification.AssessmentClassificationService;
import it.gov.pagopa.payhub.activities.dto.assessments.AssessmentEventDTO;
import it.gov.pagopa.payhub.activities.dto.assessments.AssessmentsClassificationSemanticKeyDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssessmentsClassificationActivityImplTest {

	@Mock
	private AssessmentClassificationService assessmentClassificationServiceMock;

	private AssessmentsClassificationActivity activity;

	@AfterEach
	void verifyNoMoreInteractions(){
		Mockito.verifyNoMoreInteractions(assessmentClassificationServiceMock);
	}

	@BeforeEach
	void setUp() {
		activity = new AssessmentsClassificationActivityImpl(assessmentClassificationServiceMock);
	}

	@Test
	void whenClassifyAssessmentThenOk() {
		Long organizationId = 3L;
		String iuv = "testIUV";
		String iud = "testIUD";
		AssessmentEventDTO expectedResult = new AssessmentEventDTO();
		expectedResult.setOrganizationId(organizationId);
		expectedResult.setIuv(iuv);
		expectedResult.setIud(iud);

		AssessmentsClassificationSemanticKeyDTO assessmentsClassificationSemanticKeyDTO = new AssessmentsClassificationSemanticKeyDTO(
				organizationId, iuv, iud
		);

		when(assessmentClassificationServiceMock.classifyAssessment(organizationId, iuv, iud)).thenReturn(expectedResult);

		AssessmentEventDTO actualResult = activity.classifyAssessment(assessmentsClassificationSemanticKeyDTO);

		Assertions.assertEquals(expectedResult, actualResult);
	}
}