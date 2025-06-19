package it.gov.pagopa.payhub.activities.connector.classification.client;

import it.gov.pagopa.payhub.activities.connector.classification.config.ClassificationApisHolder;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsRegistry;
import it.gov.pagopa.pu.classification.dto.generated.CreateAssessmentsRegistryByDebtPositionDTOAndIudRequest;
import it.gov.pagopa.pu.classification.dto.generated.PagedModelAssessmentsRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

  /**
   * Retrieves a paginated list of assessments registry based on the provided filters.   *
   */
  public PagedModelAssessmentsRegistry getAssessmentsRegistrySearch(
          Long organizationId, String debtPositionTypeOrgCode, String sectionCode,
          String officeCode, String assessmentCode, String operatingYear,
          String accessToken, Integer page, Integer size, List<String> sort) {
    Set<String> debtPositionTypeOrgCodes = new HashSet<>();
    if (debtPositionTypeOrgCode != null ) {
        debtPositionTypeOrgCodes.add(debtPositionTypeOrgCode);
    }
    return classificationApisHolder.getAssessmentsRegistrySearchControllerApi(accessToken)
            .crudAssessmentsRegistriesFindAssessmentsRegistriesByFilters(
                    organizationId,
                    debtPositionTypeOrgCodes,
                    sectionCode,null,
                    officeCode,null,
                    assessmentCode, null,
                    operatingYear,
                    null,
                    page, size, sort);
  }
}
