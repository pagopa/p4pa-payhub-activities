package it.gov.pagopa.payhub.activities.activity.organization;

import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Lazy
@Slf4j
@Service
public class OrganizationBrokeredRetrieverActivityImpl implements OrganizationBrokeredRetrieverActivity {
	private final OrganizationService organizationService;

	public OrganizationBrokeredRetrieverActivityImpl(OrganizationService organizationService) {
		this.organizationService = organizationService;
	}

	@Override
	public List<Organization> retrieve(Long brokerId) {
		log.info("Retrieving organizations brokered by broker with id: {}", brokerId);
		return organizationService.getOrganizationsByBrokerId(brokerId);
	}
}
