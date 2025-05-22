package it.gov.pagopa.payhub.activities.connector.organization.client;

import it.gov.pagopa.payhub.activities.connector.organization.config.OrganizationApisHolder;
import it.gov.pagopa.pu.organization.dto.generated.OrgSilService;
import it.gov.pagopa.pu.organization.dto.generated.OrgSilServiceRequestBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
@Slf4j
public class OrgSilServiceClient {

  private final OrganizationApisHolder organizationApisHolder;

  public OrgSilServiceClient(OrganizationApisHolder organizationApisHolder) {
    this.organizationApisHolder = organizationApisHolder;
  }

  public OrgSilService createOrgSilService(OrgSilServiceRequestBody orgSilServiceRequestBody,
      String accessToken) {
    return organizationApisHolder.getOrgSilServiceEntityControllerApi(accessToken)
        .crudCreateOrgsilservice(orgSilServiceRequestBody);
  }

}
