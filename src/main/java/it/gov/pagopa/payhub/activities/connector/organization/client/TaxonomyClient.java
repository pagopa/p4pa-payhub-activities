package it.gov.pagopa.payhub.activities.connector.organization.client;

import it.gov.pagopa.payhub.activities.connector.organization.config.OrganizationApisHolder;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class TaxonomyClient {
	private final OrganizationApisHolder organizationApisHolder;

	public TaxonomyClient(OrganizationApisHolder organizationApisHolder) {
		this.organizationApisHolder = organizationApisHolder;
	}

	public Integer fetchTaxonomies(String accessToken) {
		return organizationApisHolder.getTaxonomyApi(accessToken).syncTaxonomies();
	}
}
