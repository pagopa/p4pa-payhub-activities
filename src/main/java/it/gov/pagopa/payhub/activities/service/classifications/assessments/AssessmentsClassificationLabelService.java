package it.gov.pagopa.payhub.activities.service.classifications.assessments;

import it.gov.pagopa.pu.classification.dto.generated.Classification;
import it.gov.pagopa.pu.classification.dto.generated.ClassificationLabel;
import java.util.List;


/**
 * This interface provides methods for extracting assessments classification label from Classification list.
 */
public interface AssessmentsClassificationLabelService {
	/**
	 * Extract assessments classification label from specified Classification list
	 *
	 * @param classificationList list of classifications
	 * @return the extracted assessments classification label
	 */
	ClassificationLabel extractAssessmentsClassificationLabel(List<Classification> classificationList);

}