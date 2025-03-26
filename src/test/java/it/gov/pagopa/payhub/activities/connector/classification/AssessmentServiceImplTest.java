package it.gov.pagopa.payhub.activities.connector.classification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.classification.client.AssessmentClient;
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
class AssessmentServiceImplTest {

  @Mock
  private AssessmentClient assessmentClientMock;
  @Mock
  private AuthnService authnServiceMock;

  private AssessmentService assessmentService;


  @BeforeEach
  void setUp() {
    assessmentService = new AssessmentServiceImpl(assessmentClientMock, authnServiceMock);
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
        assessmentClientMock,
        authnServiceMock);
  }

  @Test
  void createAssessmentsWithValidReceiptId() {
    Long receiptId = 123L;
    List<Assessments> expectedResponse = List.of(new Assessments());
    String accessToken = "accessToken";

    when(assessmentClientMock.createAssessments(receiptId, accessToken)).thenReturn(expectedResponse);
    Mockito.when(authnServiceMock.getAccessToken())
        .thenReturn(accessToken);

    List<Assessments> result = assessmentService.createAssessments(receiptId);

    assertEquals(expectedResponse, result);
    verify(assessmentClientMock, times(1)).createAssessments(receiptId, accessToken);
  }

  @Test
  void createAssessmentsWithNullReceiptId() {
    Long receiptId = null;
    String accessToken = "accessToken";

    when(assessmentClientMock.createAssessments(receiptId, accessToken)).thenReturn(Collections.emptyList());
    Mockito.when(authnServiceMock.getAccessToken())
        .thenReturn(accessToken);

    List<Assessments> result = assessmentService.createAssessments(receiptId);

    assertTrue(result.isEmpty());
    verify(assessmentClientMock, times(1)).createAssessments(receiptId, accessToken);
  }

  @Test
  void createAssessmentsWithEmptyResponse() {
    Long receiptId = 123L;
    List<Assessments> expectedResponse = Collections.emptyList();
    String accessToken = "accessToken";

    when(assessmentClientMock.createAssessments(receiptId, accessToken)).thenReturn(expectedResponse);
    Mockito.when(authnServiceMock.getAccessToken())
        .thenReturn(accessToken);

    List<Assessments> result = assessmentService.createAssessments(receiptId);

    assertEquals(expectedResponse, result);
    verify(assessmentClientMock, times(1)).createAssessments(receiptId, accessToken);
  }
}
