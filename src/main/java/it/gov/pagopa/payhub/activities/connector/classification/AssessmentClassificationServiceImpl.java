package it.gov.pagopa.payhub.activities.connector.classification;

import it.gov.pagopa.payhub.activities.dto.assessments.AssessmentEventDTO;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.assessmentsclassification.Assessments2AssessmentEventMapper;
import it.gov.pagopa.payhub.activities.util.Utilities;
import it.gov.pagopa.pu.classification.dto.generated.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Lazy
@Slf4j
@Service
public class AssessmentClassificationServiceImpl implements AssessmentClassificationService {

	private final AssessmentsService assessmentService;
	private final AssessmentsDetailService assessmentsDetailService;
	private final ClassificationService classificationService;
	private final Assessments2AssessmentEventMapper assessmentMapper;

	public AssessmentClassificationServiceImpl(AssessmentsService assessmentService,
											   AssessmentsDetailService assessmentsDetailService,
											   ClassificationService classificationService,
											   Assessments2AssessmentEventMapper assessmentMapper) {
		this.assessmentService = assessmentService;
		this.assessmentsDetailService = assessmentsDetailService;
		this.classificationService = classificationService;
		this.assessmentMapper = assessmentMapper;
	}

	@Override
	public AssessmentEventDTO classifyAssessment(Long organizationId, String iuv, String iud) {
		CollectionModelAssessmentsDetail collectionModelAssessmentsDetail = assessmentsDetailService.findAssessmentsDetailByOrganizationIdAndIuvAndIud(
				organizationId,
				iuv,
				iud
		);
		if (collectionModelAssessmentsDetail == null ||
				collectionModelAssessmentsDetail.getEmbedded() == null ||
				collectionModelAssessmentsDetail.getEmbedded().getAssessmentsDetails() == null ||
				collectionModelAssessmentsDetail.getEmbedded().getAssessmentsDetails().isEmpty()
		) {
			log.info("Assessment details not found for organizationId: {}, iuv: {}, iud: {}", organizationId, iuv, iud);
			return null;
		}
		List<AssessmentsDetail> assessmentsDetailList = collectionModelAssessmentsDetail.getEmbedded().getAssessmentsDetails();

		Assessments assessment = assessmentService.findAssessment(assessmentsDetailList.getFirst().getAssessmentId());
		if (assessment == null) {
			log.info("Assessment not found for organizationId: {}, iuv: {}, iud: {}", organizationId, iuv, iud);
			return null;
		}

		CollectionModelClassification collectionModelClassification = classificationService.findAllByOrganizationIdAndIuvAndIud(
				organizationId,
				iuv,
				iud
		);
		if (collectionModelClassification == null ||
				collectionModelClassification.getEmbedded() == null ||
				collectionModelClassification.getEmbedded().getClassifications() == null ||
				collectionModelClassification.getEmbedded().getClassifications().isEmpty()) {
			log.info("Classifications not found for organizationId: {}, iuv: {}, iud: {}", organizationId, iuv, iud);
			return null;
		}
		List<Classification> classificationList = collectionModelClassification.getEmbedded().getClassifications();

		List<AssessmentsDetail> updatedAssessmentsDetailList = classifyAssessmentsDetails(assessmentsDetailList, classificationList);

		return assessmentMapper.map(assessment, updatedAssessmentsDetailList);
	}

	private List<AssessmentsDetail> classifyAssessmentsDetails(List<AssessmentsDetail> assessmentsDetailList, List<Classification> classificationList) {
		ClassificationLabel classificationLabel = null;
		LocalDate classificationLabelDate = null;
		for (Classification classification: classificationList) {
			switch (classification.getLabel()) {
				case RT_NO_IUF, RT_NO_IUD -> {
					if(!ClassificationLabel.REPORTED.equals(classificationLabel)) {
						classificationLabel = ClassificationLabel.PAID;
						classificationLabelDate = classification.getPayDate();
					}
				}
				case RT_IUF, IUF_TES_DIV_IMP -> {
					classificationLabel = ClassificationLabel.REPORTED;
					classificationLabelDate = classification.getRegulationDate();
				}
				case RT_TES, RT_IUF_TES, IUD_RT_IUF_TES -> {
					return updateAssessmentsDetails(assessmentsDetailList, ClassificationLabel.CASHED, classification.getBillDate());
				}
				default ->
					log.debug("Unused label for assessment classification: label {}, classificationId {}", classification.getLabel(), classification.getClassificationId());
			}
		}
		return updateAssessmentsDetails(assessmentsDetailList, classificationLabel, classificationLabelDate);
	}

	private List<AssessmentsDetail> updateAssessmentsDetails(
			List<AssessmentsDetail> assessmentsDetailList,
			ClassificationLabel classificationLabel, LocalDate classificationLabelDate) {
		List<AssessmentsDetail> updatedAssessmentsDetailList = new ArrayList<>();

		assessmentsDetailList.forEach(ad -> {
			AssessmentsDetailRequestBody assessmentsDetailRequestBody = AssessmentsDetailRequestBody.builder()
					.assessmentId(ad.getAssessmentId())
					.organizationId(ad.getOrganizationId())
					.debtPositionTypeOrgId(ad.getDebtPositionTypeOrgId())
					.debtPositionTypeOrgCode(ad.getDebtPositionTypeOrgCode())
					.iuv(ad.getIuv())
					.iud(ad.getIud())
					.iur(ad.getIur())
					.debtorFiscalCodeHash(ad.getDebtorFiscalCodeHash())
					.sectionCode(ad.getSectionCode())
					.amountCents(ad.getAmountCents())
					.amountSubmitted(ad.getAmountSubmitted())
					.dateReceipt(ClassificationLabel.PAID.equals(classificationLabel) ?
							classificationLabelDate.atStartOfDay().atZone(Utilities.ZONEID).toOffsetDateTime() : ad.getDateReceipt())
					.dateReporting(ClassificationLabel.REPORTED.equals(classificationLabel) ?
							classificationLabelDate.atStartOfDay().atZone(Utilities.ZONEID).toOffsetDateTime() : ad.getDateReporting())
					.dateTreasury(ClassificationLabel.CASHED.equals(classificationLabel) ?
							classificationLabelDate.atStartOfDay().atZone(Utilities.ZONEID).toOffsetDateTime() : ad.getDateTreasury())
					.classificationLabel(classificationLabel)
					.build();
			updatedAssessmentsDetailList.add(
					assessmentsDetailService.updateAssessmentsDetail(
						ad.getAssessmentDetailId(),
						assessmentsDetailRequestBody
					)
			);
		});

		return updatedAssessmentsDetailList;
	}
}
