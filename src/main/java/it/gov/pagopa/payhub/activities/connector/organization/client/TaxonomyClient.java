package it.gov.pagopa.payhub.activities.connector.organization.client;

import it.gov.pagopa.payhub.activities.connector.organization.config.OrganizationApisHolder;
import it.gov.pagopa.pu.organization.dto.generated.PagedModelTaxonomy;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

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

	public PagedModelTaxonomy findTaxonomies(String organizationType, String macroAreaCode,
											 String serviceTypeCode, String collectionReason,
											 Integer page, Integer size, List<String> sort, String accessToken) {
		return organizationApisHolder.getTaxonomySearchControllerApi(accessToken)
				.crudTaxonomiesFindTaxonomies(organizationType, macroAreaCode, serviceTypeCode, collectionReason, page, size, sort);
	}

}
