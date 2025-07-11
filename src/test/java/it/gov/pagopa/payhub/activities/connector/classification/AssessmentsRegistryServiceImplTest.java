package it.gov.pagopa.payhub.activities.connector.classification;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.classification.client.AssessmentsRegistryClient;
import it.gov.pagopa.payhub.activities.dto.assessments.AssessmentsRegistrySemanticKey;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsRegistry;
import it.gov.pagopa.pu.classification.dto.generated.CreateAssessmentsRegistryByDebtPositionDTOAndIudRequest;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssessmentsRegistryServiceImplTest {

    @Mock
    private AssessmentsRegistryClient assessmentsRegistryClientMock;
    @Mock
    private AuthnService authnServiceMock;

    @InjectMocks
    private AssessmentsRegistryServiceImpl service;

    private static final String ACCESS_TOKEN = "mock-token";

    @BeforeEach
    void setUp() {
        when(authnServiceMock.getAccessToken()).thenReturn(ACCESS_TOKEN);
    }

    @AfterEach
    void verifyNoMoreInteractions(){
        Mockito.verifyNoMoreInteractions(
                assessmentsRegistryClientMock,
                authnServiceMock);
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
        verify(assessmentsRegistryClientMock).createAssessmentsRegistryByDebtPositionDTOAndIud(
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
        verify(assessmentsRegistryClientMock).createAssessmentsRegistry(registry, ACCESS_TOKEN);
    }

    @Test
    void testSearchAssessmentsRegistryBySemanticKey_found() {
        // Arrange
        Optional<AssessmentsRegistry> expectedResult = Optional.empty();
        AssessmentsRegistrySemanticKey registrySemanticKey = new AssessmentsRegistrySemanticKey();
        String token = "mockToken";

        when(authnServiceMock.getAccessToken()).thenReturn(token);
        when(assessmentsRegistryClientMock.searchAssessmentsRegistryBySemanticKey(
                registrySemanticKey,
                token))
                .thenReturn(expectedResult);

        // Act
        Optional<AssessmentsRegistry> result = service.searchAssessmentsRegistryBySemanticKey(registrySemanticKey);

        // Assert
        assertSame(expectedResult, result);
    }

}