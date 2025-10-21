package it.gov.pagopa.payhub.activities.connector.classification;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.classification.client.AssessmentsDetailClient;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssessmentsDetailServiceImplTest {

  @Mock
  private AssessmentsDetailClient assessmentsDetailClientMock;
  @Mock
  private AuthnService authnServiceMock;

  private AssessmentsDetailService service;


  @BeforeEach
  void setUp() {
    service = new AssessmentsDetailServiceImpl(assessmentsDetailClientMock, authnServiceMock);
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
            assessmentsDetailClientMock,
        authnServiceMock);
  }


  @Test
  void createAssessmentsDetailWithValidRequest() {
    AssessmentsDetailRequestBody assessmentsRequestBody = new AssessmentsDetailRequestBody();
    AssessmentsDetail expectedResponse = new AssessmentsDetail();
    String accessToken = "accessToken";

    when(assessmentsDetailClientMock.createAssessmentDetail(assessmentsRequestBody, accessToken)).thenReturn(expectedResponse);
    Mockito.when(authnServiceMock.getAccessToken())
            .thenReturn(accessToken);

    AssessmentsDetail result = service.createAssessmentDetail(assessmentsRequestBody);

    assertEquals(expectedResponse, result);
    verify(assessmentsDetailClientMock, times(1)).createAssessmentDetail(assessmentsRequestBody, accessToken);
  }

  @Test
  void whenFindAssessmentsDetailByOrganizationIdAndIuvAndIudThenOk() {
    Long organizationId = 1L;
    String iuv = "testIUV";
    String iud = "testIUD";
    String accessToken = "accessToken";

    //region prepare CollectionModelAssessmentsDetail expectedResponse
    CollectionModelAssessmentsDetail expectedResponse = new CollectionModelAssessmentsDetail();
    PagedModelAssessmentsDetailEmbedded page = new PagedModelAssessmentsDetailEmbedded();
    AssessmentsDetail assessmentsDetail = new AssessmentsDetail();
    assessmentsDetail.setOrganizationId(organizationId);
    assessmentsDetail.setIuv(iuv);
    assessmentsDetail.setIud(iud);
    page.setAssessmentsDetails(List.of(assessmentsDetail));
    expectedResponse.setEmbedded(page);
    //endregion

    when(assessmentsDetailClientMock.findAssessmentsDetailByOrganizationIdAndIuvAndIud(organizationId, iuv, iud, accessToken))
            .thenReturn(expectedResponse);
    Mockito.when(authnServiceMock.getAccessToken())
            .thenReturn(accessToken);

    CollectionModelAssessmentsDetail result = service.findAssessmentsDetailByOrganizationIdAndIuvAndIud(organizationId, iuv, iud);

    assertEquals(expectedResponse, result);
  }

  @Test
  void whenUpdateAssessmentsDetailThenOk() {
    //Given
    Long assessmentDetailId = 1L;
    AssessmentsDetailRequestBody updateRequest = new AssessmentsDetailRequestBody();
    String accessToken = "accessToken";

    AssessmentsDetail expectedResponse = new AssessmentsDetail();
    expectedResponse.setAssessmentDetailId(assessmentDetailId);

    when(assessmentsDetailClientMock.updateAssessmentsDetail(assessmentDetailId, updateRequest, accessToken))
            .thenReturn(expectedResponse);
    Mockito.when(authnServiceMock.getAccessToken())
            .thenReturn(accessToken);

    //When
    AssessmentsDetail result = service.updateAssessmentsDetail(assessmentDetailId, updateRequest);

    //Then
    assertEquals(expectedResponse, result);
  }
}
