package it.gov.pagopa.payhub.activities.activity.assessments;

import it.gov.pagopa.payhub.activities.connector.classification.AssessmentsRegistryService;
import it.gov.pagopa.pu.classification.dto.generated.DebtPositionDTO;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Slf4j
@Component
public class AssessmentsRegistryCreationActivityImpl implements AssessmentsRegistryCreationActivity {

  private final AssessmentsRegistryService assessmentsRegistryService;

  public AssessmentsRegistryCreationActivityImpl(
      AssessmentsRegistryService assessmentsRegistryService) {
    this.assessmentsRegistryService = assessmentsRegistryService;
  }

  @Override
  public void createAssessmentsRegistryByDebtPositionDTOAndIudList(DebtPositionDTO debtPositionDTO, List<String> iudList) {
    log.debug("Start creation assessments registry by debtPositionDTO {} and IUD list {}", debtPositionDTO, iudList);
    assessmentsRegistryService.createAssessmentsRegistryByDebtPositionDTOAndIudList(debtPositionDTO, iudList);
  }
}
