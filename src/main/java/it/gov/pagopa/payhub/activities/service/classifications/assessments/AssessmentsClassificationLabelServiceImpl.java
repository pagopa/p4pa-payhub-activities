package it.gov.pagopa.payhub.activities.service.classifications.assessments;

import it.gov.pagopa.pu.classification.dto.generated.Classification;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationLabel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;

@Lazy
@Slf4j
@Service
public class AssessmentsClassificationLabelServiceImpl implements AssessmentsClassificationLabelService {

	@Override
	public ClassificationLabel extractAssessmentsClassificationLabel(List<Classification> classificationList) {
		Set<ClassificationLabel> classificationLabelSet = new HashSet<>();
		for (Classification classification: classificationList) {
			switch (classification.getLabel()) {
				case RT_NO_IUF, RT_NO_IUD ->
						classificationLabelSet.add(ClassificationLabel.PAID);
				case RT_IUF, IUF_TES_DIV_IMP ->
						classificationLabelSet.add(ClassificationLabel.REPORTED);
				case RT_TES, RT_IUF_TES, IUD_RT_IUF_TES ->
						classificationLabelSet.add(ClassificationLabel.CASHED);
				default ->
						log.debug("Unused label for assessment classification: label {}, classificationId {}",
								classification.getLabel(), classification.getClassificationId()
						);
			}
		}
		return classificationLabelSet.stream()
				.max(Comparator.comparingInt(ClassificationLabel::ordinal))
				.orElse(ClassificationLabel.PAID); //at least assessment is paid
	}

}