package it.gov.pagopa.payhub.activities.service.classifications.assessments;

import it.gov.pagopa.payhub.activities.connector.classification.AssessmentsDetailService;
import it.gov.pagopa.payhub.activities.connector.classification.AssessmentsService;
import it.gov.pagopa.payhub.activities.connector.classification.ClassificationService;
import it.gov.pagopa.payhub.activities.dto.assessments.AssessmentEventDTO;
import it.gov.pagopa.payhub.activities.dto.assessments.AssessmentsClassificationSemanticKeyDTO;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.assessmentsclassification.Assessments2AssessmentEventMapper;
import it.gov.pagopa.payhub.activities.util.Utilities;
import it.gov.pagopa.pu.classification.dto.generated.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static it.gov.pagopa.pu.classification.dto.generated.ClassificationLabel.*;
import static it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum.*;

@ExtendWith(MockitoExtension.class)
class AssessmentClassificationServiceImplTest {
	@Mock
	private AssessmentsService assessmentService;
	@Mock
	private AssessmentsDetailService assessmentsDetailService;
	@Mock
	private ClassificationService classificationService;
	@Mock
	private AssessmentsClassificationLabelService assessmentsClassificationLabelService;
	@Mock
	private Assessments2AssessmentEventMapper assessmentMapper;

	@InjectMocks
	private AssessmentClassificationServiceImpl service;

	private static Assessments assessments;

	private static final Long ASSESSMENT_ID = 1L;
	private static final Long ORGANIZATION_ID = 3L;
	private static final String IUV = "testIUV";
	private static final String IUD = "testIUD";
	private static final String IUR = "testIUR";
	private static final String DEBT_POSITION_TYPE_ORG_CODE = "dbTypeOrgCode";
	private static final Long DEBT_POSITION_TYPE_ORG_ID = 5L;
	private static final String DEBTOR_FISCAL_CODE_HASH = "debtorFiscalCodeHash";
	private static final String SECTION_CODE = "testSECTIONCODE";
	private static final Long AMOUNT_CENTS = 0L;
	private static final Boolean AMOUNT_SUBMITTED = true;

	private static final AssessmentsClassificationSemanticKeyDTO  assessmentsClassificationSemanticKeyDTO =
			new AssessmentsClassificationSemanticKeyDTO(
					ORGANIZATION_ID,
					IUV,
					IUD
			);

	private static final OffsetDateTime EXPECTED_DATE_TREASURY = LocalDate.of(2025, 3, 1).atStartOfDay(Utilities.ZONEID).toOffsetDateTime();
	private static final OffsetDateTime EXPECTED_DATE_REPORTING = LocalDate.of(2025, 2, 1).atStartOfDay(Utilities.ZONEID).toOffsetDateTime();
	private static final OffsetDateTime EXPECTED_DATE_RECEIPT = LocalDate.of(2025, 1, 1).atStartOfDay(Utilities.ZONEID).toOffsetDateTime();

	@BeforeAll
	static void setup() {
		assessments = new Assessments();
		assessments.setAssessmentId(ASSESSMENT_ID);
		assessments.setOrganizationId(ORGANIZATION_ID);
	}

	@AfterEach
	void verifyNoMoreInteractions(){
		Mockito.verifyNoMoreInteractions(
				assessmentService,
				assessmentsDetailService,
				classificationService,
				assessmentsClassificationLabelService,
				assessmentMapper
		);
	}

	private static AssessmentsDetail buildAssessmentsDetail() {
		AssessmentsDetail assessmentsDetail = new AssessmentsDetail();
		assessmentsDetail.setAssessmentDetailId(ASSESSMENT_ID);
		assessmentsDetail.setAssessmentId(ASSESSMENT_ID);
		assessmentsDetail.organizationId(ORGANIZATION_ID);
		assessmentsDetail.setIuv(IUV);
		assessmentsDetail.setIud(IUD);
		assessmentsDetail.setIur(IUR);
		assessmentsDetail.setDebtPositionTypeOrgCode(DEBT_POSITION_TYPE_ORG_CODE);
		assessmentsDetail.setDebtPositionTypeOrgId(DEBT_POSITION_TYPE_ORG_ID);
		assessmentsDetail.setDebtorFiscalCodeHash(DEBTOR_FISCAL_CODE_HASH.getBytes(StandardCharsets.UTF_8));
		assessmentsDetail.setSectionCode(SECTION_CODE);
		assessmentsDetail.setAmountCents(AMOUNT_CENTS);
		assessmentsDetail.setAmountSubmitted(AMOUNT_SUBMITTED);
		return assessmentsDetail;
	}

	private static AssessmentsDetail buildClassifiedAssessmentsDetail(ClassificationLabel label) {
		AssessmentsDetail classifiedAssessmentDetail = buildAssessmentsDetail();
		classifiedAssessmentDetail.setClassificationLabel(label);
		classifiedAssessmentDetail.setDateTreasury(EXPECTED_DATE_TREASURY);
		classifiedAssessmentDetail.setDateReporting(EXPECTED_DATE_REPORTING);
		classifiedAssessmentDetail.setDateReceipt(EXPECTED_DATE_RECEIPT);
		return classifiedAssessmentDetail;
	}

