package it.gov.pagopa.payhub.activities.connector.classification.client;

import it.gov.pagopa.payhub.activities.connector.classification.config.ClassificationApisHolder;
import it.gov.pagopa.pu.classification.client.generated.AssessmentsRegistryApi;
import it.gov.pagopa.pu.classification.client.generated.AssessmentsRegistrySearchControllerApi;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsRegistry;
import it.gov.pagopa.pu.classification.dto.generated.CreateAssessmentsRegistryByDebtPositionDTOAndIudRequest;
import it.gov.pagopa.pu.classification.dto.generated.PagedModelAssessmentsRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;

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

  @Test
  void whenGetAssessmentsRegistrySearchWithValidRequestThenSuccess() {
    // Given
    String accessToken = "accessToken";
    AssessmentsRegistry request = new AssessmentsRegistry();
    request.setOrganizationId(1L);
    request.setDebtPositionTypeOrgCode("debtPositionTypeOrgCode");
    Integer page = 0;
    Integer size = 20;
    List<String> sort = List.of("assessmentCode,asc");

    PagedModelAssessmentsRegistry expectedResponse = new PagedModelAssessmentsRegistry();

    AssessmentsRegistrySearchControllerApi mockApi = mock(AssessmentsRegistrySearchControllerApi.class);
    when(classificationApisHolderMock.getAssessmentsRegistrySearchControllerApi(accessToken)).thenReturn(mockApi);
    when(mockApi.crudAssessmentsRegistriesFindAssessmentsRegistriesByFilters(
            1L, Set.of("debtPositionTypeOrgCode"), null, null, null, null, null, null, null, null, page, size, sort))
            .thenReturn(expectedResponse);

    // When
    PagedModelAssessmentsRegistry actualResponse = assessmentsRegistryClient.getAssessmentsRegistrySearch(request, accessToken, page, size, sort);

    // Then
    Assertions.assertNotNull(actualResponse);
    Assertions.assertEquals(expectedResponse, actualResponse);
    verify(classificationApisHolderMock.getAssessmentsRegistrySearchControllerApi(accessToken), times(1))
            .crudAssessmentsRegistriesFindAssessmentsRegistriesByFilters(
                    1L, Set.of("debtPositionTypeOrgCode"), null, null, null, null, null, null, null, null, page, size, sort);
  }

  @Test
  void whenGetAssessmentsRegistrySearchWithEmptyDebtPositionTypeOrgCodeThenSuccess() {
    // Given
    String accessToken = "accessToken";
    AssessmentsRegistry request = new AssessmentsRegistry();
    request.setOrganizationId(1L);
    request.setDebtPositionTypeOrgCode("");
    Integer page = 0;
    Integer size = 20;
    List<String> sort = List.of("assessmentCode,asc");

    PagedModelAssessmentsRegistry expectedResponse = new PagedModelAssessmentsRegistry();

    AssessmentsRegistrySearchControllerApi mockApi = mock(AssessmentsRegistrySearchControllerApi.class);
    when(classificationApisHolderMock.getAssessmentsRegistrySearchControllerApi(accessToken)).thenReturn(mockApi);
    when(mockApi.crudAssessmentsRegistriesFindAssessmentsRegistriesByFilters(
            1L, Set.of(), null, null, null, null, null, null, null, null, page, size, sort))
            .thenReturn(expectedResponse);

    // When
    PagedModelAssessmentsRegistry actualResponse = assessmentsRegistryClient.getAssessmentsRegistrySearch(request, accessToken, page, size, sort);

    // Then
    Assertions.assertNotNull(actualResponse);
    Assertions.assertEquals(expectedResponse, actualResponse);
    verify(classificationApisHolderMock.getAssessmentsRegistrySearchControllerApi(accessToken), times(1))
            .crudAssessmentsRegistriesFindAssessmentsRegistriesByFilters(
                    1L, Set.of(), null, null, null, null, null, null, null, null, page, size, sort);
  }

  @Test
  void whenGetAssessmentsRegistrySearchWithNullRequestThenThrowException() {
    // Given
    String accessToken = "accessToken";
    AssessmentsRegistry request = null;
    Integer page = 0;
    Integer size = 20;
    List<String> sort = List.of("assessmentCode,asc");

    // When & Then
    Assertions.assertThrows(NullPointerException.class, () ->
            assessmentsRegistryClient.getAssessmentsRegistrySearch(request, accessToken, page, size, sort));
  }

}