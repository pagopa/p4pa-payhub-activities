package it.gov.pagopa.payhub.activities.connector.organization;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.organization.client.TaxonomyClient;
import it.gov.pagopa.pu.organization.dto.generated.PagedModelTaxonomy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

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

	@Override
	@Cacheable(key = "#organizationType + '-' + #macroAreaCode + '-' + #serviceTypeCode + '-' + #collectionReason",
			unless = "#result.getEmbedded().getTaxonomies().size() == 0")
	public PagedModelTaxonomy getTaxonomies(String organizationType, String macroAreaCode, String serviceTypeCode, String collectionReason, Integer page, Integer size, List<String> sort) {
		return taxonomyClient.findTaxonomies(organizationType, macroAreaCode, serviceTypeCode, collectionReason, page, size, sort,authnService.getAccessToken());
	}
}
