package it.gov.pagopa.payhub.activities.activity.assessments;

import it.gov.pagopa.payhub.activities.service.classifications.assessments.AssessmentClassificationService;
import it.gov.pagopa.payhub.activities.dto.assessments.AssessmentEventDTO;
import it.gov.pagopa.payhub.activities.dto.assessments.AssessmentsClassificationSemanticKeyDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssessmentsClassificationActivityImplTest {

	@Mock
	private AssessmentClassificationService assessmentClassificationServiceMock;
	@InjectMocks
	private AssessmentsClassificationActivityImpl activity;

	@AfterEach
	void verifyNoMoreInteractions(){
		Mockito.verifyNoMoreInteractions(assessmentClassificationServiceMock);
	}

	@Test
	void whenClassifyAssessmentThenOk() {
		//Given
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

		when(assessmentClassificationServiceMock.classifyAssessment(assessmentsClassificationSemanticKeyDTO))
				.thenReturn(expectedResult);
		//When
		AssessmentEventDTO actualResult = activity.classifyAssessment(assessmentsClassificationSemanticKeyDTO);
		//Then
		Assertions.assertEquals(expectedResult, actualResult);
	}
}