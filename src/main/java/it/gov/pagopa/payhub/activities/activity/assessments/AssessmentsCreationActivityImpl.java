package it.gov.pagopa.payhub.activities.activity.assessments;

import it.gov.pagopa.payhub.activities.connector.classification.AssessmentsService;
import it.gov.pagopa.pu.classification.dto.generated.Assessments;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Lazy
@Slf4j
@Component
public class AssessmentsCreationActivityImpl implements AssessmentsCreationActivity {

	private final AssessmentsService assessmentsService;

	public AssessmentsCreationActivityImpl(AssessmentsService assessmentsService) {
		this.assessmentsService = assessmentsService;
    }

	@Override
	public void createAssessments(Long receiptId) {
		log.info("Start creation assessments for receipt id: {}", receiptId);

		List<Assessments> res = assessmentsService.createAssessments(receiptId);
		log.debug("Creation finished for receipt id: {}. Created or managed assessments: {}", receiptId, res.size());
	}
}
