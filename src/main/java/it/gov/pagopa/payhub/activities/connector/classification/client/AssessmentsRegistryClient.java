package it.gov.pagopa.payhub.activities.connector.classification.client;

import it.gov.pagopa.payhub.activities.connector.classification.config.ClassificationApisHolder;
import it.gov.pagopa.payhub.activities.dto.assessments.AssessmentsRegistrySemanticKey;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsRegistry;
import it.gov.pagopa.pu.classification.dto.generated.CreateAssessmentsRegistryByDebtPositionDTOAndIudRequest;
import it.gov.pagopa.pu.classification.dto.generated.PagedModelAssessmentsRegistry;
import it.gov.pagopa.pu.classification.dto.generated.PagedModelAssessmentsRegistryEmbedded;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
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

    public void createAssessmentsRegistry(AssessmentsRegistry request, String accessToken) {
        classificationApisHolder.getAssessmentsRegistryApi(accessToken)
                .createAssessmentsRegistry(request);
    }

    /**
     * Retrieves a paginated list of assessments registry based on the provided filters.   *
     */
    public Optional<AssessmentsRegistry> searchAssessmentsRegistryBySemanticKey(
            AssessmentsRegistrySemanticKey semanticKey, String accessToken) {
        Set<String> debtPositionTypeOrgCodes = new HashSet<>();
        if (semanticKey.getDebtPositionTypeOrgCode() != null) {
            debtPositionTypeOrgCodes.add(semanticKey.getDebtPositionTypeOrgCode());
        }
        PagedModelAssessmentsRegistry pagedModelAssessmentsRegistry = classificationApisHolder.getAssessmentsRegistrySearchControllerApi(accessToken)
                .crudAssessmentsRegistriesFindAssessmentsRegistriesByFilters(
                        semanticKey.getOrganizationId(),
                        debtPositionTypeOrgCodes,
                        semanticKey.getSectionCode(), null,
                        semanticKey.getOfficeCode(), null,
                        semanticKey.getAssessmentCode(), null,
                        semanticKey.getOperatingYear(),
                        null,
                        0, 1, null);

        PagedModelAssessmentsRegistryEmbedded embedded = pagedModelAssessmentsRegistry.getEmbedded();
        if (embedded == null || CollectionUtils.isEmpty(embedded.getAssessmentsRegistries())) {
            return Optional.empty();
        }
        return Optional.of(embedded.getAssessmentsRegistries().getFirst());
    }
}
