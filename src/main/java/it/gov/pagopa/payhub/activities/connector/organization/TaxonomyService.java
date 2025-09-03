package it.gov.pagopa.payhub.activities.connector.organization;

import it.gov.pagopa.pu.organization.dto.generated.PagedModelTaxonomy;

import java.util.List;

/**
 * This interface provides methods that manage Taxonomies
 */
public interface TaxonomyService {
	Integer syncTaxonomies();

	PagedModelTaxonomy getTaxonomies(String organizationType, String macroAreaCode,
									 String serviceTypeCode, String collectionReason,
									 Integer page, Integer size, List<String> sort);

}
