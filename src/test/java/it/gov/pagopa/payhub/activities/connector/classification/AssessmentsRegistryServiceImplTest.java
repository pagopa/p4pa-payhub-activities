package it.gov.pagopa.payhub.activities.connector.classification;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.classification.client.AssessmentsRegistryClient;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsRegistry;
import it.gov.pagopa.pu.classification.dto.generated.CreateAssessmentsRegistryByDebtPositionDTOAndIudRequest;
import it.gov.pagopa.pu.classification.dto.generated.PagedModelAssessmentsRegistry;
import it.gov.pagopa.pu.classification.dto.generated.PagedModelAssessmentsRegistryEmbedded;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AssessmentsRegistryServiceImplTest {

    @Mock
    private AssessmentsRegistryClient assessmentsRegistryClient;

    @Mock
    private AuthnService authnService;

    @InjectMocks
    private AssessmentsRegistryServiceImpl service;

    private static final String ACCESS_TOKEN = "mock-token";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(authnService.getAccessToken()).thenReturn(ACCESS_TOKEN);
    }

    @Test
    void testCreateAssessmentsRegistryByDebtPositionDTOAndIudList() {
        // Arrange
        DebtPositionDTO debtPositionDTO = mock(DebtPositionDTO.class);
        List<String> iudList = List.of("iud1", "iud2");

        // Act
        service.createAssessmentsRegistryByDebtPositionDTOAndIudList(debtPositionDTO, iudList);

        // Assert
        ArgumentCaptor<CreateAssessmentsRegistryByDebtPositionDTOAndIudRequest> requestCaptor =
                ArgumentCaptor.forClass(CreateAssessmentsRegistryByDebtPositionDTOAndIudRequest.class);
        verify(assessmentsRegistryClient).createAssessmentsRegistryByDebtPositionDTOAndIud(
                requestCaptor.capture(), eq(ACCESS_TOKEN));

        CreateAssessmentsRegistryByDebtPositionDTOAndIudRequest captured = requestCaptor.getValue();
        assertSame(debtPositionDTO, captured.getDebtPositionDTO());
        assertEquals(iudList, captured.getIudList());
    }

    @Test
    void testCreateAssessmentsRegistry() {
        // Arrange
        AssessmentsRegistry registry = mock(AssessmentsRegistry.class);

        // Act
        service.createAssessmentsRegistry(registry);

        // Assert
        verify(assessmentsRegistryClient).createAssessmentsRegistry(eq(registry), eq(ACCESS_TOKEN));
    }

    @Test
    void testSearchAssessmentsRegistryByBusinessKey_found() {
        // Arrange
        AssessmentsRegistry mockRegistry = new AssessmentsRegistry(); // Usa costruttore adeguato
        String token = "mockToken";

        PagedModelAssessmentsRegistryEmbedded embedded = new PagedModelAssessmentsRegistryEmbedded();
        embedded.setAssessmentsRegistries(List.of(mockRegistry));

        PagedModelAssessmentsRegistry pagedModel = new PagedModelAssessmentsRegistry();
        pagedModel.setEmbedded(embedded);

        when(authnService.getAccessToken()).thenReturn(token);
        when(assessmentsRegistryClient.getAssessmentsRegistry(
                mockRegistry.getOrganizationId(),
                mockRegistry.getDebtPositionTypeOrgCode(),
                mockRegistry.getSectionCode(),
                mockRegistry.getOfficeCode(),
                mockRegistry.getAssessmentCode(),
                mockRegistry.getOperatingYear(),
                token, 0, 1, null))
                .thenReturn(pagedModel);

        // Act
        Optional<AssessmentsRegistry> result = service.searchAssessmentsRegistryByBusinessKey(
                mockRegistry.getOrganizationId(),
                mockRegistry.getDebtPositionTypeOrgCode(),
                mockRegistry.getSectionCode(),
                mockRegistry.getOfficeCode(),
                mockRegistry.getAssessmentCode(),
                mockRegistry.getOperatingYear()
        );

        // Assert
        assertTrue(result.isPresent());
        assertEquals(mockRegistry, result.get());
    }

    @Test
    void testSearchAssessmentsRegistryByBusinessKey_notFound_nullEmbedded() {
        // Arrange
        AssessmentsRegistry mockRegistry = new AssessmentsRegistry();
        String token = "mockToken";

        PagedModelAssessmentsRegistry pagedModel = new PagedModelAssessmentsRegistry();
        pagedModel.setEmbedded(null);

        when(authnService.getAccessToken()).thenReturn(token);
        when(assessmentsRegistryClient.getAssessmentsRegistry(
                mockRegistry.getOrganizationId(),
                mockRegistry.getDebtPositionTypeOrgCode(),
                mockRegistry.getSectionCode(),
                mockRegistry.getOfficeCode(),
                mockRegistry.getAssessmentCode(),
                mockRegistry.getOperatingYear()
                , token, 0, 1, null))
                .thenReturn(pagedModel);

        // service
        Optional<AssessmentsRegistry> result = service.searchAssessmentsRegistryByBusinessKey(
                mockRegistry.getOrganizationId(),
                mockRegistry.getDebtPositionTypeOrgCode(),
                mockRegistry.getSectionCode(),
                mockRegistry.getOfficeCode(),
                mockRegistry.getAssessmentCode(),
                mockRegistry.getOperatingYear()
        );

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testSearchAssessmentsRegistryByBusinessKey_notFound_nullAssessments() {
        // Arrange
        AssessmentsRegistry mockRegistry = new AssessmentsRegistry();
        String token = "mockToken";

        PagedModelAssessmentsRegistryEmbedded embedded = new PagedModelAssessmentsRegistryEmbedded();
        embedded.setAssessmentsRegistries(null);

        PagedModelAssessmentsRegistry pagedModel = new PagedModelAssessmentsRegistry();
        pagedModel.setEmbedded(embedded);

        when(authnService.getAccessToken()).thenReturn(token);
        when(assessmentsRegistryClient.getAssessmentsRegistry(
                mockRegistry.getOrganizationId(),
                mockRegistry.getDebtPositionTypeOrgCode(),
                mockRegistry.getSectionCode(),
                mockRegistry.getOfficeCode(),
                mockRegistry.getAssessmentCode(),
                mockRegistry.getOperatingYear(),
                token, 0, 1, null))
                .thenReturn(pagedModel);

        // Act
        Optional<AssessmentsRegistry> result = service.searchAssessmentsRegistryByBusinessKey(
                mockRegistry.getOrganizationId(),
                mockRegistry.getDebtPositionTypeOrgCode(),
                mockRegistry.getSectionCode(),
                mockRegistry.getOfficeCode(),
                mockRegistry.getAssessmentCode(),
                mockRegistry.getOperatingYear()
        );

        // Assert
        assertTrue(result.isEmpty());
    }
}