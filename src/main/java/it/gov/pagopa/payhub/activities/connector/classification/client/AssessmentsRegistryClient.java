package it.gov.pagopa.payhub.activities.connector.classification.client;

import it.gov.pagopa.payhub.activities.connector.classification.config.ClassificationApisHolder;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsRegistry;
import it.gov.pagopa.pu.classification.dto.generated.CreateAssessmentsRegistryByDebtPositionDTOAndIudRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
@Slf4j
public class AssessmentsRegistryClient {

  private final ClassificationApisHolder classificationApisHolder;

  public AssessmentsRegistryClient(ClassificationApisHolder classificationApisHolder) {
    this.classificationApisHolder = classificationApisHolder;
  }

  public void createAssessmentsRegistryByDebtPositionDTOAndIud(CreateAssessmentsRegistryByDebtPositionDTOAndIudRequest request,
      String accessToken) {
    classificationApisHolder.getAssessmentsRegistryApi(accessToken)
        .createAssessmentsRegistryByDebtPositionDTOAndIud(request);
  }

  public void createAssessmentsRegistry(AssessmentsRegistry request,String accessToken) {
    classificationApisHolder.getAssessmentsRegistryApi(accessToken)
            .createAssessmentsRegistry(request);
  }
}
