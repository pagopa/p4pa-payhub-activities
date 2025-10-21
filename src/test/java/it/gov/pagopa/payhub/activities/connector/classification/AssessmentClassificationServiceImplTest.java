package it.gov.pagopa.payhub.activities.connector.classification;

import it.gov.pagopa.payhub.activities.dto.assessments.AssessmentEventDTO;
import it.gov.pagopa.payhub.activities.enums.assessment.AssessmentClassificationLabelEnum;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.assessmentsclassification.Assessments2AssessmentEventMapper;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum.*;
import static it.gov.pagopa.payhub.activities.enums.assessment.AssessmentClassificationLabelEnum.*;

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

	private final static Long assessmentId = 1L;
	private final static Long organizationId = 3L;
	private final static String iuv = "testIUV";
	private final static String iud = "testIUD";
	private final static String iur = "testIUR";
	private final static String debtPositionTypeOrgCode = "dbTypeOrgCode";
	private final static String debtorFiscalCodeHash = "debtorFiscalCodeHash";
	private final static String sectionCode = "";
	private final static Long amountCents = 0L;
	private final static Boolean amountSubmitted = true;

	@BeforeAll
	static void setup() {
		LocalDate expectedDateTreasury = LocalDate.of(2025, 3, 1);
		LocalDate expectedDateReporting = LocalDate.of(2025, 2, 1);
		LocalDate expectedDateReceipt = LocalDate.of(2025, 1, 1);

		assessments = new Assessments();
		assessments.setAssessmentId(assessmentId);
		assessments.setOrganizationId(organizationId);

		classifiedAsCashedAssessmentDetails = buildClassifiedAssessmentsDetail(CASHED, expectedDateTreasury);
		classifiedAsReportedAssessmentDetails = buildClassifiedAssessmentsDetail(REPORTED, expectedDateReporting);
		classifiedAsPaidAssessmentDetails = buildClassifiedAssessmentsDetail(PAID, expectedDateReceipt);
	}

	private static AssessmentsDetail buildClassifiedAssessmentsDetail(
			AssessmentClassificationLabelEnum label, LocalDate classificationDate) {
		AssessmentsDetail classifiedAssessmentDetail = new AssessmentsDetail();
		classifiedAssessmentDetail.setAssessmentDetailId(assessmentId);
		classifiedAssessmentDetail.setAssessmentId(assessmentId);
		classifiedAssessmentDetail.setOrganizationId(organizationId);
		classifiedAssessmentDetail.setIuv(iuv);
		classifiedAssessmentDetail.setIud(iud);
		// TODO P4ADEV-4025 uncomment classifiedAssessmentDetail.setClassificationLabel(label);
		switch (label) {
			case CASHED -> {break;}
				// TODO P4ADEV-4025 uncomment classifiedAssessmentDetail.setDateTreasury(classificationDate);
			case REPORTED -> {break;}
				// TODO P4ADEV-4025 uncomment classifiedAssessmentDetail.setDateReporting(classificationDate);
			case PAID -> {break;}
				// TODO P4ADEV-4025 uncomment classifiedAssessmentDetail.setDateReceipt(classificationDate);
			default -> throw new IllegalStateException("Unexpected value: " + label);
		}
		return classifiedAssessmentDetail;
	}

	private static CollectionModelAssessmentsDetail buildCollectionModelAssessmentsDetail() {
		CollectionModelAssessmentsDetail collectionModelAssessmentsDetail = new CollectionModelAssessmentsDetail();
		List<AssessmentsDetail> assessmentsDetailList = new ArrayList<>();

		AssessmentsDetail assessmentsDetail = new AssessmentsDetail();
		assessmentsDetail.setAssessmentDetailId(assessmentId);
		assessmentsDetail.setAssessmentId(assessmentId);
		assessmentsDetail.organizationId(organizationId);
		assessmentsDetail.setIuv(iuv);
		assessmentsDetail.setIud(iud);
		assessmentsDetail.setIur(iur);
		assessmentsDetail.setDebtPositionTypeOrgCode(debtPositionTypeOrgCode);
		assessmentsDetail.setDebtorFiscalCodeHash(debtorFiscalCodeHash.getBytes(StandardCharsets.UTF_8));
		assessmentsDetail.setSectionCode(sectionCode);
		assessmentsDetail.setAmountCents(amountCents);
		assessmentsDetail.setAmountSubmitted(amountSubmitted);
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
					classification.organizationId(organizationId);
					classification.setIuv(iuv);
					classification.setIud(iud);
					classification.setLabel(label);
					classificationList.add(classification);
				}
		);
		collectionModelClassification.setEmbedded(
				new PagedModelClassificationEmbedded(classificationList)
		);
		return collectionModelClassification;
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

	static Stream<CollectionModelAssessmentsDetail> testCollectionModelAssessmentsDetailsInputProvider() {
		CollectionModelAssessmentsDetail nullEmbedded = new CollectionModelAssessmentsDetail();
		nullEmbedded.setEmbedded(null);
		CollectionModelAssessmentsDetail nullAssessmentsDetailList = new CollectionModelAssessmentsDetail();
		nullAssessmentsDetailList.setEmbedded(new PagedModelAssessmentsDetailEmbedded());
		CollectionModelAssessmentsDetail emptyAssessmentsDetailList = new CollectionModelAssessmentsDetail();
		emptyAssessmentsDetailList.setEmbedded(new PagedModelAssessmentsDetailEmbedded(Collections.emptyList()));
		return Stream.of(
				null,
				nullEmbedded,
				nullAssessmentsDetailList,
				emptyAssessmentsDetailList
		);
	}

	static Stream<CollectionModelClassification> testCollectionModelClassificationsInputProvider() {
		CollectionModelClassification nullEmbedded = new CollectionModelClassification();
		nullEmbedded.setEmbedded(null);
		CollectionModelClassification nullClassificationList = new CollectionModelClassification();
		nullClassificationList.setEmbedded(new PagedModelClassificationEmbedded());
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
	@MethodSource("testCollectionModelAssessmentsDetailsInputProvider")
	void whenClassifyAssessmentWithNoAssessmentsDetailsThenNull(CollectionModelAssessmentsDetail collectionModelAssessmentsDetail) {
		//Given

		//region mock stubbing
		Mockito.when(assessmentsDetailService.findAssessmentsDetailByOrganizationIdAndIuvAndIud(organizationId, iuv, iud))
				.thenReturn(collectionModelAssessmentsDetail);
		//endregion

		//When
		AssessmentEventDTO actualResult = service.classifyAssessment(organizationId, iuv, iud);

		//Then
		Assertions.assertNull(actualResult);
	}

	@Test
	void whenClassifyAssessmentWithNoAssessmentThenNull() {
		//Given
		CollectionModelAssessmentsDetail collectionModelAssessmentsDetail = buildCollectionModelAssessmentsDetail();

		//region mock stubbing
		Mockito.when(assessmentService.findAssessment(assessmentId))
				.thenReturn(null);
		Mockito.when(assessmentsDetailService.findAssessmentsDetailByOrganizationIdAndIuvAndIud(organizationId, iuv, iud))
				.thenReturn(collectionModelAssessmentsDetail);
		//endregion

		//When
		AssessmentEventDTO actualResult = service.classifyAssessment(organizationId, iuv, iud);

		//Then
		Assertions.assertNull(actualResult);
	}

	@ParameterizedTest
	@MethodSource("testCollectionModelClassificationsInputProvider")
	void whenClassifyAssessmentWithNoClassificationsThenNull(CollectionModelClassification collectionModelClassification) {
		//Given
		CollectionModelAssessmentsDetail collectionModelAssessmentsDetail = buildCollectionModelAssessmentsDetail();

		//region mock stubbing
		Mockito.when(assessmentService.findAssessment(assessmentId))
				.thenReturn(assessments);
		Mockito.when(assessmentsDetailService.findAssessmentsDetailByOrganizationIdAndIuvAndIud(organizationId, iuv, iud))
				.thenReturn(collectionModelAssessmentsDetail);
		Mockito.when(classificationService.findAllByOrganizationIdAndIuvAndIud(organizationId, iuv, iud))
				.thenReturn(collectionModelClassification);
		//endregion

		//When
		AssessmentEventDTO actualResult = service.classifyAssessment(organizationId, iuv, iud);

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
		expectedResult.setAssessmentId(assessmentId);
		expectedResult.setOrganizationId(organizationId);
		expectedResult.setIuv(iuv);
		expectedResult.setIud(iud);
		expectedResult.setAssessmentsDetailList(List.of(classifiedAsCashedAssessmentDetails));
		//endregion

		//region mock stubbing
		Mockito.when(assessmentService.findAssessment(assessmentId))
				.thenReturn(assessments);
		Mockito.when(assessmentsDetailService.findAssessmentsDetailByOrganizationIdAndIuvAndIud(organizationId, iuv, iud))
				.thenReturn(collectionModelAssessmentsDetail);
		Mockito.when(classificationService.findAllByOrganizationIdAndIuvAndIud(organizationId, iuv, iud))
				.thenReturn(collectionModelClassification);
		Mockito.when(assessmentsDetailService.updateAssessmentsDetail(Mockito.eq(assessmentId), Mockito.any()))
				.thenReturn(classifiedAsCashedAssessmentDetails);
		Mockito.when(assessmentMapper.map(assessments, List.of(classifiedAsCashedAssessmentDetails)))
				.thenReturn(expectedResult);
		//endregion

		//When
		AssessmentEventDTO actualResult = service.classifyAssessment(organizationId, iuv, iud);

		//Then
		Assertions.assertEquals(expectedResult, actualResult);
		Mockito.verify(assessmentsDetailService).updateAssessmentsDetail(Mockito.any(), assessmentsDetailRequestBodyArgumentCaptor.capture());
		AssessmentsDetailRequestBody assessmentsDetailRequestBody = assessmentsDetailRequestBodyArgumentCaptor.getValue();
		/* TODO P4ADEV-4025 uncomment Assertions.assertEquals(CASHED, assessmentsDetailRequestBody.getClassificationLabel());
		Assertions.assertEquals(expectedDateTreasury, assessmentsDetailRequestBody.getDateTreasury());
		Assertions.assertNull(assessmentsDetailRequestBody.getDateReceipt());
		Assertions.assertNull(assessmentsDetailRequestBody.getDateReporting());*/
	}

	@Test
	void whenClassifyAssessmentAsReportedThenOk() {
		//Given
		CollectionModelAssessmentsDetail collectionModelAssessmentsDetail = buildCollectionModelAssessmentsDetail();
		CollectionModelClassification collectionModelClassification = buildCollectionModelClassification(RT_NO_IUD, IUF_TES_DIV_IMP, RT_NO_IUF);

		//region expected result
		AssessmentEventDTO expectedResult = new AssessmentEventDTO();
		expectedResult.setAssessmentId(assessmentId);
		expectedResult.setOrganizationId(organizationId);
		expectedResult.setIuv(iuv);
		expectedResult.setIud(iud);
		expectedResult.setAssessmentsDetailList(List.of(classifiedAsReportedAssessmentDetails));
		//endregion

		//region mock stubbing
		Mockito.when(assessmentService.findAssessment(assessmentId))
				.thenReturn(assessments);
		Mockito.when(assessmentsDetailService.findAssessmentsDetailByOrganizationIdAndIuvAndIud(organizationId, iuv, iud))
				.thenReturn(collectionModelAssessmentsDetail);
		Mockito.when(classificationService.findAllByOrganizationIdAndIuvAndIud(organizationId, iuv, iud))
				.thenReturn(collectionModelClassification);
		Mockito.when(assessmentsDetailService.updateAssessmentsDetail(Mockito.eq(assessmentId), Mockito.any()))
				.thenReturn(classifiedAsReportedAssessmentDetails);
		Mockito.when(assessmentMapper.map(assessments, List.of(classifiedAsReportedAssessmentDetails)))
				.thenReturn(expectedResult);
		//endregion

		//When
		AssessmentEventDTO actualResult = service.classifyAssessment(organizationId, iuv, iud);

		//Then
		Assertions.assertEquals(expectedResult, actualResult);
		Mockito.verify(assessmentsDetailService).updateAssessmentsDetail(Mockito.any(), assessmentsDetailRequestBodyArgumentCaptor.capture());
		AssessmentsDetailRequestBody assessmentsDetailRequestBody = assessmentsDetailRequestBodyArgumentCaptor.getValue();
		/* TODO P4ADEV-4025 uncomment Assertions.assertEquals(REPORTED, assessmentsDetailRequestBody.getClassificationLabel());
		Assertions.assertEquals(expectedDateReporting, assessmentsDetailRequestBody.getDateReporting());
		Assertions.assertNull(assessmentsDetailRequestBody.getDateTreasury());
		Assertions.assertNull(assessmentsDetailRequestBody.getDateReceipt());*/
	}

	@Test
	void whenClassifyAssessmentAsPaidThenOk() {
		//Given
		CollectionModelAssessmentsDetail collectionModelAssessmentsDetail = buildCollectionModelAssessmentsDetail();
		CollectionModelClassification collectionModelClassification = buildCollectionModelClassification(RT_NO_IUF, RT_NO_IUD);

		//region expected result
		AssessmentEventDTO expectedResult = new AssessmentEventDTO();
		expectedResult.setAssessmentId(assessmentId);
		expectedResult.setOrganizationId(organizationId);
		expectedResult.setIuv(iuv);
		expectedResult.setIud(iud);
		expectedResult.setAssessmentsDetailList(List.of(classifiedAsPaidAssessmentDetails));
		//endregion

		//region mock stubbing
		Mockito.when(assessmentService.findAssessment(assessmentId))
				.thenReturn(assessments);
		Mockito.when(assessmentsDetailService.findAssessmentsDetailByOrganizationIdAndIuvAndIud(organizationId, iuv, iud))
				.thenReturn(collectionModelAssessmentsDetail);
		Mockito.when(classificationService.findAllByOrganizationIdAndIuvAndIud(organizationId, iuv, iud))
				.thenReturn(collectionModelClassification);
		Mockito.when(assessmentsDetailService.updateAssessmentsDetail(Mockito.eq(assessmentId), Mockito.any()))
				.thenReturn(classifiedAsPaidAssessmentDetails);
		Mockito.when(assessmentMapper.map(assessments, List.of(classifiedAsPaidAssessmentDetails)))
				.thenReturn(expectedResult);
		//endregion

		//When
		AssessmentEventDTO actualResult = service.classifyAssessment(organizationId, iuv, iud);

		//Then
		Assertions.assertEquals(expectedResult, actualResult);
		Mockito.verify(assessmentsDetailService).updateAssessmentsDetail(Mockito.any(), assessmentsDetailRequestBodyArgumentCaptor.capture());
		AssessmentsDetailRequestBody assessmentsDetailRequestBody = assessmentsDetailRequestBodyArgumentCaptor.getValue();
		/* TODO P4ADEV-4025 uncomment Assertions.assertEquals(PAID, assessmentsDetailRequestBody.getClassificationLabel());
		Assertions.assertEquals(expectedDateReceipt, assessmentsDetailRequestBody.getDateReceipt());
		Assertions.assertNull(assessmentsDetailRequestBody.getDateTreasury());
		Assertions.assertNull(assessmentsDetailRequestBody.getDateReporting());*/
	}
}