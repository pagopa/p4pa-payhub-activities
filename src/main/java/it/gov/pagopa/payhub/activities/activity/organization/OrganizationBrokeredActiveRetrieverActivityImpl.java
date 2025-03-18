package it.gov.pagopa.payhub.activities.activity.organization;

import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Lazy
@Slf4j
@Service
public class OrganizationBrokeredActiveRetrieverActivityImpl implements OrganizationBrokeredActiveRetrieverActivity {
	private final OrganizationService organizationService;

	public OrganizationBrokeredActiveRetrieverActivityImpl(OrganizationService organizationService) {
		this.organizationService = organizationService;
	}

	@Override
	public List<Organization> retrieveBrokeredOrganizations(Long brokerId) {
		log.info("Retrieving organizations brokered by broker with id: {}", brokerId);
		return organizationService.getActiveOrganizationsByBrokerId(brokerId);
	}
}
