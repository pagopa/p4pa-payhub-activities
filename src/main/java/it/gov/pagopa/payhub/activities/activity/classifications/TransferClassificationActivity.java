package it.gov.pagopa.payhub.activities.activity.classifications;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.payhub.activities.dto.classifications.TransferSemanticKeyDTO;

/**
 * Interface for defining an activity to process Transfer classifications.
 */
@ActivityInterface
public interface TransferClassificationActivity {

	/**
	 * Processes Transfer classification based on the provided parameters.
	 *
	 * @param transferSemanticKey the DTO containing semantic keys such as organization ID, IUV, IUR, and transfer index.
	 */
	@ActivityMethod
	void classifyTransfer(TransferSemanticKeyDTO transferSemanticKey);
}