package it.gov.pagopa.payhub.activities.connector.organization;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.organization.client.OrgSilServiceClient;
import it.gov.pagopa.pu.organization.dto.generated.OrgSilService;
import it.gov.pagopa.pu.organization.dto.generated.OrgSilServiceRequestBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Slf4j
@Service
public class OrgSilServiceServiceImpl implements OrgSilServiceService {

  private final AuthnService authnService;
  private final OrgSilServiceClient orgSilServiceClient;

  public OrgSilServiceServiceImpl(AuthnService authnService,
      OrgSilServiceClient orgSilServiceClient) {
    this.authnService = authnService;
    this.orgSilServiceClient = orgSilServiceClient;
  }

  @Override
  public OrgSilService createOrgSilService(OrgSilServiceRequestBody orgSilServiceRequestBody) {
    return orgSilServiceClient.createOrgSilService(orgSilServiceRequestBody,
        authnService.getAccessToken());
  }
}
