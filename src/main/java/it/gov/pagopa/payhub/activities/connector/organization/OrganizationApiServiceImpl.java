package it.gov.pagopa.payhub.activities.connector.organization;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.organization.client.OrganizationApiClient;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationApiKeys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Slf4j
@Service
public class OrganizationApiServiceImpl implements OrganizationApiService {

  private final AuthnService authnService;
  private final OrganizationApiClient organizationApiClient;

  public OrganizationApiServiceImpl(AuthnService authnService,
      OrganizationApiClient organizationApiClient) {
    this.authnService = authnService;
    this.organizationApiClient = organizationApiClient;
  }

  @Override
  public void encryptAndSaveApiKey(Long organizationId, OrganizationApiKeys organizationApiKeys) {
    organizationApiClient.encryptAndSaveApiKey(organizationId, organizationApiKeys,
        authnService.getAccessToken());
  }
}
