package it.gov.pagopa.payhub.activities.connector.organization;

import it.gov.pagopa.payhub.activities.connector.auth.AuthnService;
import it.gov.pagopa.payhub.activities.connector.organization.client.OrganizationSilServiceClient;
import it.gov.pagopa.pu.organization.dto.generated.OrgSilServiceDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
@Slf4j
public class OrgSilServiceServiceImpl implements OrgSilServiceService {

    private final AuthnService authnService;
    private final OrganizationSilServiceClient organizationSilServiceClient;


    public OrgSilServiceServiceImpl(AuthnService authnService, OrganizationSilServiceClient organizationSilServiceClient) {
        this.authnService = authnService;
        this.organizationSilServiceClient = organizationSilServiceClient;
    }

    @Override
    public OrgSilServiceDTO createOrUpdateOrgSilService(OrgSilServiceDTO orgSilServiceDTO) {
        return organizationSilServiceClient.createOrUpdateOrgSilService(orgSilServiceDTO, authnService.getAccessToken());
    }
}
