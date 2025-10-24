package it.gov.pagopa.payhub.activities.service.classifications.assessments;

import it.gov.pagopa.payhub.activities.connector.classification.AssessmentsDetailService;
import it.gov.pagopa.payhub.activities.connector.classification.AssessmentsService;
import it.gov.pagopa.payhub.activities.connector.classification.ClassificationService;
import it.gov.pagopa.payhub.activities.dto.assessments.AssessmentEventDTO;
import it.gov.pagopa.payhub.activities.dto.assessments.AssessmentsClassificationSemanticKeyDTO;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.assessmentsclassification.Assessments2AssessmentEventMapper;
import it.gov.pagopa.payhub.activities.util.Utilities;
import it.gov.pagopa.pu.classification.dto.generated.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Lazy
@Slf4j
@Service
public class AssessmentClassificationServiceImpl implements AssessmentClassificationService {

	private final AssessmentsService assessmentsService;
	private final AssessmentsDetailService assessmentsDetailService;
	private final ClassificationService classificationService;
	private final AssessmentsClassificationLabelService assessmentsClassificationLabelService;
	private final Assessments2AssessmentEventMapper assessmentMapper;

	public AssessmentClassificationServiceImpl(
			AssessmentsService assessmentsService,
			AssessmentsDetailService assessmentsDetailService,
			ClassificationService classificationService,
			AssessmentsClassificationLabelService assessmentsClassificationLabelService,
			Assessments2AssessmentEventMapper assessmentMapper) {
		this.assessmentsService = assessmentsService;
		this.assessmentsDetailService = assessmentsDetailService;
		this.classificationService = classificationService;
		this.assessmentsClassificationLabelService = assessmentsClassificationLabelService;
		this.assessmentMapper = assessmentMapper;
	}

	@Override
	public AssessmentEventDTO classifyAssessment(AssessmentsClassificationSemanticKeyDTO assessmentsClassificationSemanticKeyDTO) {
		Long organizationId = assessmentsClassificationSemanticKeyDTO.getOrgId();
		String iuv = assessmentsClassificationSemanticKeyDTO.getIuv();
		String iud = assessmentsClassificationSemanticKeyDTO.getIud();

		List<AssessmentsDetail> assessmentsDetailList = assessmentsDetailService.findAssessmentsDetailByOrganizationIdAndIuvAndIud(
				organizationId,
				iuv,
				iud
		);
		if (assessmentsDetailList.isEmpty()) {
			log.info("Assessment details not found for organizationId: {}, iuv: {}, iud: {}", organizationId, iuv, iud);
			return null;
		}

		Assessments assessment = assessmentsService.findAssessment(assessmentsDetailList.getFirst().getAssessmentId());
		if (assessment == null) {
			log.info("Assessment not found for assessmentId: {}", assessmentsDetailList.getFirst().getAssessmentId());
			return null;
		}

		List<Classification> classificationList = classificationService.findAllByOrganizationIdAndIuvAndIud(
				organizationId,
				iuv,
				iud
		);
		if (classificationList.isEmpty()) {
			log.info("Classifications not found for organizationId: {}, iuv: {}, iud: {}", organizationId, iuv, iud);
			return null;
		}

		ClassificationLabel label = assessmentsClassificationLabelService.extractAssessmentsClassificationLabel(classificationList);

		assessmentsDetailList.forEach(ad -> {
			ad.setClassificationLabel(label);
			ad.setDateReceipt(classificationList.getFirst().getReceiptPaymentDateTime());
			ad.setDateReporting(Utilities.toOffsetDateTimeStartOfTheDay(classificationList.getFirst().getRegulationDate()));
			ad.setDateTreasury(Utilities.toOffsetDateTimeStartOfTheDay(classificationList.getFirst().getBillDate()));
			assessmentsDetailService.updateAssessmentsDetail(ad.getAssessmentDetailId(), ad);
		});

		return assessmentMapper.map(assessment, assessmentsDetailList);
	}
}
