package it.gov.pagopa.payhub.activities.connector.classification.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.gov.pagopa.payhub.activities.connector.classification.config.ClassificationApisHolder;
import it.gov.pagopa.pu.classification.client.generated.AssessmentsControllerApi;
import it.gov.pagopa.pu.classification.dto.generated.Assessments;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AssessmentsClientTest {

    @Mock
    private ClassificationApisHolder classificationApisHolderMock;

    private AssessmentClient assessmentClient;

    @BeforeEach
    void setUp() {
        assessmentClient = new AssessmentClient(classificationApisHolderMock);
    }
    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(classificationApisHolderMock);
    }

    @Test
    void testCreateAssessment() {
        // Given
        String accessToken = "accessToken";
        List<Assessments> expectedResponse = Collections.emptyList();
        AssessmentsControllerApi mockApi = mock(AssessmentsControllerApi.class);
        when(classificationApisHolderMock.getAssessmentsControllerApi(accessToken)).thenReturn(mockApi);
        when(mockApi.createAssessmentByReceiptId(any())).thenReturn(expectedResponse);

        // When
        List<Assessments> result = assessmentClient.createAssessments(1L, accessToken);

        // Then
        assertEquals(expectedResponse, result);
        verify(classificationApisHolderMock.getAssessmentsControllerApi(accessToken), times(1))
            .createAssessmentByReceiptId(any());
    }

}