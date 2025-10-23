package it.gov.pagopa.payhub.activities.service.classifications.assessments;

import it.gov.pagopa.pu.classification.dto.generated.Classification;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationLabel;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static it.gov.pagopa.pu.classification.dto.generated.ClassificationLabel.*;
import static it.gov.pagopa.pu.classification.dto.generated.ClassificationsEnum.*;

@ExtendWith(MockitoExtension.class)
class AssessmentsClassificationLabelServiceImplTest {

	private final AssessmentsClassificationLabelService service = new AssessmentsClassificationLabelServiceImpl();

	@Test
	void extractAssessmentsClassificationLabelThenCashed() {
		//Given
		List<Classification> classificationList = buildClassificationList(RT_NO_IUF, RT_IUF, RT_TES, TES_NO_IUF_OR_IUV);
		//When
		ClassificationLabel actualResult = service.extractAssessmentsClassificationLabel(classificationList);
		//Then
		Assertions.assertEquals(CASHED, actualResult);
	}

	@Test
	void extractAssessmentsClassificationLabelThenReported() {
		//Given
		List<Classification> classificationList = buildClassificationList(RT_NO_IUD, IUF_TES_DIV_IMP, RT_NO_IUF, TES_NO_IUF_OR_IUV);
		//When
		ClassificationLabel actualResult = service.extractAssessmentsClassificationLabel(classificationList);
		//Then
		Assertions.assertEquals(REPORTED, actualResult);
	}

	@Test
	void extractAssessmentsClassificationLabelThenPaid() {
		//Given
		List<Classification> classificationList = buildClassificationList(RT_NO_IUF, RT_NO_IUD, TES_NO_IUF_OR_IUV);
		//When
		ClassificationLabel actualResult = service.extractAssessmentsClassificationLabel(classificationList);
		//Then
		Assertions.assertEquals(PAID, actualResult);
	}

	@Test
	void extractAssessmentsClassificationLabelAllUnusedLabelsThenPaid() {
		//Given
		List<Classification> classificationList = buildClassificationList(TES_NO_MATCH, TES_NO_IUF_OR_IUV);
		//When
		ClassificationLabel actualResult = service.extractAssessmentsClassificationLabel(classificationList);
		//Then
		Assertions.assertEquals(PAID, actualResult);
	}

	@Test
	void extractAssessmentsClassificationLabelFromEmptyClassificationListThenPaid() {
		//Given
		List<Classification> classificationList = Collections.emptyList();
		//When
		ClassificationLabel actualResult = service.extractAssessmentsClassificationLabel(classificationList);
		//Then
		Assertions.assertEquals(PAID, actualResult);
	}

	private static List<Classification> buildClassificationList(ClassificationsEnum... labels) {
		List<Classification> classificationList = new ArrayList<>();
		Arrays.stream(labels).forEach(
				label -> {
					Classification classification = new Classification();
					classification.setLabel(label);
					classificationList.add(classification);
				}
		);
		return classificationList;
	}

}