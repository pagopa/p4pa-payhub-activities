package it.gov.pagopa.payhub.activities.connector.classification;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.classification.client.AssessmentsDetailClient;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsDetail;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsDetailRequestBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
@Slf4j
public class AssessmentsDetailServiceImpl implements AssessmentsDetailService {
    private final AssessmentsDetailClient assessmentsDetailClient;
    private final AuthnService authnService;

    public AssessmentsDetailServiceImpl(AssessmentsDetailClient assessmentsDetailClient, AuthnService authnService) {
        this.assessmentsDetailClient = assessmentsDetailClient;
        this.authnService = authnService;
    }


    @Override
    public AssessmentsDetail createAssessmentDetail(AssessmentsDetailRequestBody assessmentsDetailRequestBody) {
        return assessmentsDetailClient.createAssessmentDetail(assessmentsDetailRequestBody, authnService.getAccessToken());
    }


}
