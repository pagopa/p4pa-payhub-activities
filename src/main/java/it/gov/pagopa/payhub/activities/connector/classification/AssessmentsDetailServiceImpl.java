package it.gov.pagopa.payhub.activities.connector.classification;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.classification.client.AssessmentsDetailClient;
import it.gov.pagopa.payhub.activities.connector.classification.mapper.UpdateAssessmentsDetailRequestBodyMapper;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsDetail;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsDetailRequestBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Lazy
@Service
@Slf4j
public class AssessmentsDetailServiceImpl implements AssessmentsDetailService {
    private final AssessmentsDetailClient assessmentsDetailClient;
    private final AuthnService authnService;
    private final UpdateAssessmentsDetailRequestBodyMapper assessmentsDetailRequestBodyMapper;

    public AssessmentsDetailServiceImpl(AssessmentsDetailClient assessmentsDetailClient,
                                        UpdateAssessmentsDetailRequestBodyMapper assessmentsDetailRequestBodyMapper,
                                        AuthnService authnService) {
        this.assessmentsDetailClient = assessmentsDetailClient;
        this.assessmentsDetailRequestBodyMapper = assessmentsDetailRequestBodyMapper;
        this.authnService = authnService;
    }


    @Override
    public AssessmentsDetail createAssessmentDetail(AssessmentsDetailRequestBody assessmentsDetailRequestBody) {
        return assessmentsDetailClient.createAssessmentDetail(assessmentsDetailRequestBody, authnService.getAccessToken());
    }


    @Override
    public List<AssessmentsDetail> findAssessmentsDetailByOrganizationIdAndIuvAndIud(Long organizationId, String iuv, String iud) {
        return assessmentsDetailClient.findAssessmentsDetailByOrganizationIdAndIuvAndIud(
                organizationId,
                iuv,
                iud,
                authnService.getAccessToken()
        );
    }

    public AssessmentsDetail updateAssessmentsDetail(Long assessmentDetailId, AssessmentsDetailRequestBody updateRequest) {
        return assessmentsDetailClient.updateAssessmentsDetail(
                assessmentDetailId,
                updateRequest,
                authnService.getAccessToken()
        );
    }

    public AssessmentsDetail updateAssessmentsDetail(Long assessmentDetailId, AssessmentsDetail updatedAssessmentsDetail) {
        return this.updateAssessmentsDetail(
                assessmentDetailId,
                assessmentsDetailRequestBodyMapper.mapFromAssessmentsDetail(updatedAssessmentsDetail)
        );
    }
}
