package it.gov.pagopa.payhub.activities.activity.organization;

import it.gov.pagopa.pu.organization.dto.generated.Organization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Set;

@Lazy
@Slf4j
@Service
public class OrganizationBrokeredRetrieverActivityImpl implements OrganizationBrokeredRetrieverActivity {

	@Override
	public Set<Organization> retrieve(Long brokerId) {
		return Set.of();
	}
}
