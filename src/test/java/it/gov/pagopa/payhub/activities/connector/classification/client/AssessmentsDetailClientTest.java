package it.gov.pagopa.payhub.activities.connector.classification.client;

import it.gov.pagopa.payhub.activities.connector.classification.config.ClassificationApisHolder;
import it.gov.pagopa.pu.classification.client.generated.AssessmentsDetailEntityControllerApi;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsDetail;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsDetailRequestBody;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssessmentsDetailClientTest {

    @Mock
    private ClassificationApisHolder classificationApisHolderMock;

    private AssessmentsDetailClient assessmentDetailClient;

    @BeforeEach
    void setUp() {
        assessmentDetailClient = new AssessmentsDetailClient(classificationApisHolderMock);
    }
    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(classificationApisHolderMock);
    }


    @Test
    void testCreateAssessmentsDetail() {
        // Given
        String accessToken = "accessToken";
        AssessmentsDetail expectedResponse = new AssessmentsDetail();
        AssessmentsDetailEntityControllerApi mockApi = mock(AssessmentsDetailEntityControllerApi.class);
        when(classificationApisHolderMock.getAssessmentsDetailEntityControllerApi(accessToken)).thenReturn(mockApi);
        when(mockApi.crudCreateAssessmentsdetail(any())).thenReturn(expectedResponse);

        // When
        AssessmentsDetail result = assessmentDetailClient.createAssessmentDetail(new AssessmentsDetailRequestBody(), accessToken);

        // Then
        assertEquals(expectedResponse, result);
        verify(classificationApisHolderMock.getAssessmentsDetailEntityControllerApi(accessToken), times(1))
            .crudCreateAssessmentsdetail(any());
    }

}