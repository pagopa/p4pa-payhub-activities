package it.gov.pagopa.payhub.activities.connector.classification;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.classification.client.AssessmentClient;
import it.gov.pagopa.pu.classification.dto.generated.Assessments;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
@Slf4j
public class AssessmentServiceImpl implements AssessmentService {
    private final AssessmentClient assessmentClient;
    private final AuthnService authnService;

    public AssessmentServiceImpl(AssessmentClient assessmentClient, AuthnService authnService) {
        this.assessmentClient = assessmentClient;
        this.authnService = authnService;
    }

    @Override
    public List<Assessments> createAssessments(Long receiptId) {
        return assessmentClient.createAssessments(receiptId, authnService.getAccessToken());
    }
}
