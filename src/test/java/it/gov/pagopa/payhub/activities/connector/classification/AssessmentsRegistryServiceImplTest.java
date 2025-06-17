package it.gov.pagopa.payhub.activities.connector.classification;

import static it.gov.pagopa.payhub.activities.util.faker.AssessmentsRegistryFaker.buildAssessmentsRegistry;
import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildDebtPositionDTO;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.classification.client.AssessmentsRegistryClient;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsRegistry;
import it.gov.pagopa.pu.classification.dto.generated.CreateAssessmentsRegistryByDebtPositionDTOAndIudRequest;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AssessmentsRegistryServiceImplTest {

  @Mock
  private AssessmentsRegistryClient assessmentsRegistryClientMock;
  @Mock
  private AuthnService authnServiceMock;

  private AssessmentsRegistryService assessmentsRegistryService;

  @BeforeEach
  void setUp() {
    assessmentsRegistryService = new AssessmentsRegistryServiceImpl(assessmentsRegistryClientMock, authnServiceMock);
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
        assessmentsRegistryClientMock,
        authnServiceMock);
  }

  @Test
  void givenValidRequestWhenCreateAssessmentsRegistryByDebtPositionDTOAndIudWithValidReceiptIdThenVerify() {
    DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
    List<String> iudList = List.of("IUD");
    CreateAssessmentsRegistryByDebtPositionDTOAndIudRequest request = CreateAssessmentsRegistryByDebtPositionDTOAndIudRequest.builder()
        .debtPositionDTO(debtPositionDTO).iudList(iudList).build();
    String accessToken = "accessToken";

    Mockito.when(authnServiceMock.getAccessToken())
        .thenReturn(accessToken);
    doNothing().when(assessmentsRegistryClientMock).createAssessmentsRegistryByDebtPositionDTOAndIud(request, accessToken);

    assessmentsRegistryService.createAssessmentsRegistryByDebtPositionDTOAndIudList(debtPositionDTO, iudList);

    verify(assessmentsRegistryClientMock, times(1))
        .createAssessmentsRegistryByDebtPositionDTOAndIud(request, accessToken);
  }

  @Test
  void givenValidRequestWhenCreateAssessmentsRegistry() {
    AssessmentsRegistry assessmentsRegistry = buildAssessmentsRegistry();

    String accessToken = "accessToken";

    Mockito.when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
    doNothing().when(assessmentsRegistryClientMock).createAssessmentsRegistry(assessmentsRegistry, accessToken);

    assessmentsRegistryService.createAssessmentsRegistry(assessmentsRegistry);

    verify(assessmentsRegistryClientMock, times(1))
            .createAssessmentsRegistry(assessmentsRegistry, accessToken);
  }

}