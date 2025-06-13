package it.gov.pagopa.payhub.activities.activity.assessments;

import static it.gov.pagopa.payhub.activities.util.faker.DebtPositionFaker.buildDebtPositionDTO;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;

import it.gov.pagopa.payhub.activities.connector.classification.AssessmentsRegistryService;
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
class AssessmentsRegistryCreationActivityImplTest {

  @Mock
  private AssessmentsRegistryService assessmentsRegistryServiceMock;

  private AssessmentsRegistryCreationActivity activity;

  @BeforeEach
  void setUp() {
    activity = new AssessmentsRegistryCreationActivityImpl(assessmentsRegistryServiceMock);
  }

  @AfterEach
  void verifyNoMoreInteractions(){
    Mockito.verifyNoMoreInteractions(assessmentsRegistryServiceMock);
  }

  @Test
  void whenCreateAssessmentsRegistryByDebtPositionDTOAndIudListThenSuccess() {
    DebtPositionDTO debtPositionDTO = buildDebtPositionDTO();
    List<String> iudList = List.of("IUD");

    doNothing().when(assessmentsRegistryServiceMock)
        .createAssessmentsRegistryByDebtPositionDTOAndIudList(debtPositionDTO, iudList);

    assertDoesNotThrow(() -> activity.createAssessmentsRegistryByDebtPositionDTOAndIudList(debtPositionDTO, iudList));

    Mockito.verify(assessmentsRegistryServiceMock, Mockito.times(1))
        .createAssessmentsRegistryByDebtPositionDTOAndIudList(debtPositionDTO, iudList);
  }
}