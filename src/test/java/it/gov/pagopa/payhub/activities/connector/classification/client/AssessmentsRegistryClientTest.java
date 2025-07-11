package it.gov.pagopa.payhub.activities.connector.classification.client;

import it.gov.pagopa.payhub.activities.connector.classification.config.ClassificationApisHolder;
import it.gov.pagopa.payhub.activities.dto.assessments.AssessmentsRegistrySemanticKey;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.classification.client.generated.AssessmentsRegistryApi;
import it.gov.pagopa.pu.classification.client.generated.AssessmentsRegistrySearchControllerApi;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsRegistry;
import it.gov.pagopa.pu.classification.dto.generated.CreateAssessmentsRegistryByDebtPositionDTOAndIudRequest;
import it.gov.pagopa.pu.classification.dto.generated.PagedModelAssessmentsRegistry;
import it.gov.pagopa.pu.classification.dto.generated.PagedModelAssessmentsRegistryEmbedded;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssessmentsRegistryClientTest {

    @Mock
    private ClassificationApisHolder classificationApisHolderMock;
    @Mock
    private AssessmentsRegistryApi assessmentsRegistryApiMock;
    @Mock
    private AssessmentsRegistrySearchControllerApi assessmentsRegistrySearchControllerApiMock;

    private AssessmentsRegistryClient assessmentsRegistryClient;

    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    @BeforeEach
    void setUp() {
        assessmentsRegistryClient = new AssessmentsRegistryClient(classificationApisHolderMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                classificationApisHolderMock,
                assessmentsRegistryApiMock,
                assessmentsRegistrySearchControllerApiMock);
    }

    @Test
    void whenCreateAssessmentRegistryClientThenSuccess() {
        // Given
        CreateAssessmentsRegistryByDebtPositionDTOAndIudRequest request = new CreateAssessmentsRegistryByDebtPositionDTOAndIudRequest();
        String accessToken = "accessToken";

        when(classificationApisHolderMock.getAssessmentsRegistryApi(accessToken)).thenReturn(assessmentsRegistryApiMock);
        doNothing().when(assessmentsRegistryApiMock).createAssessmentsRegistryByDebtPositionDTOAndIud(request);

        // When
        assessmentsRegistryClient.createAssessmentsRegistryByDebtPositionDTOAndIud(request, accessToken);

        // Then
        verify(classificationApisHolderMock.getAssessmentsRegistryApi(accessToken), times(1))
                .createAssessmentsRegistryByDebtPositionDTOAndIud(request);
    }

    @Test
    void testSearchAssessmentsRegistry_BySemanticKey_withValidParams() {
        // Arrange
        String accessToken = "token123";
        AssessmentsRegistrySemanticKey registrySemanticKey = podamFactory.manufacturePojo(AssessmentsRegistrySemanticKey.class);
        registrySemanticKey.setDebtPositionTypeOrgCode(null);

        AssessmentsRegistry expectedResult = new AssessmentsRegistry();
        PagedModelAssessmentsRegistry pagedResult = new PagedModelAssessmentsRegistry();
        pagedResult.setEmbedded(new PagedModelAssessmentsRegistryEmbedded(List.of(expectedResult)));

        when(classificationApisHolderMock.getAssessmentsRegistrySearchControllerApi(accessToken))
                .thenReturn(assessmentsRegistrySearchControllerApiMock);

        when(assessmentsRegistrySearchControllerApiMock.crudAssessmentsRegistriesFindAssessmentsRegistriesByFilters(
                eq(registrySemanticKey.getOrganizationId()),
                eq(Set.of()),
                eq(registrySemanticKey.getSectionCode()),
                isNull(), // macroArea
                eq(registrySemanticKey.getOfficeCode()),
                isNull(), // subOffice
                eq(registrySemanticKey.getAssessmentCode()),
                isNull(), // assessmentTypeCode
                eq(registrySemanticKey.getOperatingYear()),
                isNull(), // assessmentDate
                eq(0),
                eq(1),
                isNull()
        )).thenReturn(pagedResult);

        // Act
        Optional<AssessmentsRegistry> result = assessmentsRegistryClient.searchAssessmentsRegistryBySemanticKey(
                registrySemanticKey,
                accessToken
        );

        // Assert
        assertTrue(result.isPresent());
        assertSame(expectedResult, result.get());
    }

    @Test
    void testSearchAssessmentsRegistry_BySemanticKey_withNullDebtPositionTypeOrgCode() {
        // Arrange
        String accessToken = "token123";
        AssessmentsRegistrySemanticKey registrySemanticKey = podamFactory.manufacturePojo(AssessmentsRegistrySemanticKey.class);

        AssessmentsRegistry expectedResult = new AssessmentsRegistry();
        PagedModelAssessmentsRegistry pagedResult = new PagedModelAssessmentsRegistry();
        pagedResult.setEmbedded(new PagedModelAssessmentsRegistryEmbedded(List.of(expectedResult)));

        when(classificationApisHolderMock.getAssessmentsRegistrySearchControllerApi(accessToken))
                .thenReturn(assessmentsRegistrySearchControllerApiMock);

        when(assessmentsRegistrySearchControllerApiMock.crudAssessmentsRegistriesFindAssessmentsRegistriesByFilters(
                eq(registrySemanticKey.getOrganizationId()),
                eq(Set.of(registrySemanticKey.getDebtPositionTypeOrgCode())),
                eq(registrySemanticKey.getSectionCode()),
                isNull(), // macroArea
                eq(registrySemanticKey.getOfficeCode()),
                isNull(), // subOffice
                eq(registrySemanticKey.getAssessmentCode()),
                isNull(), // assessmentTypeCode
                eq(registrySemanticKey.getOperatingYear()),
                isNull(), // assessmentDate
                eq(0),
                eq(1),
                isNull()
        )).thenReturn(pagedResult);

        // Act
        Optional<AssessmentsRegistry> result = assessmentsRegistryClient.searchAssessmentsRegistryBySemanticKey(
                registrySemanticKey,
                accessToken
        );

        // Assert
        assertTrue(result.isPresent());
        assertSame(expectedResult, result.get());
    }

}