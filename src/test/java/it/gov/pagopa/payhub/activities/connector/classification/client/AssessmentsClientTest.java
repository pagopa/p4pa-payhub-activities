package it.gov.pagopa.payhub.activities.connector.classification.client;

import it.gov.pagopa.payhub.activities.connector.classification.config.ClassificationApisHolder;
import it.gov.pagopa.pu.classification.client.generated.AssessmentsControllerApi;
import it.gov.pagopa.pu.classification.client.generated.AssessmentsEntityControllerApi;
import it.gov.pagopa.pu.classification.client.generated.AssessmentsSearchControllerApi;
import it.gov.pagopa.pu.classification.dto.generated.Assessments;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsRequestBody;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

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
    void testCreateAssessments() {
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

    @Test
    void testCreateAssessment() {
        // Given
        String accessToken = "accessToken";
        Assessments expectedResponse = new Assessments();
        AssessmentsEntityControllerApi mockApi = mock(AssessmentsEntityControllerApi.class);
        when(classificationApisHolderMock.getAssessmentsEntityControllerApi(accessToken)).thenReturn(mockApi);
        when(mockApi.crudCreateAssessments(any())).thenReturn(expectedResponse);

        // When
        Assessments result = assessmentClient.createAssessment(new AssessmentsRequestBody(), accessToken);

        // Then
        assertEquals(expectedResponse, result);
        verify(classificationApisHolderMock.getAssessmentsEntityControllerApi(accessToken), times(1))
            .crudCreateAssessments(any());
    }


    @Test
    void whenGetByOrganizationIdAndIufThenOk() {
        // Given
        Long organizationId = 1L;
        String debtPositionTypeOrgCode = "debtPositionTypeOrgCode";
        String assessmentName = "AssessmentName";
        String accessToken = "accessToken";
        AssessmentsSearchControllerApi mockApi = mock(AssessmentsSearchControllerApi.class);
        Assessments expectedResponse = new Assessments();

        when(classificationApisHolderMock.getAssessmentsSearchControllerApi(accessToken))
                .thenReturn(mockApi);
        when(mockApi.crudAssessmentsFindByOrganizationIdAndDebtPositionTypeOrgCodeAndAssessmentName(organizationId, debtPositionTypeOrgCode, assessmentName))
                .thenReturn(expectedResponse);

        // When
        Assessments result = assessmentClient.findByOrganizationIdAndDebtPositionTypeOrgCodeAndAssessmentName(organizationId, debtPositionTypeOrgCode, assessmentName, accessToken);

        // Then
        assertEquals(expectedResponse, result);
        verify(classificationApisHolderMock.getAssessmentsSearchControllerApi(accessToken), times(1))
                .crudAssessmentsFindByOrganizationIdAndDebtPositionTypeOrgCodeAndAssessmentName(any(), any(), any());
    }

    @Test
    void whenFindAssessmentByAssessmentIdThenOk() {
        // Given
        Long assessmentId = 1L;
        String accessToken = "accessToken";

        AssessmentsEntityControllerApi mockApi = mock(AssessmentsEntityControllerApi.class);

        Assessments expectedResponse = new Assessments();
        expectedResponse.assessmentId(assessmentId);

        when(classificationApisHolderMock.getAssessmentsEntityControllerApi(accessToken))
                .thenReturn(mockApi);
        when(mockApi.crudGetAssessments(String.valueOf(assessmentId)))
                .thenReturn(expectedResponse);

        // When
        Assessments result = assessmentClient.findAssessment(assessmentId, accessToken);

        // Then
        assertEquals(expectedResponse, result);
    }

    @Test
    void whenFindNonExistingAssessmentByAssessmentIdThenNull() {
        // Given
        Long assessmentId = 0L;
        String accessToken = "accessToken";

        AssessmentsEntityControllerApi mockApi = mock(AssessmentsEntityControllerApi.class);

        when(classificationApisHolderMock.getAssessmentsEntityControllerApi(accessToken))
                .thenReturn(mockApi);
        when(mockApi.crudGetAssessments(String.valueOf(assessmentId)))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));

        // When
        Assessments actualResult = assessmentClient.findAssessment(assessmentId, accessToken);

        // Then
        assertNull(actualResult);
    }
}