package it.gov.pagopa.payhub.activities.dto.assessments;

import it.gov.pagopa.pu.classification.dto.generated.AssessmentStatus;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentEventDTO {
  // Assessments
  private Long assessmentId;
  private Long organizationId;
  private String debtPositionTypeOrgCode;
  private AssessmentStatus status;
  private String assessmentName;
  private boolean printed;
  private boolean flagManualGeneration;
  private String operatorExternalUserId;
  private String iuv;
  private String iud;
  private String iur;

  // AssessmentsDetail
  private List<AssessmentsDetail> assessmentsDetailList;

}