	private static List<Classification> buildClassificationList(ClassificationsEnum... labels) {
		List<Classification> classificationList = new ArrayList<>();
		Arrays.stream(labels).forEach(
				label -> {
					Classification classification = new Classification();
					classification.organizationId(ORGANIZATION_ID);
					classification.setIuv(IUV);
					classification.setIud(IUD);
					classification.setLabel(label);
					classification.setReceiptPaymentDateTime(EXPECTED_DATE_RECEIPT);
					classification.setRegulationDate(EXPECTED_DATE_REPORTING.toLocalDate());
					classification.setBillDate(EXPECTED_DATE_TREASURY.toLocalDate());
					classificationList.add(classification);
				}
		);
		return classificationList;
	}

	@Test
	void whenClassifyAssessmentWithNoAssessmentsDetailsThenNull() {
		//Given

		//region mock stubbing
		Mockito.when(assessmentsDetailService.findAssessmentsDetailByOrganizationIdAndIuvAndIud(ORGANIZATION_ID, IUV, IUD))
				.thenReturn(Collections.emptyList());
		//endregion

		//When
		AssessmentEventDTO actualResult = service.classifyAssessment(assessmentsClassificationSemanticKeyDTO);

		//Then
		Assertions.assertNull(actualResult);
	}

	@Test
	void whenClassifyAssessmentWithNoAssessmentThenNull() {
		//Given
		List<AssessmentsDetail> assessmentsDetailList = List.of(buildAssessmentsDetail());

		//region mock stubbing
		Mockito.when(assessmentService.findAssessment(ASSESSMENT_ID))
				.thenReturn(null);
		Mockito.when(assessmentsDetailService.findAssessmentsDetailByOrganizationIdAndIuvAndIud(ORGANIZATION_ID, IUV, IUD))
				.thenReturn(assessmentsDetailList);
		//endregion

		//When
		AssessmentEventDTO actualResult = service.classifyAssessment(assessmentsClassificationSemanticKeyDTO);

		//Then
		Assertions.assertNull(actualResult);
	}

	@Test
	void whenClassifyAssessmentWithNoClassificationsThenNull() {
		//Given
		List<AssessmentsDetail> assessmentsDetailList = List.of(buildAssessmentsDetail());
		List<Classification> classificationList = buildClassificationList();

		//region mock stubbing
		Mockito.when(assessmentService.findAssessment(ASSESSMENT_ID))
				.thenReturn(assessments);
		Mockito.when(assessmentsDetailService.findAssessmentsDetailByOrganizationIdAndIuvAndIud(ORGANIZATION_ID, IUV, IUD))
				.thenReturn(assessmentsDetailList);
		Mockito.when(classificationService.findAllByOrganizationIdAndIuvAndIud(ORGANIZATION_ID, IUV, IUD))
				.thenReturn(classificationList);
		//endregion

		//When
		AssessmentEventDTO actualResult = service.classifyAssessment(assessmentsClassificationSemanticKeyDTO);

		//Then
		Assertions.assertNull(actualResult);
	}

	@Test
	void whenClassifyAssessmentAsCashedThenOk() {
		//Given
		List<AssessmentsDetail> assessmentsDetailList = List.of(buildAssessmentsDetail());
		List<Classification> classificationList = buildClassificationList(RT_NO_IUF, RT_IUF, RT_TES);
		AssessmentsDetail classifiedAssessmentsDetail = buildClassifiedAssessmentsDetail(CASHED);

		//region expected result
		AssessmentEventDTO expectedResult = new AssessmentEventDTO();
		expectedResult.setAssessmentId(ASSESSMENT_ID);
		expectedResult.setOrganizationId(ORGANIZATION_ID);
		expectedResult.setIuv(IUV);
		expectedResult.setIud(IUD);
		expectedResult.setAssessmentsDetailList(List.of(classifiedAssessmentsDetail));
		//endregion

		//region mock stubbing
		Mockito.when(assessmentService.findAssessment(ASSESSMENT_ID))
				.thenReturn(assessments);
		Mockito.when(assessmentsDetailService.findAssessmentsDetailByOrganizationIdAndIuvAndIud(ORGANIZATION_ID, IUV, IUD))
				.thenReturn(assessmentsDetailList);
		Mockito.when(classificationService.findAllByOrganizationIdAndIuvAndIud(ORGANIZATION_ID, IUV, IUD))
				.thenReturn(classificationList);
		Mockito.when(assessmentsClassificationLabelService.extractAssessmentsClassificationLabel(Mockito.anyList()))
				.thenReturn(CASHED);
		Mockito.when(assessmentsDetailService.updateAssessmentsDetail(ASSESSMENT_ID, classifiedAssessmentsDetail))
				.thenReturn(classifiedAssessmentsDetail);
		Mockito.when(assessmentMapper.map(assessments, List.of(classifiedAssessmentsDetail)))
				.thenReturn(expectedResult);
		//endregion

		//When
		AssessmentEventDTO actualResult = service.classifyAssessment(assessmentsClassificationSemanticKeyDTO);

		//Then
		Assertions.assertEquals(expectedResult, actualResult);
	}

}