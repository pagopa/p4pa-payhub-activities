package it.gov.pagopa.payhub.activities.activity.classifications;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.payhub.activities.dto.classifications.TransferSemanticKeyDTO;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentNoPII;
import it.gov.pagopa.pu.debtposition.dto.generated.Transfer;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Interface for defining an activity to process Transfer classifications.
 */
@ActivityInterface
public interface TransferClassificationActivity {

	/**
	 * Processes Transfer classification based on the provided parameters.
	 *
	 * @param transferSemanticKey the DTO containing semantic keys such as organization ID, IUV, IUR, and transfer index.
	 * @return Pair&lt;InstallmentNoPII,Transfer&gt; return pair of classified installment and transfer
	 */
	@ActivityMethod
	Pair<InstallmentNoPII,Transfer> classifyTransfer(TransferSemanticKeyDTO transferSemanticKey);
}