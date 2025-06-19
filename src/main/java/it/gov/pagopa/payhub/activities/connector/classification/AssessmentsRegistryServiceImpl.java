package it.gov.pagopa.payhub.activities.connector.classification;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.classification.client.AssessmentsRegistryClient;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsRegistry;
import it.gov.pagopa.pu.classification.dto.generated.CreateAssessmentsRegistryByDebtPositionDTOAndIudRequest;
import it.gov.pagopa.pu.classification.dto.generated.PagedModelAssessmentsRegistry;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

  @Override
  public void createAssessmentsRegistry(AssessmentsRegistry assessmentsRegistry) {
    assessmentsRegistryClient.createAssessmentsRegistry(assessmentsRegistry, authnService.getAccessToken());
  }

  @Override
  public Optional<AssessmentsRegistry> searchAssessmentsRegistryByBusinessKey(AssessmentsRegistry assessmentsRegistry) {
    PagedModelAssessmentsRegistry pagedModelAssessmentsRegistry =
            assessmentsRegistryClient.getAssessmentsRegistrySearch(assessmentsRegistry, authnService.getAccessToken(), 0, 1, null);

    if (pagedModelAssessmentsRegistry.getEmbedded() == null ||
        pagedModelAssessmentsRegistry.getEmbedded().getAssessmentsRegistries() == null){
      return Optional.empty();
    }
    return Optional.of(pagedModelAssessmentsRegistry.getEmbedded().getAssessmentsRegistries().getFirst());
  }

}
