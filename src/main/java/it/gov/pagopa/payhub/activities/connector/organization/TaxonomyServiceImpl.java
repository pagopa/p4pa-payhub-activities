package it.gov.pagopa.payhub.activities.connector.organization;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.organization.client.TaxonomyClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Slf4j
@Service
public class TaxonomyServiceImpl implements TaxonomyService {
	private final AuthnService authnService;
	private final TaxonomyClient taxonomyClient;

	public TaxonomyServiceImpl(AuthnService authnService, TaxonomyClient taxonomyClient) {
		this.authnService = authnService;
		this.taxonomyClient = taxonomyClient;
	}

	@Override
	public Integer syncTaxonomies() {
		log.debug("Synchronizing taxonomies");
		return taxonomyClient.fetchTaxonomies(authnService.getAccessToken());
	}
}
