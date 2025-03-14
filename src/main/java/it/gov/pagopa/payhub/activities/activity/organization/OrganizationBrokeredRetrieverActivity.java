package it.gov.pagopa.payhub.activities.activity.organization;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import it.gov.pagopa.pu.organization.dto.generated.Organization;

import java.util.List;

/**
 * Activity Interface that is used to find all the organizations that are brokered by a specific broker
 */
@ActivityInterface
public interface OrganizationBrokeredRetrieverActivity {

	/**
	 * Method that has the logic to find all the organizations that are brokered by a specific broker
	 * @param brokerId the ID of the broker
	 * @return a list of the organizations that are brokered by the broker
	 */
	@ActivityMethod
	List<Organization> retrieveBrokeredOrganizations(Long brokerId);
}
