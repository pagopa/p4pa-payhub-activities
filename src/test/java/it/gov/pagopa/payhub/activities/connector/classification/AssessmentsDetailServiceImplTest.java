package it.gov.pagopa.payhub.activities.connector.classification;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.classification.client.AssessmentsDetailClient;
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

}
