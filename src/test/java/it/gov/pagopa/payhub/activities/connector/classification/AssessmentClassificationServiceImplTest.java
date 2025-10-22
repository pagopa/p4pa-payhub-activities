package it.gov.pagopa.payhub.activities.connector.classification;

import it.gov.pagopa.payhub.activities.dto.assessments.AssessmentEventDTO;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.assessmentsclassification.Assessments2AssessmentEventMapper;
import it.gov.pagopa.payhub.activities.util.Utilities;
import it.gov.pagopa.pu.classification.dto.generated.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

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
	private Assessments2AssessmentEventMapper assessmentMapper;

	@Captor
	ArgumentCaptor<AssessmentsDetailRequestBody> assessmentsDetailRequestBodyArgumentCaptor;

	@InjectMocks
	private AssessmentClassificationServiceImpl service;

	private static Assessments assessments;
	private static AssessmentsDetail classifiedAsCashedAssessmentDetails;
	private static AssessmentsDetail classifiedAsReportedAssessmentDetails;
	private static AssessmentsDetail classifiedAsPaidAssessmentDetails;

	private static final Long ASSESSMENT_ID = 1L;
	private static final Long ORGANIZATION_ID = 3L;
	private static final String IUV = "testIUV";
	private static final String IUD = "testIUD";
	private static final String IUR = "testIUR";
	private static final String DEBT_POSITION_TYPE_ORG_CODE = "dbTypeOrgCode";
	private static final Long DEBT_POSITION_TYPE_ORG_ID = 5L;
	private static final String DEBTOR_FISCAL_CODE_HASH = "debtorFiscalCodeHash";
	private static final String SECTION_CODE = "";
	private static final Long AMOUNT_CENTS = 0L;
	private static final Boolean AMOUNT_SUBMITTED = true;

	private static final OffsetDateTime EXPECTED_DATE_TREASURY = LocalDate.of(2025, 3, 1).atStartOfDay().atZone(Utilities.ZONEID).toOffsetDateTime();
	private static final OffsetDateTime EXPECTED_DATE_REPORTING = LocalDate.of(2025, 2, 1).atStartOfDay().atZone(Utilities.ZONEID).toOffsetDateTime();
	private static final OffsetDateTime EXPECTED_DATE_RECEIPT = LocalDate.of(2025, 1, 1).atStartOfDay().atZone(Utilities.ZONEID).toOffsetDateTime();

	@BeforeAll
	static void setup() {
		assessments = new Assessments();
		assessments.setAssessmentId(ASSESSMENT_ID);
		assessments.setOrganizationId(ORGANIZATION_ID);

		classifiedAsCashedAssessmentDetails = buildClassifiedAssessmentsDetail(CASHED, EXPECTED_DATE_TREASURY);
		classifiedAsReportedAssessmentDetails = buildClassifiedAssessmentsDetail(REPORTED, EXPECTED_DATE_REPORTING);
		classifiedAsPaidAssessmentDetails = buildClassifiedAssessmentsDetail(PAID, EXPECTED_DATE_RECEIPT);
	}

	@AfterEach
	void verifyNoMoreInteractions(){
		Mockito.verifyNoMoreInteractions(
				assessmentService,
				assessmentsDetailService,
				classificationService,
				assessmentMapper
		);
	}

	private static AssessmentsDetail buildClassifiedAssessmentsDetail(
			ClassificationLabel label, OffsetDateTime classificationDate) {
		AssessmentsDetail classifiedAssessmentDetail = new AssessmentsDetail();
		classifiedAssessmentDetail.setAssessmentDetailId(ASSESSMENT_ID);
		classifiedAssessmentDetail.setAssessmentId(ASSESSMENT_ID);
		classifiedAssessmentDetail.setOrganizationId(ORGANIZATION_ID);
		classifiedAssessmentDetail.setIuv(IUV);
		classifiedAssessmentDetail.setIud(IUD);
		classifiedAssessmentDetail.setClassificationLabel(label);
		switch (label) {
			case CASHED ->
				classifiedAssessmentDetail.setDateTreasury(classificationDate);
			case REPORTED ->
				classifiedAssessmentDetail.setDateReporting(classificationDate);
			case PAID ->
				classifiedAssessmentDetail.setDateReceipt(classificationDate);
			default -> throw new IllegalStateException("Unexpected value: " + label);
		}
		return classifiedAssessmentDetail;
	}

	private static CollectionModelAssessmentsDetail buildCollectionModelAssessmentsDetail() {
		CollectionModelAssessmentsDetail collectionModelAssessmentsDetail = new CollectionModelAssessmentsDetail();
		List<AssessmentsDetail> assessmentsDetailList = new ArrayList<>();

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
		assessmentsDetailList.add(assessmentsDetail);

		collectionModelAssessmentsDetail.setEmbedded(
				new PagedModelAssessmentsDetailEmbedded(assessmentsDetailList)
		);
		return collectionModelAssessmentsDetail;
	}

	private static CollectionModelClassification buildCollectionModelClassification(ClassificationsEnum... labels) {
		CollectionModelClassification collectionModelClassification = new CollectionModelClassification();
		List<Classification> classificationList = new ArrayList<>();
		Arrays.stream(labels).forEach(
				label -> {
					Classification classification = new Classification();
					classification.organizationId(ORGANIZATION_ID);
					classification.setIuv(IUV);
					classification.setIud(IUD);
					classification.setLabel(label);
					classification.setPayDate(EXPECTED_DATE_RECEIPT.toLocalDate());
					classification.setRegulationDate(EXPECTED_DATE_REPORTING.toLocalDate());
					classification.setBillDate(EXPECTED_DATE_TREASURY.toLocalDate());
					classificationList.add(classification);
				}
		);
		collectionModelClassification.setEmbedded(
				new PagedModelClassificationEmbedded(classificationList)
		);
		return collectionModelClassification;
	}

	static Stream<CollectionModelAssessmentsDetail> testCollectionModelAssessmentsDetailsInputProvider() {
		CollectionModelAssessmentsDetail nullEmbedded = new CollectionModelAssessmentsDetail();
		nullEmbedded.setEmbedded(null);
		CollectionModelAssessmentsDetail nullAssessmentsDetailList = new CollectionModelAssessmentsDetail();
		nullAssessmentsDetailList.setEmbedded(new PagedModelAssessmentsDetailEmbedded(null));
		CollectionModelAssessmentsDetail emptyAssessmentsDetailList = new CollectionModelAssessmentsDetail();
		emptyAssessmentsDetailList.setEmbedded(new PagedModelAssessmentsDetailEmbedded(Collections.emptyList()));
		return Stream.of(
				null,
				nullEmbedded,
				nullAssessmentsDetailList,
				emptyAssessmentsDetailList
		);
	}

	@ParameterizedTest
	@MethodSource("testCollectionModelAssessmentsDetailsInputProvider")
	void whenClassifyAssessmentWithNoAssessmentsDetailsThenNull(CollectionModelAssessmentsDetail collectionModelAssessmentsDetail) {
		//Given

		//region mock stubbing
		Mockito.when(assessmentsDetailService.findAssessmentsDetailByOrganizationIdAndIuvAndIud(ORGANIZATION_ID, IUV, IUD))
				.thenReturn(collectionModelAssessmentsDetail);
		//endregion

		//When
		AssessmentEventDTO actualResult = service.classifyAssessment(ORGANIZATION_ID, IUV, IUD);

		//Then
		Assertions.assertNull(actualResult);
	}

	@Test
	void whenClassifyAssessmentWithNoAssessmentThenNull() {
		//Given
		CollectionModelAssessmentsDetail collectionModelAssessmentsDetail = buildCollectionModelAssessmentsDetail();

		//region mock stubbing
		Mockito.when(assessmentService.findAssessment(ASSESSMENT_ID))
				.thenReturn(null);
		Mockito.when(assessmentsDetailService.findAssessmentsDetailByOrganizationIdAndIuvAndIud(ORGANIZATION_ID, IUV, IUD))
				.thenReturn(collectionModelAssessmentsDetail);
		//endregion

		//When
		AssessmentEventDTO actualResult = service.classifyAssessment(ORGANIZATION_ID, IUV, IUD);

		//Then
		Assertions.assertNull(actualResult);
	}

	static Stream<CollectionModelClassification> testCollectionModelClassificationsInputProvider() {
		CollectionModelClassification nullEmbedded = new CollectionModelClassification();
		nullEmbedded.setEmbedded(null);
		CollectionModelClassification nullClassificationList = new CollectionModelClassification();
		nullClassificationList.setEmbedded(new PagedModelClassificationEmbedded(null));
		CollectionModelClassification emptyClassificationList = new CollectionModelClassification();
		emptyClassificationList.setEmbedded(new PagedModelClassificationEmbedded(Collections.emptyList()));
		return Stream.of(
				null,
				nullEmbedded,
				nullClassificationList,
				emptyClassificationList
		);
	}

	@ParameterizedTest
	@MethodSource("testCollectionModelClassificationsInputProvider")
	void whenClassifyAssessmentWithNoClassificationsThenNull(CollectionModelClassification collectionModelClassification) {
		//Given
		CollectionModelAssessmentsDetail collectionModelAssessmentsDetail = buildCollectionModelAssessmentsDetail();

		//region mock stubbing
		Mockito.when(assessmentService.findAssessment(ASSESSMENT_ID))
				.thenReturn(assessments);
		Mockito.when(assessmentsDetailService.findAssessmentsDetailByOrganizationIdAndIuvAndIud(ORGANIZATION_ID, IUV, IUD))
				.thenReturn(collectionModelAssessmentsDetail);
		Mockito.when(classificationService.findAllByOrganizationIdAndIuvAndIud(ORGANIZATION_ID, IUV, IUD))
				.thenReturn(collectionModelClassification);
		//endregion

		//When
		AssessmentEventDTO actualResult = service.classifyAssessment(ORGANIZATION_ID, IUV, IUD);

		//Then
		Assertions.assertNull(actualResult);
	}

	@Test
	void whenClassifyAssessmentAsCashedThenOk() {
		//Given
		CollectionModelAssessmentsDetail collectionModelAssessmentsDetail = buildCollectionModelAssessmentsDetail();
		CollectionModelClassification collectionModelClassification = buildCollectionModelClassification(RT_NO_IUF, RT_IUF, RT_TES);

		//region expected result
		AssessmentEventDTO expectedResult = new AssessmentEventDTO();
		expectedResult.setAssessmentId(ASSESSMENT_ID);
		expectedResult.setOrganizationId(ORGANIZATION_ID);
		expectedResult.setIuv(IUV);
		expectedResult.setIud(IUD);
		expectedResult.setAssessmentsDetailList(List.of(classifiedAsCashedAssessmentDetails));
		//endregion

		//region mock stubbing
		Mockito.when(assessmentService.findAssessment(ASSESSMENT_ID))
				.thenReturn(assessments);
		Mockito.when(assessmentsDetailService.findAssessmentsDetailByOrganizationIdAndIuvAndIud(ORGANIZATION_ID, IUV, IUD))
				.thenReturn(collectionModelAssessmentsDetail);
		Mockito.when(classificationService.findAllByOrganizationIdAndIuvAndIud(ORGANIZATION_ID, IUV, IUD))
				.thenReturn(collectionModelClassification);
		Mockito.when(assessmentsDetailService.updateAssessmentsDetail(Mockito.eq(ASSESSMENT_ID), Mockito.any()))
				.thenReturn(classifiedAsCashedAssessmentDetails);
		Mockito.when(assessmentMapper.map(assessments, List.of(classifiedAsCashedAssessmentDetails)))
				.thenReturn(expectedResult);
		//endregion

		//When
		AssessmentEventDTO actualResult = service.classifyAssessment(ORGANIZATION_ID, IUV, IUD);

		//Then
		Assertions.assertEquals(expectedResult, actualResult);
		Mockito.verify(assessmentsDetailService).updateAssessmentsDetail(Mockito.any(), assessmentsDetailRequestBodyArgumentCaptor.capture());
		AssessmentsDetailRequestBody assessmentsDetailRequestBody = assessmentsDetailRequestBodyArgumentCaptor.getValue();
		Assertions.assertEquals(CASHED, assessmentsDetailRequestBody.getClassificationLabel());
		Assertions.assertEquals(EXPECTED_DATE_TREASURY, assessmentsDetailRequestBody.getDateTreasury());
		Assertions.assertNull(assessmentsDetailRequestBody.getDateReceipt());
		Assertions.assertNull(assessmentsDetailRequestBody.getDateReporting());
	}

	@Test
	void whenClassifyAssessmentAsReportedThenOk() {
		//Given
		CollectionModelAssessmentsDetail collectionModelAssessmentsDetail = buildCollectionModelAssessmentsDetail();
		CollectionModelClassification collectionModelClassification = buildCollectionModelClassification(RT_NO_IUD, IUF_TES_DIV_IMP, RT_NO_IUF);

		//region expected result
		AssessmentEventDTO expectedResult = new AssessmentEventDTO();
		expectedResult.setAssessmentId(ASSESSMENT_ID);
		expectedResult.setOrganizationId(ORGANIZATION_ID);
		expectedResult.setIuv(IUV);
		expectedResult.setIud(IUD);
		expectedResult.setAssessmentsDetailList(List.of(classifiedAsReportedAssessmentDetails));
		//endregion

		//region mock stubbing
		Mockito.when(assessmentService.findAssessment(ASSESSMENT_ID))
				.thenReturn(assessments);
		Mockito.when(assessmentsDetailService.findAssessmentsDetailByOrganizationIdAndIuvAndIud(ORGANIZATION_ID, IUV, IUD))
				.thenReturn(collectionModelAssessmentsDetail);
		Mockito.when(classificationService.findAllByOrganizationIdAndIuvAndIud(ORGANIZATION_ID, IUV, IUD))
				.thenReturn(collectionModelClassification);
		Mockito.when(assessmentsDetailService.updateAssessmentsDetail(Mockito.eq(ASSESSMENT_ID), Mockito.any()))
				.thenReturn(classifiedAsReportedAssessmentDetails);
		Mockito.when(assessmentMapper.map(assessments, List.of(classifiedAsReportedAssessmentDetails)))
				.thenReturn(expectedResult);
		//endregion

		//When
		AssessmentEventDTO actualResult = service.classifyAssessment(ORGANIZATION_ID, IUV, IUD);

		//Then
		Assertions.assertEquals(expectedResult, actualResult);
		Mockito.verify(assessmentsDetailService).updateAssessmentsDetail(Mockito.any(), assessmentsDetailRequestBodyArgumentCaptor.capture());
		AssessmentsDetailRequestBody assessmentsDetailRequestBody = assessmentsDetailRequestBodyArgumentCaptor.getValue();
		Assertions.assertEquals(REPORTED, assessmentsDetailRequestBody.getClassificationLabel());
		Assertions.assertEquals(EXPECTED_DATE_REPORTING, assessmentsDetailRequestBody.getDateReporting());
		Assertions.assertNull(assessmentsDetailRequestBody.getDateTreasury());
		Assertions.assertNull(assessmentsDetailRequestBody.getDateReceipt());
	}

	@Test
	void whenClassifyAssessmentAsPaidThenOk() {
		//Given
		CollectionModelAssessmentsDetail collectionModelAssessmentsDetail = buildCollectionModelAssessmentsDetail();
		CollectionModelClassification collectionModelClassification = buildCollectionModelClassification(RT_NO_IUF, RT_NO_IUD, TES_NO_IUF_OR_IUV);

		//region expected result
		AssessmentEventDTO expectedResult = new AssessmentEventDTO();
		expectedResult.setAssessmentId(ASSESSMENT_ID);
		expectedResult.setOrganizationId(ORGANIZATION_ID);
		expectedResult.setIuv(IUV);
		expectedResult.setIud(IUD);
		expectedResult.setAssessmentsDetailList(List.of(classifiedAsPaidAssessmentDetails));
		//endregion

		//region mock stubbing
		Mockito.when(assessmentService.findAssessment(ASSESSMENT_ID))
				.thenReturn(assessments);
		Mockito.when(assessmentsDetailService.findAssessmentsDetailByOrganizationIdAndIuvAndIud(ORGANIZATION_ID, IUV, IUD))
				.thenReturn(collectionModelAssessmentsDetail);
		Mockito.when(classificationService.findAllByOrganizationIdAndIuvAndIud(ORGANIZATION_ID, IUV, IUD))
				.thenReturn(collectionModelClassification);
		Mockito.when(assessmentsDetailService.updateAssessmentsDetail(Mockito.eq(ASSESSMENT_ID), Mockito.any()))
				.thenReturn(classifiedAsPaidAssessmentDetails);
		Mockito.when(assessmentMapper.map(assessments, List.of(classifiedAsPaidAssessmentDetails)))
				.thenReturn(expectedResult);
		//endregion

		//When
		AssessmentEventDTO actualResult = service.classifyAssessment(ORGANIZATION_ID, IUV, IUD);

		//Then
		Assertions.assertEquals(expectedResult, actualResult);
		Mockito.verify(assessmentsDetailService).updateAssessmentsDetail(Mockito.any(), assessmentsDetailRequestBodyArgumentCaptor.capture());
		AssessmentsDetailRequestBody assessmentsDetailRequestBody = assessmentsDetailRequestBodyArgumentCaptor.getValue();
		Assertions.assertEquals(PAID, assessmentsDetailRequestBody.getClassificationLabel());
		Assertions.assertEquals(EXPECTED_DATE_RECEIPT, assessmentsDetailRequestBody.getDateReceipt());
		Assertions.assertNull(assessmentsDetailRequestBody.getDateTreasury());
		Assertions.assertNull(assessmentsDetailRequestBody.getDateReporting());
	}
}