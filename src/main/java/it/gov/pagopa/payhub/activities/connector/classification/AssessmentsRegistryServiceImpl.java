package it.gov.pagopa.payhub.activities.connector.classification;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.classification.client.AssessmentsRegistryClient;
import it.gov.pagopa.pu.classification.dto.generated.CreateAssessmentsRegistryByDebtPositionDTOAndIudRequest;
import it.gov.pagopa.pu.classification.dto.generated.DebtPositionDTO;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
@Slf4j
public class AssessmentsRegistryServiceImpl implements AssessmentsRegistryService{

  private final AssessmentsRegistryClient assessmentsRegistryClient;
  private final AuthnService authnService;

  public AssessmentsRegistryServiceImpl(AssessmentsRegistryClient assessmentsRegistryClient,
      AuthnService authnService) {
    this.assessmentsRegistryClient = assessmentsRegistryClient;
    this.authnService = authnService;
  }

  @Override
  public void createAssessmentsRegistryByDebtPositionDTOAndIudList(DebtPositionDTO debtPositionDTO, List<String> iudList) {
    assessmentsRegistryClient.createAssessmentsRegistryByDebtPositionDTOAndIud(
        CreateAssessmentsRegistryByDebtPositionDTOAndIudRequest.builder()
            .debtPositionDTO(debtPositionDTO)
            .iudList(iudList)
            .build(),
        authnService.getAccessToken());
  }
}
