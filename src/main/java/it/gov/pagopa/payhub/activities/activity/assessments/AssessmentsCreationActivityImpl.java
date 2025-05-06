package it.gov.pagopa.payhub.activities.activity.assessments;

import it.gov.pagopa.payhub.activities.connector.classification.AssessmentService;
import it.gov.pagopa.pu.classification.dto.generated.Assessments;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Slf4j
@Component
public class AssessmentsCreationActivityImpl implements AssessmentsCreationActivity {

	private final AssessmentService assessmentService;

	public AssessmentsCreationActivityImpl(AssessmentService assessmentService) {
		this.assessmentService = assessmentService;
    }

	@Override
	public void createAssessments(Long receiptId) {
		log.info("Start creation assessments for receipt id: {}", receiptId);

		List<Assessments> res = assessmentService.createAssessments(receiptId);
		log.debug("Creation finished for receipt id: {}. Created or managed assessments: {}", receiptId, res.size());
	}
}
