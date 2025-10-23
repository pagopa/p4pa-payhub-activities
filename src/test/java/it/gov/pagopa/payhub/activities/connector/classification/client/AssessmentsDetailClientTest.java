package it.gov.pagopa.payhub.activities.connector.classification.client;

import it.gov.pagopa.payhub.activities.connector.classification.config.ClassificationApisHolder;
import it.gov.pagopa.pu.classification.client.generated.AssessmentsDetailEntityControllerApi;
import it.gov.pagopa.pu.classification.client.generated.AssessmentsDetailSearchControllerApi;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsDetail;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsDetailRequestBody;
import it.gov.pagopa.pu.classification.dto.generated.CollectionModelAssessmentsDetail;
import it.gov.pagopa.pu.classification.dto.generated.PagedModelAssessmentsDetailEmbedded;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

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

    @Test
    void whenEmbeddedNullFindAssessmentsDetailByOrganizationIdAndIuvAndIudThenEmptyList() {
        // Given
        String accessToken = "accessToken";
        Long organizationId = 3L;
        String iuv = "testIuv";
        String iud = "testIud";

        AssessmentsDetailSearchControllerApi mockApi = mock(AssessmentsDetailSearchControllerApi.class);

        CollectionModelAssessmentsDetail expectedResponse = new CollectionModelAssessmentsDetail();
        expectedResponse.setEmbedded(null);

        when(classificationApisHolderMock.getAssessmentsDetailSearchControllerApi(accessToken))
                .thenReturn(mockApi);
        when(mockApi.crudAssessmentsDetailsFindAllByOrganizationIdAndIuvAndIud(organizationId, iuv, iud))
                .thenReturn(expectedResponse);

        // When
        List<AssessmentsDetail> actualResult = assessmentDetailClient.findAssessmentsDetailByOrganizationIdAndIuvAndIud(organizationId, iuv, iud, accessToken);

        // Then
        assertEquals(Collections.emptyList(), actualResult);
    }

    @Test
    void whenAssessmentsDetailNullFindAssessmentsDetailByOrganizationIdAndIuvAndIudThenEmptyList() {
        // Given
        String accessToken = "accessToken";
        Long organizationId = 3L;
        String iuv = "testIuv";
        String iud = "testIud";

        AssessmentsDetailSearchControllerApi mockApi = mock(AssessmentsDetailSearchControllerApi.class);

        CollectionModelAssessmentsDetail expectedResponse = new CollectionModelAssessmentsDetail();
        expectedResponse.setEmbedded(new PagedModelAssessmentsDetailEmbedded(null));

        when(classificationApisHolderMock.getAssessmentsDetailSearchControllerApi(accessToken))
                .thenReturn(mockApi);
        when(mockApi.crudAssessmentsDetailsFindAllByOrganizationIdAndIuvAndIud(organizationId, iuv, iud))
                .thenReturn(expectedResponse);

        // When
        List<AssessmentsDetail> actualResult = assessmentDetailClient.findAssessmentsDetailByOrganizationIdAndIuvAndIud(organizationId, iuv, iud, accessToken);

        // Then
        assertEquals(Collections.emptyList(), actualResult);
    }

    @Test
    void whenFindAssessmentsDetailByOrganizationIdAndIuvAndIudThenOk() {
        // Given
        String accessToken = "accessToken";
        Long organizationId = 3L;
        String iuv = "testIuv";
        String iud = "testIud";

        AssessmentsDetailSearchControllerApi mockApi = mock(AssessmentsDetailSearchControllerApi.class);

        CollectionModelAssessmentsDetail expectedResponse = new CollectionModelAssessmentsDetail();
        AssessmentsDetail assessmentsDetail = new AssessmentsDetail();
        assessmentsDetail.setOrganizationId(organizationId);
        assessmentsDetail.setIuv(iuv);
        assessmentsDetail.setIud(iud);
        expectedResponse.setEmbedded(new PagedModelAssessmentsDetailEmbedded(List.of(assessmentsDetail)));

        when(classificationApisHolderMock.getAssessmentsDetailSearchControllerApi(accessToken))
                .thenReturn(mockApi);
        when(mockApi.crudAssessmentsDetailsFindAllByOrganizationIdAndIuvAndIud(organizationId, iuv, iud))
                .thenReturn(expectedResponse);

        // When
        List<AssessmentsDetail> actualResult = assessmentDetailClient.findAssessmentsDetailByOrganizationIdAndIuvAndIud(organizationId, iuv, iud, accessToken);

        // Then
		assert expectedResponse.getEmbedded() != null;
		assertEquals(expectedResponse.getEmbedded().getAssessmentsDetails(), actualResult);
    }

    @Test
    void whenUpdateAssessmentsDetailThenOk() {
        // Given
        String accessToken = "accessToken";
        Long assessmentDetailId = 3L;
        AssessmentsDetailRequestBody updateRequest = new AssessmentsDetailRequestBody();

        AssessmentsDetailEntityControllerApi mockApi = mock(AssessmentsDetailEntityControllerApi.class);
        AssessmentsDetail expectedResponse = new AssessmentsDetail();
        expectedResponse.setAssessmentDetailId(assessmentDetailId);

        when(classificationApisHolderMock.getAssessmentsDetailEntityControllerApi(accessToken))
                .thenReturn(mockApi);
        when(mockApi.crudUpdateAssessmentsdetail(String.valueOf(assessmentDetailId), updateRequest))
                .thenReturn(expectedResponse);

        // When
        AssessmentsDetail actualResult = assessmentDetailClient.updateAssessmentsDetail(assessmentDetailId, updateRequest, accessToken);

        // Then
        assertEquals(expectedResponse, actualResult);
    }
}