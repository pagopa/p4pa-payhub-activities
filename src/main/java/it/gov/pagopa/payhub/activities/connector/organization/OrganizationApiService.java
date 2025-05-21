package it.gov.pagopa.payhub.activities.connector.organization;

import it.gov.pagopa.pu.organization.dto.generated.OrganizationApiKeys;

public interface OrganizationApiService {

  void encryptAndSaveApiKey(Long organizationId, OrganizationApiKeys organizationApiKeys);

}
