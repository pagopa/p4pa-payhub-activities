package it.gov.pagopa.payhub.activities.connector.classification.client;

import it.gov.pagopa.payhub.activities.connector.classification.config.ClassificationApisHolder;
import it.gov.pagopa.pu.classification.client.generated.AssessmentsRegistryApi;
import it.gov.pagopa.pu.classification.client.generated.AssessmentsRegistrySearchControllerApi;
import it.gov.pagopa.pu.classification.dto.generated.CreateAssessmentsRegistryByDebtPositionDTOAndIudRequest;
import it.gov.pagopa.pu.classification.dto.generated.PagedModelAssessmentsRegistry;
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
    void testGetAssessmentsRegistrySearch_withValidParams() {
        // Arrange
        String accessToken = "token123";
        Long organizationId = 1L;
        String debtPositionTypeOrgCode = "CODE123";
        String sectionCode = "SEC1";
        String officeCode = "OFF1";
        String assessmentCode = "ASS1";
        String operatingYear = "2024";
        int page = 0;
        int size = 10;
        List<String> sort = Collections.singletonList("field,asc");

        PagedModelAssessmentsRegistry expectedResponse = new PagedModelAssessmentsRegistry();

        AssessmentsRegistrySearchControllerApi searchControllerApi = mock(AssessmentsRegistrySearchControllerApi.class);

        when(classificationApisHolderMock.getAssessmentsRegistrySearchControllerApi(accessToken))
                .thenReturn(searchControllerApi);

        when(searchControllerApi.crudAssessmentsRegistriesFindAssessmentsRegistriesByFilters(
                eq(organizationId),
                anySet(),
                eq(sectionCode),
                isNull(), // macroArea
                eq(officeCode),
                isNull(), // subOffice
                eq(assessmentCode),
                isNull(), // assessmentTypeCode
                eq(operatingYear),
                isNull(), // assessmentDate
                eq(page),
                eq(size),
                eq(sort)
        )).thenReturn(expectedResponse);

        // Act
        PagedModelAssessmentsRegistry result = assessmentsRegistryClient.getAssessmentsRegistrySearch(
                organizationId,
                debtPositionTypeOrgCode,
                sectionCode,
                officeCode,
                assessmentCode,
                operatingYear,
                accessToken,
                page,
                size,
                sort
        );

        // Assert
        assertEquals(expectedResponse, result);
        verify(classificationApisHolderMock, times(1)).getAssessmentsRegistrySearchControllerApi(accessToken);
        verify(searchControllerApi, times(1)).crudAssessmentsRegistriesFindAssessmentsRegistriesByFilters(
                eq(organizationId),
                eq(Collections.singleton(debtPositionTypeOrgCode)),
                eq(sectionCode),
                isNull(),
                eq(officeCode),
                isNull(),
                eq(assessmentCode),
                isNull(),
                eq(operatingYear),
                isNull(),
                eq(page),
                eq(size),
                eq(sort)
        );
    }

    @Test
    void testGetAssessmentsRegistrySearch_withNullDebtPositionTypeOrgCode() {
        AssessmentsRegistrySearchControllerApi searchControllerApi = mock(AssessmentsRegistrySearchControllerApi.class);
        when(classificationApisHolderMock.getAssessmentsRegistrySearchControllerApi(anyString()))
                .thenReturn(searchControllerApi);

        when(searchControllerApi.crudAssessmentsRegistriesFindAssessmentsRegistriesByFilters(
                anyLong(),
                eq(Collections.emptySet()),
                anyString(),
                any(),
                anyString(),
                any(),
                anyString(),
                any(),
                anyString(),
                any(),
                anyInt(),
                anyInt(),
                anyList()
        )).thenReturn(new PagedModelAssessmentsRegistry());

        // Act
        PagedModelAssessmentsRegistry result = assessmentsRegistryClient.getAssessmentsRegistrySearch(
                1L, null, "SEC", "OFF", "ASS", "2024", "token", 0, 10, List.of("sort"));

        // Assert
        assertEquals(PagedModelAssessmentsRegistry.class, result.getClass());
    }

}