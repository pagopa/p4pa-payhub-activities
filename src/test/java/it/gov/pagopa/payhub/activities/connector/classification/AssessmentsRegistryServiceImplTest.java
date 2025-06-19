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
    void testGetAssessmentsRegistrySearch_withData() {
        // Arrange
        AssessmentsRegistry input = new AssessmentsRegistry();
        AssessmentsRegistry result1 = new AssessmentsRegistry();
        AssessmentsRegistry result2 = new AssessmentsRegistry();

        PagedModelAssessmentsRegistryEmbedded embedded = new PagedModelAssessmentsRegistryEmbedded();
        embedded.setAssessmentsRegistries(List.of(result1, result2));

        PagedModelAssessmentsRegistry paged = new PagedModelAssessmentsRegistry();
        paged.setEmbedded(embedded);

        when(assessmentsRegistryClient.getAssessmentsRegistrySearch(eq(input), eq(ACCESS_TOKEN), any(), any(), any()))
                .thenReturn(paged);

        // Act
        List<AssessmentsRegistry> result = service.getAssessmentsRegistrySearch(input);

        // Assert
        assertEquals(2, result.size());
        assertSame(result1, result.get(0));
        assertSame(result2, result.get(1));
    }

    @Test
    void testGetAssessmentsRegistrySearch_nullEmbedded() {
        // Arrange
        AssessmentsRegistry input = new AssessmentsRegistry();
        PagedModelAssessmentsRegistry paged = new PagedModelAssessmentsRegistry();
        paged.setEmbedded(null);

        when(assessmentsRegistryClient.getAssessmentsRegistrySearch(eq(input), eq(ACCESS_TOKEN), any(), any(), any()))
                .thenReturn(paged);

        // Act
        List<AssessmentsRegistry> result = service.getAssessmentsRegistrySearch(input);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAssessmentsRegistrySearch_nullAssessmentsRegistries() {
        // Arrange
        AssessmentsRegistry input = new AssessmentsRegistry();
        PagedModelAssessmentsRegistryEmbedded embedded = new PagedModelAssessmentsRegistryEmbedded();
        embedded.setAssessmentsRegistries(null);

        PagedModelAssessmentsRegistry paged = new PagedModelAssessmentsRegistry();
        paged.setEmbedded(embedded);

        when(assessmentsRegistryClient.getAssessmentsRegistrySearch(eq(input), eq(ACCESS_TOKEN), any(), any(), any()))
                .thenReturn(paged);

        // Act
        List<AssessmentsRegistry> result = service.getAssessmentsRegistrySearch(input);

        // Assert
        assertTrue(result.isEmpty());
    }
}