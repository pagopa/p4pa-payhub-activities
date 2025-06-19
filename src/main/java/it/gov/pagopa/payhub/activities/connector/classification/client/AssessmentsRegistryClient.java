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
   * Retrieves a paginated list of assessments registry based on the provided filters.
   *
   * @param request
   * @param accessToken
   * @param page default: 0
   * @param size default: 20
   * @param sort default: ["assessmentRegistryId,asc"]
   * @return PagedModelAssessmentsRegistry
   */
  public PagedModelAssessmentsRegistry getAssessmentsRegistrySearch(AssessmentsRegistry request, String accessToken, Integer page, Integer size, List<String> sort) {
    Set<String> debtPositionTypeOrgCodes = new HashSet<>();
    if (!request.getDebtPositionTypeOrgCode().isEmpty()) {
        debtPositionTypeOrgCodes.add(request.getDebtPositionTypeOrgCode());
    }
    return classificationApisHolder.getAssessmentsRegistrySearchControllerApi(accessToken)
            .crudAssessmentsRegistriesFindAssessmentsRegistriesByFilters(
                    request.getOrganizationId(),
                    debtPositionTypeOrgCodes,
                    request.getSectionCode(),null,
                    request.getOfficeCode(),null,
                    request.getAssessmentCode(), null,
                    request.getOperatingYear(),
                    null,
                    page, size, sort);
  }
}
