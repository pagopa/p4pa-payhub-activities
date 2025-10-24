package it.gov.pagopa.payhub.activities.mapper.ingestionflow.assessmentsclassification;

import it.gov.pagopa.payhub.activities.dto.assessments.AssessmentEventDTO;
import it.gov.pagopa.pu.classification.dto.generated.Assessments;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class Assessments2AssessmentEventMapper {
  public AssessmentEventDTO map(Assessments assessments, List<AssessmentsDetail> assessmentsDetail) {
    return AssessmentEventDTO.builder()
      .assessmentId(assessments.getAssessmentId())
      .organizationId(assessments.getOrganizationId())
      .debtPositionTypeOrgCode(assessments.getDebtPositionTypeOrgCode())
      .status(assessments.getStatus())
      .assessmentName(assessments.getAssessmentName())
      .printed(assessments.getPrinted())
      .flagManualGeneration(assessments.getFlagManualGeneration())
      .operatorExternalUserId(assessments.getOperatorExternalUserId())
      .iuv(assessmentsDetail.getFirst().getIuv())
      .iud(assessmentsDetail.getFirst().getIud())
      .iur(assessmentsDetail.getFirst().getIur())
      //assessments detail
      .assessmentsDetailList(assessmentsDetail)
      .build();
  }
}
