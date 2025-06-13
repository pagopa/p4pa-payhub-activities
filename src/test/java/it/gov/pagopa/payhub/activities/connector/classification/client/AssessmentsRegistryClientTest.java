package it.gov.pagopa.payhub.activities.connector.classification.client;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.gov.pagopa.payhub.activities.connector.classification.config.ClassificationApisHolder;
import it.gov.pagopa.pu.classification.client.generated.AssessmentsRegistryApi;
import it.gov.pagopa.pu.classification.dto.generated.CreateAssessmentsRegistryByDebtPositionDTOAndIudRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AssessmentsRegistryClientTest {

  @Mock
  private ClassificationApisHolder classificationApisHolderMock;

  private AssessmentsRegistryClient assessmentsRegistryClient;

  @BeforeEach
  void setUp() {
    assessmentsRegistryClient = new AssessmentsRegistryClient(classificationApisHolderMock);
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(classificationApisHolderMock);
  }

  @Test
  void whenCreateAssessmentRegistryClientThenSuccess() {
    // Given
    CreateAssessmentsRegistryByDebtPositionDTOAndIudRequest request = new CreateAssessmentsRegistryByDebtPositionDTOAndIudRequest();
    String accessToken = "accessToken";

    AssessmentsRegistryApi mockApi = mock(AssessmentsRegistryApi.class);
    when(classificationApisHolderMock.getAssessmentsRegistryApi(accessToken)).thenReturn(mockApi);
    doNothing().when(mockApi).createAssessmentsRegistryByDebtPositionDTOAndIud(request);

    // When
    assessmentsRegistryClient.createAssessmentsRegistryByDebtPositionDTOAndIud(request, accessToken);

    // Then
    verify(classificationApisHolderMock.getAssessmentsRegistryApi(accessToken), times(1))
        .createAssessmentsRegistryByDebtPositionDTOAndIud(request);
  }

}