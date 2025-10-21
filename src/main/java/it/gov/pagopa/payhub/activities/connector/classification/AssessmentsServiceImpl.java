package it.gov.pagopa.payhub.activities.connector.classification;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.classification.client.AssessmentClient;
import it.gov.pagopa.pu.classification.dto.generated.Assessments;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsRequestBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Lazy
@Service
@Slf4j
public class AssessmentsServiceImpl implements AssessmentsService {
    private final AssessmentClient assessmentClient;
    private final AuthnService authnService;

    public AssessmentsServiceImpl(AssessmentClient assessmentClient, AuthnService authnService) {
        this.assessmentClient = assessmentClient;
        this.authnService = authnService;
    }

    @Override
    public List<Assessments> createAssessments(Long receiptId) {
        return assessmentClient.createAssessments(receiptId, authnService.getAccessToken());
    }

    @Override
    public Optional<Assessments> findByOrganizationIdAndDebtPositionTypeOrgCodeAndAssessmentName(Long organizationId, String debtPositionTypeOrgCode, String assessmentName) {
        return Optional.ofNullable(
                assessmentClient.findByOrganizationIdAndDebtPositionTypeOrgCodeAndAssessmentName(
                        organizationId, debtPositionTypeOrgCode, assessmentName, authnService.getAccessToken()));
    }

    @Override
    public Assessments createAssessment(AssessmentsRequestBody assessmentsRequestBody) {
        return assessmentClient.createAssessment(assessmentsRequestBody, authnService.getAccessToken());
    }

    @Override
    public Assessments findAssessment(Long assessmentId) {
        return assessmentClient.findAssessment(assessmentId, authnService.getAccessToken());
    }


}
